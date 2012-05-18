package org.apache.camel.osgi.integration;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.junit.runner.RunWith;
import org.openengsb.labs.paxexam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.options.CompositeOption;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public abstract class OsgiIntegrationTest {

    protected static final Long DEFAULT_TIMEOUT = 10000L;

    @Inject
    protected BundleContext bundleContext;

    @Inject
    protected CommandProcessor commandProcessor;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

    /**
     * Executes the command and returns the output as a String.
     *
     * @param command
     * @return
     */
    protected String executeCommand(String command) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        CommandSession commandSession = commandProcessor.createSession(System.in, printStream, System.err);
        //This is required in order to run scripts that use those session variables.
        commandSession.put("APPLICATION", System.getProperty("karaf.name", "root"));
        commandSession.put("USER", "karaf");

        commandSession.execute(command);

        return byteArrayOutputStream.toString();
    }


    /**
     * Executes multiple commands inside a Single Session.
     * Commands have a default timeout of 10 seconds.
     * @param commands
     * @return
     */
    protected String executeCommands(final String ...commands) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        final CommandProcessor commandProcessor = getOsgiService(CommandProcessor.class);
        final CommandSession commandSession = commandProcessor.createSession(System.in, printStream, System.err);
        commandSession.put("APPLICATION", System.getProperty("karaf.name", "root"));
        commandSession.put("USER", "karaf");

        for (String command : commands) {
            commandSession.execute(command);
        }

        return byteArrayOutputStream.toString();
    }

    protected Bundle getInstalledBundle(String symbolicName) throws Exception {
        for (Bundle b : bundleContext.getBundles()) {
            if (b.getSymbolicName().equals(symbolicName)) {
                return b;
            }
        }
        throw new RuntimeException("Bundle " + symbolicName + " does not exist");
    }

    protected <T> T getOsgiService(Class<T> type, long timeout) {
        return getOsgiService(type, null, timeout);
    }

    protected <T> T getOsgiService(Class<T> type, String filter) {
        return getOsgiService(type, filter, DEFAULT_TIMEOUT);
    }

    protected <T> T getOsgiService(Class<T> type) {
        return getOsgiService(type, null, DEFAULT_TIMEOUT);
    }

    protected <T> T getOsgiService(Class<T> type, String filter, long timeout) {
        ServiceTracker tracker = null;
        try {
            String flt;
            if (filter != null) {
                if (filter.startsWith("(")) {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")" + filter + ")";
                } else {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")(" + filter + "))";
                }
            } else {
                flt = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";
            }
            Filter osgiFilter = FrameworkUtil.createFilter(flt);
            tracker = new ServiceTracker(bundleContext, osgiFilter, null);
            tracker.open(true);
            // Note that the tracker is not closed to keep the reference
            // This is buggy, as the service reference may change i think
            Object svc = type.cast(tracker.waitForService(timeout));
            if (svc == null) {
                throw new RuntimeException("Gave up waiting for service " + flt);
            }
            return type.cast(svc);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid filter", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Option defaultOptions() {
        return new CompositeOption() {
            @Override
            public Option[] getOptions() {
                return new Option[] {
                    karafDistributionConfiguration()
                        .frameworkUrl(maven("org.apache.karaf", "apache-karaf").versionAsInProject().type("tar.gz"))
                        .karafVersion(MavenUtils.getArtifactVersion("org.apache.karaf", "apache-karaf")).name("Apache Karaf")
                        .unpackDirectory(new File("target/paxexam/unpack/")),
                    logLevel(LogLevelOption.LogLevel.ERROR),

                    scanFeatures(
                        maven("org.apache.karaf.assemblies.features", "standard").versionAsInProject().type("xml").classifier("features"),
                        "karaf-framework"),
                    scanFeatures(
                        maven("org.apache.camel.karaf", "apache-camel").versionAsInProject().type("xml").classifier("features"),
                        "camel-blueprint"),

                    bundle("file:target/org.apache.camel.osgi-" + MavenUtils.getArtifactVersion("org.apache.camel", "org.apache.camel.osgi") + ".jar")
                };
            }
        };
    }

}
