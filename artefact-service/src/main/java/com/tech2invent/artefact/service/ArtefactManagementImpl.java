package com.tech2invent.artefact.service;

import com.tech2invent.artefact.model.ArtefactCollection;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import com.tech2invent.artefact.validator.JsonSchemaValidator;
import com.tech2invent.artefact.repository.ArtefactCollectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ArtefactManagementImpl implements ArtefactManagementI{



    private ArtefactCollectionsRepository collectionRepo;
    private JsonSchemaValidator jsonSchemaValidator;


    @Autowired
    public ArtefactManagementImpl(ArtefactCollectionsRepository collectionRepo, JsonSchemaValidator jsonSchemaValidator) {
        this.collectionRepo = collectionRepo;
        this.jsonSchemaValidator = jsonSchemaValidator;
    }

    @Override
    public List<ArtefactCollectionDto> getAllArtefactCollections() {
        return collectionRepo.getCollections().entrySet().stream().map(Map.Entry::getValue).toList();

    }

    @Override
    public void createArtefactCollection(ArtefactCollectionDto collection){
        //Validating schema using meta-schemas
        jsonSchemaValidator.validateCollectionSchemaByMetaSchema(collection.getSchema().toString(), collection.getName());
        //Using Collection schema to validate artefacts if such given.
        if(!collection.getItems().isEmpty()){
            jsonSchemaValidator.validateCollectionArtefactsBySchema(collection);
        }

        collectionRepo.addCollection(collection);
    }

    @Override
    public ArtefactCollection getArtefactCollection(String collectionName) {
        return collectionRepo.getCollection(collectionName);
    }

    @Override
    public ArtefactCollectionDto updateArtefactCollection(ArtefactCollectionDto artefactCollection) {
        return collectionRepo.addCollection(artefactCollection);
    }

    @Override
    public void deleteArtefactCollection(String collectionName) {
        collectionRepo.removeCollection(collectionName);
    }
}
