package org.nmdp.ngs.match.grade.model;

/**
 * Indicates one of the specified input values is invalid.
 */
public class InvalidInputException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }

}
