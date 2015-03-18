package org.nmdp.ngs.match.grade.model;

/**
 * Indicates a failure to calculate a match grade.
 */
public class GradeException extends Exception {

    private static final long serialVersionUID = 1L;

    public GradeException(String message) {
        super(message);
    }

}
