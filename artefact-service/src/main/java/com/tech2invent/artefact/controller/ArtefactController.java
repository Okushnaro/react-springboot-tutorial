package com.tech2invent.artefact.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.tech2invent.artefact.exception.ArtefactSchemaValidationErrorsException;
import com.tech2invent.artefact.model.ArtefactObject;
import com.tech2invent.artefact.service.ArtefactCollectionI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1.0")
public class ArtefactController {
    //Dependencies
    private ArtefactCollectionI artefactCollectionService;
    private ObjectMapper objectMapper;

    @GetMapping("/artefacts/{collectionName}/")
    ResponseEntity<List<Object>> getAllArtefacts(@PathVariable String collectionName){
        List<Object> responseLst = new ArrayList<>();
        log.debug(":: getAllArtefacts called with params: collectionName={}", collectionName);
        List<ArtefactObject> items = artefactCollectionService.getAllObjects(collectionName);

        for (ArtefactObject artefact: items) {
            responseLst.add(artefact.getDetails());

        }
        return new ResponseEntity<>(responseLst, HttpStatus.OK);
    }

    @GetMapping("/artefacts/{collectionName}/{artefactId}")
    ResponseEntity<Object> getCollectionArtefact(
            @PathVariable String collectionName,
            @PathVariable String artefactId){
        return new ResponseEntity<>(
                artefactCollectionService.getCollectionItem(collectionName, artefactId).getDetails(),
                HttpStatus.OK
        );
    }

    @PostMapping(
            value = "/artefacts/{collectionName}/",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    void addCollectionArtefact(
            @PathVariable String collectionName,
            @RequestBody String artefact) throws JsonProcessingException {
        // create instance of the ObjectMapper class

        JsonSchema schema = artefactCollectionService.getCollectionSchema(collectionName);
        log.info("Collection: " + collectionName + ", schema: " + schema.toString());
        JsonNode json = objectMapper.readTree(artefact);
        ArtefactObject artefactObject = objectMapper.readValue(artefact, ArtefactObject.class);
        Set<ValidationMessage> validationResult = schema.validate(json);
        if(!validationResult.isEmpty()){
            throw new ArtefactSchemaValidationErrorsException(collectionName, artefact, validationResult);
        }
        artefactCollectionService.addCollectionItem(collectionName, artefactObject);
    }

}
