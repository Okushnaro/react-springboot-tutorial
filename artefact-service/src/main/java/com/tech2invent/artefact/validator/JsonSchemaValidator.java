package com.tech2invent.artefact.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;


import com.networknt.schema.uri.URIFetcher;
import com.networknt.schema.uri.URLFetcher;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import com.tech2invent.artefact.exception.CollectionItemsSchemaValidationException;
import com.tech2invent.artefact.exception.CollectionMetaSchemaValidationException;
import com.tech2invent.artefact.model.ArtefactObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.*;

@Slf4j
@Component
public class JsonSchemaValidator{

    /*
    * TODO: Add custom URIFetcher to re-rout from local file to a web URI.
    *  See: https://developer-docs.amazon.com/sp-api/docs/product-type-definition-meta-schema-v1-example-java
    *
    * */

    @Value("${validation.meta-schema.url}")
    private String metaSchemaUrl;

    @Value( "${validation.meta-schema.filepath}")
    private String metaSchemaFilePath;
    @Value("classpath:default-meta-schema.json")
    private Resource metaSchemaFile;

    //Dependencies
    private ObjectMapper objectMapper;

    private JsonNode defaultMetaSchema;

    public JsonSchemaValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private void initMetaSchema(){

        //Initializing default Meta schema from resource file: default-meta-schema.json
        try {
            log.debug("::JAVA_CLASS_INIT_SECTION Reading default meta schema json file {}, description {}. " +
                    "NOTE. Use --validation-meta-schema-url app arguments or validation.meta-schema.url property " +
                    "to define url to the actual meta schema for validation." +
                    " Also, --validation-meta-schema-filepath or validation.meta-schema.filepath " +
                    "or a meta schema json file path. The default meta schema might be outdated!",
                    metaSchemaFile.getFilename(),
                    metaSchemaFile.getDescription()
                    );
            defaultMetaSchema = objectMapper.readTree(metaSchemaFile.getInputStream());
        } catch (IOException e) {
            log.error("The default meta schema can't be parsed from default file {}", metaSchemaFile.getFilename());
            throw new RuntimeException(e);
        }


    }

    /**
     * Validates artefact entries with the artefact Schema defined in an artefeact collection.
     * @param artefactCollection An artefact collection to validate artefact items.
     * @throws CollectionMetaSchemaValidationException
     */
    public void validateCollectionArtefactsBySchema(
            ArtefactCollectionDto artefactCollection
    ) throws CollectionMetaSchemaValidationException
    {

        //Getting Schema from an ArtefactCollection
        final JsonNode schemaNode = artefactCollection.getSchema();
        log.debug(
                "::validateCollectionArtefactsBySchema From Artefact collection '{} 'getting artefact schema: {}",
                artefactCollection.getName(),
                artefactCollection.getSchema().toPrettyString());

        //Detecting Schema version from schemaNode property of an ArtefactCollection.
        SpecVersion.VersionFlag schemaVersion = SpecVersionDetector.detect(schemaNode);
        log.debug(
                "::validateCollectionArtefactsBySchema Schema version '{}' detected from collection '{}'",
                schemaVersion.name(),
                artefactCollection.getName());

        //Obtaining schema factory using version detected in artefact collection schema field
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(schemaVersion);

        //Schema object itself
        JsonSchema collectionSchema = schemaFactory.getSchema(schemaNode);

        //Map container for validation error message set for each artefact entity as key.
        Map<String, Set<ValidationMessage>> artefactErrorMessageMap = new HashMap<>();

        //Run validation for each artefact object in the artefact collection
        artefactCollection.getItems().forEach(dtoObject -> {
            ArtefactObject artefactObject = objectMapper.convertValue(dtoObject, ArtefactObject.class);
            log.debug("::validateCollectionArtefactsBySchema Artefact: {} ", artefactObject.toString());
            final JsonNode artefactNode = objectMapper.convertValue(artefactObject.getDetails(), JsonNode.class);
            log.debug(
                    "::validateCollectionArtefactsBySchema Validating artefact '{}' with id '{}'...",
                    artefactNode.toPrettyString(),
                    artefactObject.getId());
            Set<ValidationMessage> tmpResult = validateObjectNode(
                    artefactNode,
                    collectionSchema
                    );
            //Storing validation error messages for a particular artefact in an artefact collection
            if(!tmpResult.isEmpty()){
                String artefactId = artefactObject.getId();
                artefactErrorMessageMap.put(artefactId, tmpResult);
                log.debug(
                        "::validateCollectionArtefactsBySchema Artefact id {} has {} errors. Error messages: {}",
                        artefactId,
                        tmpResult.size(),
                        tmpResult
                        );
            }
        });

        log.debug(
                "::validateCollectionArtefactsBySchema Found {} invalid of total {} artefacts.",
                artefactErrorMessageMap.size(),
                artefactCollection.getItems().size());

        //Throwing custom exception if any validation errors in at least one of artefact entries.
        if(artefactErrorMessageMap.size() > 0)
            throw new CollectionItemsSchemaValidationException(artefactCollection.getName(), artefactErrorMessageMap);

    }


    public void validateCollectionSchemaByMetaSchema(String schemaStr, String collectionName) throws CollectionMetaSchemaValidationException {
        log.debug("::validateSchemaByMetaSchema called with params: schemaStr={}, collectionName={}", schemaStr, collectionName);
        initMetaSchema();

        // URIFetcher to route meta-schema references to local copy.
        URIFetcher uriFetcher = uri -> {
            // Use the local copy of the meta-schema instead of retrieving from the web.
            if (defaultMetaSchema.get("$id").toString().equalsIgnoreCase(uri.toString())) {
                return metaSchemaFile.getInputStream();
            }

            // Default to the existing fetcher for other schemas.
            return new URLFetcher().fetch(uri);
        };


        SpecVersion.VersionFlag schemaVersion = SpecVersionDetector.detect(defaultMetaSchema);

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(schemaVersion);
        // Build the JsonSchemaFactory.

        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode;

        Set<ValidationMessage> validationResult = new HashSet<>();
        JsonSchema metaSchema = null;
        try {
            schemaNode = mapper.readTree(schemaStr);

            metaSchema  = schemaFactory.getSchema(metaSchemaFile.getInputStream());
            log.debug("::validateSchemaByMetaSchema using meta-schema: {}", metaSchema);
            validationResult = metaSchema.validate(schemaNode);

            if(!validationResult.isEmpty()){
                log.error("::validateSchemaByMetaSchema Found {} meta-schema validation errors in the collection '{}'",
                        validationResult.size(),
                        collectionName
                );
                throw new CollectionMetaSchemaValidationException(collectionName, validationResult );

            }
        } catch (IOException  e) {
            log.error("::validateSchemaByMetaSchema During meta-schema validation an error has occurred: {}", e.getMessage());
            e.printStackTrace();
        }

        log.debug("::validateSchemaByMetaSchema found {} validation errors.", validationResult.size());

      }

      private Set<ValidationMessage> validateObjectNode(JsonNode artefactNode, JsonSchema collectionSchema){
          return collectionSchema.validate(artefactNode);
      }
}
