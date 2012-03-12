package org.apache.camel.osgi.filter;

public class LikeFilter extends AbstractFilter {

    public static enum MatchMode {
        START,
        END,
        ANYWHERE,
        EXACT
    }

    private String attribute;
    private Object value;
    private MatchMode mode;

    public LikeFilter(String attribute, Object value, MatchMode mode) {
        this.attribute = attribute;
        this.value = value;
        this.mode = mode;
    }

    public LikeFilter(String attribute, Object value) {
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
