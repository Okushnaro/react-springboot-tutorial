package com.tech2invent.artefact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import com.tech2invent.artefact.service.ArtefactCollectionImpl;
import com.tech2invent.artefact.service.ArtefactManagementI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class InitialDataLoader implements ApplicationRunner {
    @Value("${initial.collection.data}")
    private String initDataFilePath;


    private ArtefactManagementI artefactManagementService;
    private ObjectMapper oMapper;

    @Autowired
    public InitialDataLoader(@Lazy ArtefactManagementI artefactManagementService, ObjectMapper oMapper) {
        this.artefactManagementService = artefactManagementService;
        this.oMapper = oMapper;
    }

    void loadInitialCollectionData() throws IOException {
        log.info("::loadInitialCollectionData ########### Loading initial collection data using file: {} ###########", initDataFilePath);
        FileSystemResource initDataResource = new FileSystemResource(initDataFilePath);
        log.debug("::loadInitialCollectionData File '{}' loaded, file details: {}",
                initDataResource.getFilename(),
                initDataResource.getDescription()
        );
        ArtefactCollectionDto[] artefactCollections = oMapper.readValue(
                initDataResource.getInputStream(),
                ArtefactCollectionDto[].class
        );
        log.info("::loadInitialCollectionData Deserialized '{}' initial collection(s).",
                artefactCollections.length);

        Arrays.asList(artefactCollections).stream().forEach( collection -> {
            artefactManagementService.createArtefactCollection(collection);
            log.info("::loadInitialCollectionData Loaded collection '{}' with {} artefacts.",
                    collection.getName(),
                    collection.getItems().size() );
        });
        log.info("::loadInitialCollectionData ########### Loaded all {} collection(s). ###########", artefactCollections.length);



    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //
        loadInitialCollectionData();
    }
}
