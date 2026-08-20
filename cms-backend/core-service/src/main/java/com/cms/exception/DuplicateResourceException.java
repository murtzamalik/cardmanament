package com.cms.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String key) {
        super(resource + " already exists with key: " + key);
    }
}
