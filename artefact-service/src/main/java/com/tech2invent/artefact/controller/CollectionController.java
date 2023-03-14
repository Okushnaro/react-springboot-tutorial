package com.tech2invent.artefact.controller;

import com.tech2invent.artefact.model.ArtefactCollection;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import com.tech2invent.artefact.service.ArtefactManagementI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1.0")
public class CollectionController {
    private ArtefactManagementI artefactManagementService;

    @Autowired
    public CollectionController(ArtefactManagementI artefactManagementService) {
        this.artefactManagementService = artefactManagementService;

    }

    @GetMapping("/collections/{collectionName}/")
    ResponseEntity<ArtefactCollection> getCollection(@PathVariable String collectionName){
        return new ResponseEntity<>(artefactManagementService.getArtefactCollection(collectionName), HttpStatus.OK);
    }

    @GetMapping(value = "/collections/",
                produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<List<ArtefactCollectionDto>> getAllCollections(){
        return new ResponseEntity<>(artefactManagementService.getAllArtefactCollections(), HttpStatus.OK);
    }


    @PostMapping(
            value = "/collections/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    void createCollection(@RequestBody ArtefactCollectionDto collection) throws Exception {
        log.debug("::createCollection2 Request body: {}", collection);

        //ArtefactCollectionMapper.
        artefactManagementService.createArtefactCollection(collection);
    }





}
