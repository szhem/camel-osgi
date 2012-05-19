package org.apache.camel.osgi.service;

import org.apache.camel.Component;
import org.apache.camel.Endpoint;

/**
 * The {@code OsgiEndpointType} is a factory that creates different types of camel endpoints.
 */
public enum OsgiEndpointType {
    
    DEFAULT("default:") {
        /**
         * Gets name of the endpoint from its path.
         * <p/>
         * If path starts with {@literal "default:"} than the {@link OsgiEndpointType#getName(String) default behavior} is
         * applied, else the path will be returned.
         *
         * @param path the path of the camel endpoint, i.e. {@literal "default:test"} for {@literal "osgi:default:test"}
         *
         * @return endpoint name
         */
        @Override
        public String getName(String path) {
            if(path.startsWith(prefix)) {
                return super.getName(path);
            }
            return path;
        }

        @Override
        @SuppressWarnings("unchecked")
        public OsgiDefaultEndpoint createEndpoint(String uri, Component comp) {
            return new OsgiDefaultEndpoint(uri, comp);
        }
    },

    MULTICAST("multicast:") {
        @Override
        @SuppressWarnings("unchecked")
        public OsgiMulticastEndpoint createEndpoint(String uri, Component comp) {
            return new OsgiMulticastEndpoint(uri, comp);
        }
    },

    ROUNDROBIN("roundrobin:") {
        @Override
        @SuppressWarnings("unchecked")
        public OsgiRoundRobinEndpoint createEndpoint(String uri, Component comp) {
            return new OsgiRoundRobinEndpoint(uri, comp);
        }
    },

    RANDOM("random:") {
        @Override
        @SuppressWarnings("unchecked")
        public OsgiRandomEndpoint createEndpoint(String uri, Component comp) {
            return new OsgiRandomEndpoint(uri, comp);
        }
    };

    protected final String prefix;

    private OsgiEndpointType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Creates the corresponding camel endpoint.
     *
     * @param uri endpoint uri
     * @param comp camel component
     *
     * @param <T> the type of the endpoint which must be one of the following {@link OsgiDefaultEndpoint},
     * {@link OsgiMulticastEndpoint}, {@link OsgiRoundRobinEndpoint}
     *
     * @return the corresponding camel endpoint
     *
     * @see OsgiDefaultEndpoint
     * @see OsgiMulticastEndpoint
     * @see OsgiRoundRobinEndpoint
     */
    public abstract <T extends Endpoint> T createEndpoint(String uri, Component comp);

    /**
     * Extracts name of the endpoint from its path.
     * <p/>
     * If path starts with predefined endpoint prefix ({@literal "default:"}, {@literal "multicast:"},
     * {@literal "roundrobin:"}, {@literal "random:"}) than endpoint name is considered to be the remaining path of
     * the endpoint path without prefix, else endpoint name is the whole path.
     *
     * @param path the path of the camel endpoint, i.e. {@code "default:test"} for {@code "osgi:default:test"}
     *
     * @return endpoint name
     */
    public String getName(String path) {
        return path.substring(prefix.length());
    }

    /**
     * Resolves endpoint type from the camel endpoint path.
     * <p/>
     * If path is unknown than the {@link #DEFAULT} endpoint is always resolved.
     *
     * @param path the path of the camel endpoint, i.e. {@code "default:test"} for {@code "osgi:default:test"}
     *
     * @return {@link #DEFAULT} endpoint type by default or for the path that starts with {@literal "default:"} prefix,<br/>
     * {@link #MULTICAST} endpoint type if path starts with {@literal "multicast:"} prefix,<br/>
     * {@link #ROUNDROBIN} endpoint type if path starts with {@literal "roundrobin:"} prefix,<br/>
     * {@link #RANDOM} endpoint type if path starts with {@literal "random:"} prefix
     */
    public static OsgiEndpointType fromPath(String path) {
        OsgiEndpointType result = DEFAULT;

        if(path != null) {
            for(OsgiEndpointType type : values()) {
                if (path.startsWith(type.prefix)) {
                    result = type;
                    break;
                }
            }
        }

        return result;
    }

}
