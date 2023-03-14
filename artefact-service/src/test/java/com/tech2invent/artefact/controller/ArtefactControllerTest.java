package com.tech2invent.artefact.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import com.tech2invent.artefact.model.ArtefactCollection;
import com.tech2invent.artefact.repository.ArtefactCollectionsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;


class ArtefactControllerTest {

    private String goodsCollection = """
            {
            	"name": "Goods",
            	"schema": {
            		"$schema": "https://json-schema.org/draft/2019-09/schema#",
            		"$id+": "http://my-paintings-api.com/schemas/painting-schema.json",
            		"type": "object",
            		"title": "Goods",
            		"description": "Different goods",
            		"additionalProperties": true,
            		"required": ["name", "brand", "price", "image"],
            		"properties": {
            			"name": {
            				"type": "string",
            				"description": "Painting name"
            			},
            			"brand": {
            				"type": "string",
            				"maxLength": 50,
            				"description": "Brand name"
            			},
            			"price": {
            				"type": "string",
            				"description": "Good price"
            			},
            			"image": {
            				"type": "string",
            				"description": "relative path to a media file"
            			}
            		}
            	},
            	"items": {
                  "ef91832e-82ec-11ed-a1eb-0242ac120002": {
              		"name": "good1",
              		"brand": "brand1",
              		"price": "13.5",
              		"image": "/goods-images/1"
                    },
                      "ef9186b2-82ec-11ed-a1eb-0242ac120002": {
                        "name": "good2",
                        "brand": "brand2",
                        "price": "32.72",
                        "image": "/goods-images/1"
                    },
                      "ef918b30-82ec-11ed-a1eb-0242ac120002": {
                        "name": "good3",
                        "brand": "brand2",
                        "price": "35.22",
                        "image": "/goods-images/3"
                    }
                }
            }
            """;

    private ArtefactCollectionsRepository artefactCollectionsRepository;
    private ObjectMapper oMapper;

    @BeforeEach
    public void init(){
        this.oMapper = new ObjectMapper();
        this.artefactCollectionsRepository = new ArtefactCollectionsRepository(this.oMapper);

    }


    @Test
    void ShouldReturnAllArtefacts() throws JsonProcessingException {
        //Given
        ArtefactCollection artefactCollection = oMapper.readValue(goodsCollection, ArtefactCollection.class);
        ConcurrentHashMap<String, ArtefactCollection> collectionMap = new ConcurrentHashMap<>();
        collectionMap.put("Goods", artefactCollection);
        //When
        this.artefactCollectionsRepository.setCollections(collectionMap);
        //Then
        ConcurrentMap<String, ArtefactCollectionDto> dut = artefactCollectionsRepository.getCollections();
        Assert.isTrue(!dut.isEmpty(), "Items must not be empty!");
    }

    @Test
    void getCollectionArtefact() {
    }

    @Test
    void addCollectionArtefact() {
    }
}