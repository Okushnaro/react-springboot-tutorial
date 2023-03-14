package com.tech2invent.artefact.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.networknt.schema.JsonSchema;

import java.io.IOException;

public class SchemaSerializer extends JsonSerializer<JsonSchema> {

    @Override
    public void serialize(JsonSchema value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("schema", value.toString());
        gen.writeEndObject();
    }
}
