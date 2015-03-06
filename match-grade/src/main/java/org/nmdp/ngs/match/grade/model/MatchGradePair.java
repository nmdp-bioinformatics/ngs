package org.nmdp.ngs.match.grade.model;

public class MatchGradePair {

    private MatchGradeCode matchGrade1;
    private MatchGradeCode matchGrade2;
    
    public MatchGradePair() {
    }
    public MatchGradePair(String grade1, String grade2) {
        matchGrade1 = MatchGradeCode.fromCode(grade1);
        matchGrade2 = MatchGradeCode.fromCode(grade2);
    }
    public MatchGradeCode getMatchGrade1() {
        return matchGrade1;
    }
    public void setMatchGrade1(MatchGradeCode matchGrade1) {
        this.matchGrade1 = matchGrade1;
    }
    public MatchGradeCode getMatchGrade2() {
        return matchGrade2;
    }
    public void setMatchGrade2(MatchGradeCode matchGrade2) {
        this.matchGrade2 = matchGrade2;
    }
    
    public String toString() {
        return matchGrade1 + ":" + matchGrade2;
    }
}
