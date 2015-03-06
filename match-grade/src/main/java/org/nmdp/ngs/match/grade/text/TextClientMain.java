package org.nmdp.ngs.match.grade.text;
import java.util.Arrays;

import org.nmdp.ngs.match.grade.model.GradeException;
import org.nmdp.ngs.match.grade.model.InvalidInputException;
import org.nmdp.ngs.match.grade.model.MatchGradePair;

/**
 * Simple client that may be invoked with match grade arguments.
 */
public class TextClientMain {
    
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: locus donorTypings recipientTypings");
            System.exit(1);
        }
        int pos = 0;
        String baseurl = args[pos++];
        String locus = args[pos++];
        String donorTypings = args[pos++];
        String recipientTypings = args[pos++];
        System.out.println("for " + Arrays.toString(args));
        TextResearchMatchGrader matchGradeClient = new TextResearchMatchGrader(baseurl);
        try {
            MatchGradePair matchGradePair = matchGradeClient.computeMatchGrade(locus, donorTypings, recipientTypings);
            System.out.println("Result: " + matchGradePair);
        } catch (InvalidInputException e) {
            // Notify user to correct input values.
            e.printStackTrace();
        } catch (GradeException e) {
            // Issue calculating the grade.  Report as a defect to be fixed.
            e.printStackTrace();
        } catch (RuntimeException e) {
            // System or network issue.  
            e.printStackTrace();
        }
    }
}
