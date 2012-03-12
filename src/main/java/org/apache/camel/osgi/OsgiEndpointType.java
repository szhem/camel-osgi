package org.apache.camel.osgi;

import org.apache.camel.Component;
import org.apache.camel.Endpoint;

public enum OsgiEndpointType {
    
    DEFAULT("default:") {
        @Override
        public String getName(String path) {
            if(path.startsWith(prefix)) {
                return super.getName(prefix);
            }
            return path;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Endpoint> T createEndpoint(String uri, Component comp) {
            return (T) new OsgiDefaultEndpoint(uri, comp);
        }
    },

    MULTICAST("multicast:") {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends Endpoint> T createEndpoint(String uri, Component comp) {
            return (T) new OsgiMulticastEndpoint(uri, comp);
        }
    },

    ROUNDROBIN("roundrobin:") {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends Endpoint> T createEndpoint(String uri, Component comp) {
            return (T) new OsgiRoundRobinEndpoint(uri, comp);
        }
    };

    protected final String prefix;

    private OsgiEndpointType(String prefix) {
        this.prefix = prefix;
    }
    
    public abstract <T extends Endpoint> T createEndpoint(String uri, Component comp);

    public String getName(String path) {
        return path.substring(prefix.length());
    }

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
