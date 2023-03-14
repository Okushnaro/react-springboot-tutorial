package com.tech2invent.artefact.service;


import com.tech2invent.artefact.model.ArtefactCollection;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;

import java.util.List;

public interface ArtefactManagementI {
    List<ArtefactCollectionDto> getAllArtefactCollections();

    void createArtefactCollection(ArtefactCollectionDto collection);

    ArtefactCollection getArtefactCollection(String collectionName);

    ArtefactCollectionDto updateArtefactCollection(ArtefactCollectionDto collection);
    void deleteArtefactCollection(String collectionName);
}
