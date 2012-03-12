package org.apache.camel.osgi.filter;

public abstract class AbstractFilter implements Filter {

    @Override
    public String toString() {
        return value();
    }

}
