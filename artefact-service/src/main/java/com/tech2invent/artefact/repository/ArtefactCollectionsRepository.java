package com.tech2invent.artefact.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech2invent.artefact.exception.ArtefactNotFoundException;
import com.tech2invent.artefact.exception.CollectionNotFoundException;
import com.tech2invent.artefact.model.ArtefactCollection;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import com.tech2invent.artefact.model.ArtefactObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



@Slf4j
@Repository
public class ArtefactCollectionsRepository {

    private ConcurrentMap<String, ArtefactCollection> collections = new ConcurrentHashMap<>();

    ObjectMapper oMapper;

    @Autowired
    public ArtefactCollectionsRepository(ObjectMapper oMapper) {
        this.oMapper = oMapper;
    }

    public ConcurrentMap<String, ArtefactCollectionDto> getCollections() {
        ConcurrentHashMap<String, ArtefactCollectionDto> dtoCollections = new ConcurrentHashMap<>();

        //Re-assemble map with ArtefactCollectionDto instead of repository native ArtefactCollection
        collections.forEach((colName, artefactCollection) ->{
            log.debug("::getCollections Processing collection '{}', item: {} ", colName, artefactCollection.toString());
            List<Object> dtoObjects = new ArrayList<>();
            //Converting repository native ArtefactObject into dto serializable Object
            artefactCollection.getItems().forEach((artefactId, artefactObject) ->{

                Map<String, Object> tmpObj = new LinkedHashMap<>();
                tmpObj.put("id", artefactObject.getId());
                tmpObj.putAll(artefactObject.getDetails());
                final var dtoObject = oMapper.convertValue(tmpObj, Object.class);
                dtoObjects.add(dtoObject);
                log.debug("::getCollections Converted artefact object: '{}' into dto object: {} ", artefactObject.toString(), dtoObject.toString());
            });
            dtoCollections.put(colName, new ArtefactCollectionDto(colName, artefactCollection.getSchema(),dtoObjects));
        });
        return dtoCollections;
    }

    //TODO: Add logic to assign object ids.
    public void setCollections(ConcurrentMap<String, ArtefactCollection> collections) {
        this.collections = collections;

    }

    //TODO: Return original(the one given as input) ArtefactCollectionDTO with not updated id
    //  doesn't make sense! Refactor such an related logic to use custom Serializer/Deserializer
    //  to avoid such complexity.
    public ArtefactCollectionDto addCollection(ArtefactCollectionDto collectionDto){
        //Initializing map for ArtefactCollection to form items with key/value data structure
        ConcurrentHashMap<String, ArtefactObject> items = new ConcurrentHashMap<>();

        log.debug("::addCollection Processing Collection '{}' '{}' artefacts...", collectionDto.getName(), collectionDto.getItems().size());
        //Dealing with artefact Ids and perform List -> Map mapping for artefact items
        collectionDto.getItems().forEach(plainObject ->{
            ArtefactObject artefactObject = oMapper.convertValue(plainObject, ArtefactObject.class);
            //Retrieving artefact if if such
            var id = artefactObject.getId();
            log.debug("::addCollection Artefact with id '{}'.", id);
            //Generating id if not assigned
            if (id == null) {
                id = UUID.randomUUID().toString();
                artefactObject.setId(id);
                log.debug("::addCollection Generated Artefact id '{}'.", id);
            }

            Map<String, Object> tmpObj = artefactObject.getDetails();
            tmpObj.put("id", id);
            log.debug("::addCollection Storing artefact id '{}', data: {}",id, artefactObject.getDetails());
            //Forming Mp base artefact items structure
            items.put(id, artefactObject);

        });

        //Creating repository native artefact collection structure
        ArtefactCollection collection = new ArtefactCollection(collectionDto.getName(), collectionDto.getSchema(), items);

        //Storing repository native artefact collection after mapping
        collections.put(collectionDto.getName(), collection);
        return collectionDto;
    }

    public ArtefactCollection getCollection(String collectionName){
        return collections.get(collectionName);
    }

    public void removeCollection(String collectionName){
        collections.remove(collectionName);

    }

    public ArtefactObject addOrUpdateCollectionItem(String collectionName, ArtefactObject artefactItem){
        //Throwing exception if addressed collection does not exist
        final var funcName = "::addOrUpdateCollectionItem";
        if(!collections.containsKey(collectionName)){
            log.error(funcName + " collection with name '{}' not found!");
            throw new CollectionNotFoundException(collectionName);
        }


        log.debug(funcName + " collection '{}', adding item: {}",collectionName, artefactItem);
        var id = artefactItem.getId();
        //Generating id if not assigned
        if (id == null) {
            id = UUID.randomUUID().toString();

            artefactItem.setId(id);
            log.debug(funcName + " Generated artefact id '{}', artefact object: {}", id, artefactItem);
        }

        //Update if exists
        if(collections.get(collectionName).getItems().containsKey(id)) {
            log.warn(
                    funcName + " artefact with id '{}' and data: {} exists, updating collection with object: {}",
                    id,
                    collections.get(collectionName).getItems().get(id),
                    artefactItem
            );
        }
        collections.get(collectionName).getItems().putIfAbsent(id, artefactItem);

        return artefactItem;
    }

    public ArtefactObject getCollectionItem(String collectionName, String itemId){
        if(!collections.get(collectionName).getItems().containsKey(itemId)) {
            log.error(
                    "::getCollectionItem artefact with id '{}' in collection ith name '{}' not found",
                    itemId,
                    collectionName
            );
            throw new ArtefactNotFoundException(itemId, collectionName);
        }
        return collections.get(collectionName).getItems().get(itemId);
    }

    public void removeCollectionItem(String collectionName, String itemId){
        collections.get(collectionName).getItems().remove(itemId);
    }
}
