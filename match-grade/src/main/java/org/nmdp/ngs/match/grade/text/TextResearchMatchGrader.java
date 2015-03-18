package org.nmdp.ngs.match.grade.text;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.nmdp.ngs.match.grade.model.GradeException;
import org.nmdp.ngs.match.grade.model.InvalidInputException;
import org.nmdp.ngs.match.grade.model.MatchGradePair;


public class TextResearchMatchGrader {

    private WebClient webClient;

    public TextResearchMatchGrader(String baseAddress) {
        webClient = WebClient.create(baseAddress).path("match-grade").accept("text/plain").type("text/plain");
    }
    
    public synchronized MatchGradePair computeMatchGrade(String locus, String donorTypings, String recipientTypings)
            throws InvalidInputException, GradeException
    {
        checkParam("locus", locus);
        checkParam("donorTypings", donorTypings);
        checkParam("recipientTypings", recipientTypings);
        String request = locus + " " + donorTypings + " " + recipientTypings;
        WebClient tempClient = WebClient.fromClient(webClient, true);  // for thread safety
        Response response = tempClient.post(request);
        int status = response.getStatus();
        String content = response.readEntity(String.class);
        if (status == 200 ) { // OK
            String[] parts = content.split(":");
            if (parts.length == 2) {
                return new MatchGradePair(parts[0], parts[1]);
            } else {
                throw new GradeException("Unexpected response format: " + content);
            }
        } else if (status == 400 ) { // Bad Request
            throw new GradeException(content);
        }
        
        throw new GradeException("server response status=" + status + "\n" + content);
    }

    private void checkParam(String name, String value) throws InvalidInputException {
        if (value == null) {
            throw new InvalidInputException("Null " + name + " parameter");
        }
        if (value.trim().isEmpty()) {
            throw new InvalidInputException("Empty " + name + " parameter");
        }
    }


}
