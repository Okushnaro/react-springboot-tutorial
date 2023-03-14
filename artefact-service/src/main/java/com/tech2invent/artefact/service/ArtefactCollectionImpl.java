package com.tech2invent.artefact.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.tech2invent.artefact.model.ArtefactObject;
import com.tech2invent.artefact.repository.ArtefactCollectionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ArtefactCollectionImpl implements ArtefactCollectionI{

    private ArtefactCollectionsRepository collectionRepo;

    public ArtefactCollectionImpl(@Autowired  ArtefactCollectionsRepository collectionRepo) {
        this.collectionRepo = collectionRepo;
    }

    @Override
    public List<ArtefactObject> getAllObjects(String collectionName) {
        return collectionRepo.getCollection(collectionName).getItems().values().stream().toList();
    }

    @Override
    public ArtefactObject addCollectionItem(String collectionName, ArtefactObject item) {

        return collectionRepo.addOrUpdateCollectionItem(collectionName, item);
    }

    @Override
    public ArtefactObject getCollectionItem(String collectionName, String itemId) {
        return collectionRepo.getCollectionItem(collectionName, itemId);
    }

    @Override
    public ArtefactObject updateCollectionItem(String collectionName, ArtefactObject item) {
        return addCollectionItem(collectionName, item);
    }

    @Override
    public void deleteCollectionItem(String collectionName, String itemId) {
        collectionRepo.removeCollectionItem(collectionName, itemId);
    }

    @Override
    public JsonSchema getCollectionSchema(String collectionName) {
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        ObjectMapper mapper = new ObjectMapper();
        String schemaStr = collectionRepo.getCollection(collectionName).getSchema().toString();
        JsonSchema schemaSchema;
        try {
            JsonNode actualObj = mapper.readTree(schemaStr);
            schemaSchema = schemaFactory.getSchema(actualObj);

        } catch (JsonProcessingException e) {
            //TODO:Define dedicated exception for case with NO Schema in a collection
            throw new RuntimeException(e);
        }
        return schemaSchema;
    }
}
