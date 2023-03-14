package com.tech2invent.artefact.exception;

//TODO: Add javadoc for CollectionNotFoundException class and methods
public class CollectionNotFoundException extends RuntimeException{

    public CollectionNotFoundException(String collectionName) {
        super(CollectionNotFoundException.generateMessage(collectionName));
    }

    public CollectionNotFoundException(String collectionName, Throwable cause){
        super(CollectionNotFoundException.generateMessage(collectionName), cause);

    }

    private static String generateMessage(String collectionName){
        final String msgTemplate = "Collection with name '%s' not found!";
        return String.format(msgTemplate, collectionName);

    }
}
