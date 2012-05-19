package org.apache.camel.osgi.service.filter;

/**
 * The {@code CriterionException} is exception to be used together with OSGi filter criterion API.
 */
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
