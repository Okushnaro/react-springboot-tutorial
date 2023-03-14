package com.tech2invent.artefact.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import com.tech2invent.artefact.exception.ArtefactNotFoundException;
import com.tech2invent.artefact.exception.CollectionNotFoundException;
import com.tech2invent.artefact.model.ArtefactCollection;
import com.tech2invent.artefact.model.ArtefactObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
class ArtefactCollectionsRepositoryTest {
    private String goodsRawCollection = """
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

    private String goodsDTOCollection = """
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
            	"items": [
                  {
              		"name": "good1",
              		"brand": "brand1",
              		"price": "13.5",
              		"image": "/goods-images/1"
                    },
                  {
                    "name": "good2",
                    "brand": "brand2",
                    "price": "32.72",
                    "image": "/goods-images/1"
                    },
                  {
                    "name": "good3",
                    "brand": "brand2",
                    "price": "35.22",
                    "image": "/goods-images/3"
                  }
                ]
            }
            """;

    private ArtefactCollectionsRepository artefactCollectionsRepository;
    private ObjectMapper oMapper;

    @BeforeEach
    public void init(){
        oMapper = new ObjectMapper();
        artefactCollectionsRepository = new ArtefactCollectionsRepository(oMapper);
    }

    @Test
    void ShouldReturnAllCollections() throws JsonProcessingException {
        //Given
        setRepositoryCollections(goodsRawCollection);
        //Then
        ConcurrentMap<String, ArtefactCollectionDto> dut = artefactCollectionsRepository.getCollections();
        Assert.isTrue(!dut.isEmpty(), "Items must not be empty!");
    }

    @Test
    void ShouldDeleteCollectionByName() throws JsonProcessingException {
        //Given
        setRepositoryCollections(goodsRawCollection);
        //When
        artefactCollectionsRepository.removeCollection("Goods");
        ConcurrentMap<String, ArtefactCollectionDto> dut = artefactCollectionsRepository.getCollections();
        //Then
        Assert.isTrue(dut.isEmpty(), "Must be 0 collections!");
    }

    @Test
    void ShouldAddCollectionByCollectionDTO() throws JsonProcessingException {
        //Given
        //Mapping Collection json string to the ArtefactCollection object
        ArtefactCollectionDto artefactCollection = oMapper.readValue(goodsDTOCollection, ArtefactCollectionDto.class);
        //When
        ArtefactCollectionDto artefactCollectionDto = artefactCollectionsRepository.addCollection(artefactCollection);
        ArtefactCollection dut = artefactCollectionsRepository.getCollection(artefactCollection.getName());

        log.info("ArtefactObject: {}", dut);
        //Then
        assertTrue(!dut.getItems().isEmpty(), "Items with artefact objects is not empty.");
        boolean dupPasses = dut.getItems().values().stream()
                .allMatch(x -> x.getId() != null);
        assertTrue(dupPasses, "All ArtefactObjects got their id.");
    }

    @Test
    void ShouldReturnCollectionByName() throws JsonProcessingException {
        //Given
        setRepositoryCollections(goodsRawCollection);
        //Then
        final var collectionName = "Goods";
        ArtefactCollection dut = artefactCollectionsRepository.getCollection(collectionName);
        Assert.isTrue(dut.getName().equals(collectionName), "Items must not be empty!");
    }


    @Test
    void ShouldThrowCollectionNotFoundExceptionWhenAddingCollectionItemToTheNonExistingCollection() throws JsonProcessingException {
        //Given
        final var artefactId = "TEST86b2-82ec-11ed-a1eb-0242ac12000";
        final var wrongCollectionName = "WrongCollectionName";
        //Artfecat object details
        Map<String, Object> detailsMap = Map.of(
                "id", artefactId,
                "name", "del-good",
                "brand", "del-brand",
                "price", "99.99",
                "image", "del-image"
        );

        ArtefactObject artefactObject = new ArtefactObject();
        //setting artefact object id
        artefactObject.setId(artefactId);
        //setting artefact object details
        artefactObject.setDetails(detailsMap);

        //Storing test collection
        setRepositoryCollections(goodsRawCollection);
        //Adding artefact object to be deleted

        //Then
        CollectionNotFoundException exception = assertThrows(
                CollectionNotFoundException.class,
                () -> artefactCollectionsRepository.addOrUpdateCollectionItem(wrongCollectionName, artefactObject)
        );

        assertAll("Thrown ArtefactNotFound exception",
                () -> assertEquals(
                        String.format("Collection with name '%s' not found!", wrongCollectionName),
                        exception.getMessage(),
                        "Message must contain collection name!")
        );
    }

    @Test
    void ShouldGenerateUUIDWhenAddingCollectionItemWithoutId() throws JsonProcessingException {
        //Given
        final var collectionName = "Goods";
        //Artfecat object details
        Map<String, Object> detailsMap = Map.of(
                //"id", artefactId,
                "name", "good",
                "brand", "brand",
                "price", "99.99",
                "image", "image"
        );

        ArtefactObject artefactObject = new ArtefactObject();
        //setting artefact object id
        //artefactObject.setId(artefactId);
        //setting artefact object details
        artefactObject.setDetails(detailsMap);

        //Storing test collection
        setRepositoryCollections(goodsRawCollection);
        //Adding artefact object to be deleted

        //Then
        ArtefactObject dut = artefactCollectionsRepository.addOrUpdateCollectionItem(collectionName, artefactObject);


        assertAll("Artefact Object ID must not be null.",
                () -> assertNotNull(dut.getId())
        );
    }

    @Test
    void ShouldUpdateArtefactWhenOneWithSameIdExists() throws JsonProcessingException {
        //Given
        final var artefactId = "ef9186b2-82ec-11ed-a1eb-0242ac120002";
        final var collectionName = "Goods";
        //Artfecat object details
        Map<String, Object> detailsMap = Map.of(
                "id", artefactId,
                "name", "goodUpdated",
                "brand", "brandUpdated",
                "price", "100.00",
                "image", "imageUpdated"
        );

        ArtefactObject artefactObject = new ArtefactObject();
        //setting artefact object id
        artefactObject.setId(artefactId);
        //setting artefact object details
        artefactObject.setDetails(detailsMap);

        //Storing test collection
        setRepositoryCollections(goodsRawCollection);
        //Adding artefact object to be deleted

        //Then
        ArtefactObject dut = artefactCollectionsRepository.addOrUpdateCollectionItem(collectionName, artefactObject);


        assertAll("Artefact Object ID must not be null.",
                () -> assertNotNull(dut.getId()),
                () -> assertEquals(
                        "goodUpdated",
                        dut.getDetails().get("name"),
                        "Artefact name must be updated."),
                () -> assertEquals(
                        "brandUpdated",
                        dut.getDetails().get("brand"),
                        "Artefact brand must be updated."),
                () -> assertEquals(
                        "100.00",
                        dut.getDetails().get("price"),
                        "Artefact price must be updated."),
                () -> assertEquals(
                        "imageUpdated",
                        dut.getDetails().get("image"),
                        "Artefact image must be updated.")
        );
    }

    @Test
    void ShouldReturnCollectionItemByCollectionNameAndArtefactId() throws JsonProcessingException {
        final var artefactId = "ef9186b2-82ec-11ed-a1eb-0242ac120002";
        setRepositoryCollections(goodsRawCollection);
        ArtefactObject dut = artefactCollectionsRepository.getCollectionItem("Goods", artefactId);
        log.info("Artefact object:{}", dut);
        assertNotNull(dut, "Artefact object can't be null!");
        assertEquals(artefactId, dut.getId(), "Artefact id must match!");
    }

    @Test
    void ShouldDeleteArtefactEntryByExistingCollectionNameAndArtefactId() throws JsonProcessingException {
        //Given
        final var artefactId = "TEST86b2-82ec-11ed-a1eb-0242ac12000";
        final var collectionName = "Goods";
        //Artfecat object details
        Map<String, Object> detailsMap = Map.of(
                "id", artefactId,
                "name", "del-good",
                "brand", "del-brand",
                "price", "99.99",
                "image", "del-image"
        );

        ArtefactObject artefactObjectToDelete = new ArtefactObject();
        //setting artefact object id
        artefactObjectToDelete.setId(artefactId);
        //setting artefact object details
        artefactObjectToDelete.setDetails(detailsMap);

        //Storing test collection
        setRepositoryCollections(goodsRawCollection);
        //Adding artefact object to be deleted
        artefactCollectionsRepository.addOrUpdateCollectionItem(collectionName, artefactObjectToDelete);

        //When
        artefactCollectionsRepository.removeCollectionItem(collectionName, artefactId);

        //Then
        ArtefactNotFoundException exception = assertThrows(
                ArtefactNotFoundException.class,
                () -> artefactCollectionsRepository.getCollectionItem(collectionName, artefactId)
        );

        assertAll("Thrown ArtefactNotFound exception",
                () -> assertEquals(
                        String.format("Artefact with id '%s' in the collection name '%s' not found!", artefactId, collectionName),
                        exception.getMessage(),
                        "Message must contain artefact id and collection name!")
                );

    }

    private void setRepositoryCollections(String jsonCollection) throws JsonProcessingException {
        //Mapping Collection json string to the ArtefactCollection object
        ArtefactCollection artefactCollection = oMapper.readValue(jsonCollection, ArtefactCollection.class);
        //Updating ArtefactObject IDs with Map ids
        //Just getting Map's key and set to the corresponding object as id.
        artefactCollection.getItems().entrySet().stream()
                .forEach((obj) -> obj.getValue().setId(obj.getKey()));
        //Init map
        ConcurrentHashMap<String, ArtefactCollection> collectionMap = new ConcurrentHashMap<>();
        //Adding collection to a repo storage
        collectionMap.put(artefactCollection.getName(), artefactCollection);
        //Storing collection
        this.artefactCollectionsRepository.setCollections(collectionMap);
    }
}