package com.tech2invent.artefact.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.networknt.schema.JsonSchema;
import com.tech2invent.artefact.model.ArtefactObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemSerializer  extends JsonSerializer<ConcurrentHashMap<String, ArtefactObject>> {
    @Override
    public void serialize(ConcurrentHashMap<String, ArtefactObject> artefactMap, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        List<ArtefactObject> serialList = new ArrayList<>();
        for (ArtefactObject artefact: artefactMap.values()) {
            serialList.add(artefact);
        }

        jsonGenerator.writeObject(serialList);
    }
}
