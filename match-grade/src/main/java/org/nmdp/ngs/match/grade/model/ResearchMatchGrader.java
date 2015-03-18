package org.nmdp.ngs.match.grade.model;


/*
 * Responsible for determining the research match grade.
 */
public interface ResearchMatchGrader {

    /**
     * Determines the research match grade.
     * @param locus HLA gene name or abbreviation
     * @param donorTypings 
     * @param recipientTypings
     * @return
     * @throws InvalidInputException
     * @throws GradeException
     */
    MatchGradePair computeMatchGrade(String locus, String donorTypings, String recipientTypings) throws InvalidInputException, GradeException;

}
