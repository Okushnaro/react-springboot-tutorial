package com.tech2invent.artefact.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArtefactCollectionDto {
    @NotEmpty(message = "Collection name can't be empty.")
    private String name;
    @NotNull(message = "Schema must be provided.")
    private JsonNode schema;
    //A list of artefact objects with dynamic structure regulated by collection schema
    //TODO: Refactor ArtefactCollectionDto and develop to use custom de/serializer
    //  to manage Object <-> ArtefactObject conversion
    private List<Object> items = new ArrayList<>();;
}
