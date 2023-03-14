package com.tech2invent.artefact.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.networknt.schema.*;
import com.tech2invent.artefact.exception.ArtefactSchemaValidationErrorsException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SchemaDeserializer extends JsonDeserializer<JsonSchema> {

/*    @Value("${validation.meta-schema.URI}")
    private String META_SCHEMA_URI;

    @Value("classpath:meta-schema.json")
    private Resource META_SCHEMA_FILE;*/

    /*
    * Schema validation using meta-schema during JsonSchema mapping.
    * https://stackoverflow.com/questions/51001546/catch-a-deserialization-exception-before-a-controlleradvice
    *
    * Need to add @Order(Ordered.HIGHEST_PRECEDENCE) annotation on top of ControllerAdvice class.
    *
    *
    *
    * */



    @Override
    public JsonSchema deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, ArtefactSchemaValidationErrorsException {
        log.debug("::deserialize with params:");
        //ObjectMapper objectMapper = new ObjectMapper();
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        log.debug("::deserialize meta-schema factory: {}", schemaFactory.toString());
        JsonNode node = p.getCodec().readTree(p);
        log.debug("::deserialize node: {}", node.toString());
        // 1. Get Meta schema
        // 2. Validate Collection schema
        // 3. Throw
        //JsonSchema schema = schemaFactory.getSchema(node);
        //log.debug("::deserialize schema factory: {}", schema.toString());

/*        //JsonSchema metaSchema = null;
        Set<ValidationMessage> validationResult = new HashSet<>();
        JsonSchema metaSchema = null;
        try {
            metaSchema  = schemaFactory.getSchema(META_SCHEMA_FILE.getInputStream());
            validationResult = metaSchema.validate(node);
        }
        catch (RuntimeException e){
            log.error("::deserialize During meta-schema validation an error has occurred: {}", e.getMessage());
            e.printStackTrace();
        }
        //schemaFactory.getSchema()
        log.debug("Validating collection schema object with meta schema: {}", Optional.of(metaSchema).get());




        if(!validationResult.isEmpty()){
            log.error(
                    "Given schema: {} fails meta-schema validation with errors {}",
                    node.asText(),
                    validationResult.stream()
                            .map(x -> x.getMessage())
                            .collect(Collectors.joining("\n")));
            throw new ArtefactSchemaValidationErrorsException("SOme collection", "some schema", validationResult);
        }
        validateMetaSchema(validationResult);*/
        return schemaFactory.getSchema(node);

    }

    private void metaSchemaValidator(){


     /*   List<Format> BUILTIN_FORMATS = new ArrayList<Format>(JsonMetaSchema.COMMON_BUILTIN_FORMATS);

        JsonMetaSchema myJsonMetaSchema = new JsonMetaSchema.Builder(META_SCHEMA_URI)
                .idKeyword("$id")
                .addFormats(BUILTIN_FORMATS)
                .addKeywords(ValidatorTypeCode.getNonFormatKeywords(SpecVersion.VersionFlag.V201909))
                // keywords that may validly exist, but have no validation aspect to them
                .addKeywords(Arrays.asList(
                        new NonValidationKeyword("$schema"),
                        new NonValidationKeyword("$id"),
                        new NonValidationKeyword("title"),
                        new NonValidationKeyword("description"),
                        new NonValidationKeyword("default"),
                        new NonValidationKeyword("definitions"),
                        new NonValidationKeyword("$defs")  // newly added in 2018-09 release.
                ))
                // add your custom keyword
                *//*.addKeyword(new GroovyKeyword())*//*
                .build();

        JsonSchemaFactory myJsonSchemaFactory = new JsonSchemaFactory.Builder().defaultMetaSchemaURI(myJsonMetaSchema.getUri())
                .addMetaSchema(myJsonMetaSchema)
                .build();*/
        //myJsonSchemaFactory.


    }

    private void validateMetaSchema(Set<ValidationMessage> validationResult) throws ArtefactSchemaValidationErrorsException{
        if(!validationResult.isEmpty()){
            validationResult
                    .stream()
                    .forEach(
                            x -> System.out.println(

                                    "Message: " + x.getMessage() +
                                            ", code: " + x.getCode() +
                                            ", path: " + x.getPath() +
                                            ", type: " + x.getType() +
                                            ", arguments: " + Arrays.stream(x.getArguments()).collect(Collectors.joining("; ")) +
                                            ", details: " + x.getDetails()
                            ));
            throw new ArtefactSchemaValidationErrorsException("SOme collection", "some schema", validationResult);
        }
    }
}
