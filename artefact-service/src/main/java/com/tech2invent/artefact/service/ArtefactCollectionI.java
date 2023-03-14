package com.tech2invent.artefact.service;

import com.networknt.schema.JsonSchema;
import com.tech2invent.artefact.model.ArtefactObject;

import java.util.List;

public interface ArtefactCollectionI {

    List<ArtefactObject> getAllObjects(String collectionName);

    ArtefactObject addCollectionItem(String collectionName, ArtefactObject item);

    ArtefactObject getCollectionItem(String collectionName, String itemId);

    ArtefactObject updateCollectionItem(String collectionName, ArtefactObject item);

    void deleteCollectionItem(String collectionName, String itemId);

    JsonSchema getCollectionSchema(String collectionName);

}
