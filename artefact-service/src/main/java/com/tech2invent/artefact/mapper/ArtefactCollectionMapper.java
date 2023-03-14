package com.tech2invent.artefact.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech2invent.artefact.model.ArtefactCollection;
import com.tech2invent.artefact.dto.ArtefactCollectionDto;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/*@Mapper(componentModel = "spring")*/
public interface ArtefactCollectionMapper {
    ArtefactCollectionMapper INSTANCE = Mappers.getMapper(ArtefactCollectionMapper.class);
    ObjectMapper oMapper = new ObjectMapper();

    ArtefactCollectionDto ArtefactCollectionToArtefactCollectionDto(ArtefactCollection artefactCollection);

    ArtefactCollection ArtefactCollectionDtoToArtefactCollection(ArtefactCollectionDto artefactCollection);

    default List<Object> ConcurrentHashMapToList(ConcurrentHashMap<String, Object> items) {
        return items.entrySet()
                .stream()
                .map(itemEntry -> itemEntry.getValue())
                .collect(Collectors.toList());
    }

    default ConcurrentHashMap<String, Object> ListToConcurrentHashMap(List<Object> items) {
        ConcurrentHashMap<String, Object> itemsMap = new ConcurrentHashMap<>();
        items.forEach((item) -> {
            try {
                String itemId = oMapper.readTree(oMapper.writeValueAsString(item)).get("id").toString();
                itemsMap.put(itemId, item);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return itemsMap;
    }



}
