package org.apache.camel.osgi.filter;

public class CriterionException extends RuntimeException {

    private static final long serialVersionUID = 1931841558543864413L;

    public CriterionException(String message) {
        super(message);
    }

    public CriterionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CriterionException(Throwable cause) {
        super(cause);
    }

}
