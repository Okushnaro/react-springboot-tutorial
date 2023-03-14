package com.tech2invent.artefact.exception;

import com.networknt.schema.ValidationMessage;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 *
 */
@Getter
public class CollectionItemsSchemaValidationException extends RuntimeException{
    private final transient Map<String, Set<ValidationMessage>> artefactErrorsMap;


    /**
     * Constructs a new CollectionItemsSchemaValidationException exception with the specified detail message.
     * @param collectionName
     * @param artefactErrorsMap
     */
    public CollectionItemsSchemaValidationException(String collectionName, Map<String, Set<ValidationMessage>> artefactErrorsMap) {
        super(CollectionItemsSchemaValidationException.exceptionMessage(collectionName, artefactErrorsMap));
        this.artefactErrorsMap = artefactErrorsMap;
    }


    /**
     * Returns formatted exception message about artefact items schema validation with given Collection name and
     * number of affected artefact items.
     * @param collectionName
     * @param artefactErrorsMap
     * @return
     */
    private static String exceptionMessage(String collectionName, Map<String, Set<ValidationMessage>> artefactErrorsMap){
        return String.format(
                "Collection '%s' fails Schema validation for %d artefacts",
                collectionName,
                artefactErrorsMap.size()
        );
    }
}
