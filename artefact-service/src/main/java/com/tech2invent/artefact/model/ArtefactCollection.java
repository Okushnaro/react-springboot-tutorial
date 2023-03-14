package com.tech2invent.artefact.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tech2invent.artefact.mapper.ItemSerializer;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArtefactCollection {
    private static final long serialVersionUID = 1L;
    @NotEmpty(message = "Collection name can't be empty.")
    private String name;

    /*@NotNull(message = "Schema must be provided.")*/
    private JsonNode schema;

    @JsonSerialize(using = ItemSerializer.class)
    private ConcurrentHashMap<String, ArtefactObject> items = new ConcurrentHashMap<>();


}
