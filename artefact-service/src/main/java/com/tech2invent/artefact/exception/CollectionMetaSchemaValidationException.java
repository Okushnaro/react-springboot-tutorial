package com.tech2invent.artefact.error;

import com.networknt.schema.ValidationMessage;

import java.util.Map;
import java.util.Set;

public class CollectionSchemaValidationException extends RuntimeException{


    private transient Map<String, Set<ValidationMessage>> artefactErrorsMap;
    private transient Set<ValidationMessage> schemaValidationErrors;


    public CollectionSchemaValidationException(String collectionName, Map<String, Set<ValidationMessage>> artefactErrorsMap) {
        super(String.format("Collection '%s' fails Schema validation for %d artefacts", collectionName, artefactErrorsMap.size()));
        this.artefactErrorsMap = artefactErrorsMap;
    }



    public CollectionSchemaValidationException(String collectionName, Set<ValidationMessage> schemaValidationErrors) {
        super(String.format("The `schema` object in the collection '%s' fails Meta-Schema validation, found %d errors", collectionName, schemaValidationErrors.size()));
        this.schemaValidationErrors = schemaValidationErrors;
    }

    public Map<String, Set<ValidationMessage>> getArtefactErrorsMap() {
        return artefactErrorsMap;
    }

    public Set<ValidationMessage> getSchemaValidationErrors() {
        return schemaValidationErrors;
    }
}
