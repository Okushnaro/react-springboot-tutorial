package com.tech2invent.artefact.exception;

import com.networknt.schema.ValidationMessage;

import java.util.List;
import java.util.Set;

//TODO: Fix Exception constructor tu utilize the supper()
//TODO: Add java docs

public class ArtefactSchemaValidationErrorsException extends RuntimeException{


    private List<ValidationMessage> validationMessages;

    public ArtefactSchemaValidationErrorsException(String collectionName, String artefact, Set<ValidationMessage> errorMessages) {
        validationMessages = errorMessages.stream().toList();

        ArtefactSchemaValidationErrorsException.generateMessage(collectionName, artefact);
    }


    private static String generateMessage(String collectionName, String artefact) {
        //String errorsStr = errorMessages.stream().map(x -> x.getMessage()).collect(Collectors.joining("\n"));
        return "For collection '" + collectionName + "' the entity: " + artefact + " fails Schema validation.";
    }

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
}
