package com.tech2invent.artefact.exception;
//TODO: Add javadoc for ArtefactNotFoundException class and methods
public class ArtefactNotFoundException extends RuntimeException{

    public ArtefactNotFoundException(String artefactId, String collectionName) {
        super(ArtefactNotFoundException.generateMessage(artefactId, collectionName));
    }

    public ArtefactNotFoundException(String artefactId, String collectionName, Throwable cause){
        super(ArtefactNotFoundException.generateMessage(artefactId, collectionName), cause);

    }

    private static String generateMessage(String artefactId, String collectionName){
        final String msgTemplate = "Artefact with id '%s' in the collection name '%s' not found!";
        return String.format(msgTemplate, artefactId, collectionName);

    }
}
