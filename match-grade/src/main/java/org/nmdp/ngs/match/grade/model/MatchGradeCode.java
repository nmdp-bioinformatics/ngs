package org.nmdp.ngs.match.grade.model;

public enum MatchGradeCode {
    NOT_COMPUTED("N"),
    HIGH_RESOLUTION_MATCH("HM"),
    HIGH_RESOLUTION_MISMATCH("Hmm"),
    INTERMEDIATE_RESOLUTION_MATCH("IM"),
    INTERMEDIATE_RESOLUTION_MISMATCH("Imm");
    
    private String code;

    private MatchGradeCode(String code) {
        this.code = code;
    }
    
    public String toString() {
        return code;
    }

    public static MatchGradeCode fromCode(String code) {
        for (MatchGradeCode cd : MatchGradeCode.values()) {
            if (cd.code.equals(code)) {
                return cd;
            }
        }
        throw new IllegalArgumentException("Invalid MatchGradeCode: " + code);
    }
}
