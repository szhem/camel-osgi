package org.apache.camel.osgi.service.filter;

/**
 * The {@code LikeCriterion} is the criterion that represents wildcarded match of the specified attribute to the
 * specified value.
 * <p/>
 * Type of the match is defined by means of {@link MatchMode}. By default {@link MatchMode#EXACT} is used.
 */
public class LikeCriterion extends AbstractCriterion {

    /**
     * The {@code MatchMode} is the type of the match to be used.
     */
    public static enum MatchMode {
        /**
         * The match mode to match the beginning of the specified value, i.e. {@code "attribute=val*"}.
         */
        START,

        /**
         * The match mode to match the end of the specified value, i.e. {@code "attribute=*ue"}.
         */
        END,

        /**
         * The match mode to match some substring of the specified value, i.e. {@code "attribute=*alu*"}.
         */
        ANYWHERE,

        /**
         * The match mode to match exact the specified value, i.e. {@code "attribute=value"}.
         */
        EXACT
    }

    private String attribute;
    private Object value;
    private MatchMode mode;

    public LikeCriterion(String attribute, Object value, MatchMode mode) {
        this.attribute = attribute;
        this.value = value;
        this.mode = mode;
    }

    public LikeCriterion(String attribute, Object value) {
        this(attribute, value, MatchMode.EXACT);
    }

    @Override
    public String value() {
        StringBuilder builder = new StringBuilder(32);
        builder.append('(').append(attribute).append('=');
        switch (mode) {
            case START:
                builder.append(value).append('*');
                break;
            case END:
                builder.append('*').append(value);
                break;
            case ANYWHERE:
                builder.append('*').append(value).append('*');
                break;
            default:
                builder.append(value);
                break;
        }
        builder.append(')');
        return builder.toString();
    }

}
