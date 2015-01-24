package org.tensin.mirror4j.boot;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.tensin.mirror4j.model.Mirror4JException;

/**
 * The Class Mirror4J.
 */
public class Mirror4J {

    /**
     * Found plugins.
     * 
     * @return the collection
     */
    private static Collection<String> loadPlugins(final String libPath) {
    	final Collection<String> result = new HashSet<String>();
    	try {
	        final File pluginPath = new File(libPath.replaceAll("file:", "") + File.separator + ".." + File.separator + PLUGINS_PATH);
	        if (!pluginPath.isDirectory()) {
	            System.out.println(FAKE_LOG_LABEL + "Plugin path [" + pluginPath.getCanonicalFile().getAbsolutePath() + "] doesn't exist, no plugin will be installed");
	        } else {
	            for (final File f : pluginPath.listFiles()) {
	                System.out.println(FAKE_LOG_LABEL + "Plugin [" + f.getName() + "] will be added");
	                result.add(f.getAbsolutePath());
	            }
	        }
    	} catch (IOException e) {
    		System.out.println(FAKE_LOG_LABEL + "Can't load plugins : " + e.getMessage());
    	}
        return result;
    }

    /**
     * Gestion du classpath.
     * 
     * @param args
     *            the arguments
     */
    public static void main(final String args[]) {
        // final SystemHelper systemHelper = new SystemHelper();
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            final ClasspathBooter cb = new ClasspathBooter(MIRROR4J_BOOT_JAR, "Mirror4J");
            final String libPath = cb.getLibraryPathName();
            cb.addAllJars();
            cb.addPlugins(loadPlugins(libPath));
            System.out.println(FAKE_LOG_LABEL + "Classpath :" + cb.displayClasspath(LINE_SEPARATOR));
            System.out.println(FAKE_LOG_LABEL + "Manifest :" + cb.getManifest(MIRROR4J_BOOT_JAR, LINE_SEPARATOR));
            cb.execute(MIRROR4J_MAIN_CLASS, "main", new Class[] { args.getClass() }, new Object[] { args });
        } catch (final Mirror4JException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** The Constant MIRROR4J_MAIN_CLASS. */
    private static final String MIRROR4J_MAIN_CLASS = org.tensin.mirror4j.Starter.class.getName();

    /** The Constant PLUGINS_PATH. */
    private static final String PLUGINS_PATH = "plugins/";

    /** The Constant MIRROR4J_BOOT_JAR. */
    private static final String MIRROR4J_BOOT_JAR = "mirror4j-.*\\.jar";

    /** The Constant FAKE_LOG_LABEL. */
    private static final String FAKE_LOG_LABEL = "0 [main] INFO org.tensin.mirror4j.boot.Mirror4J - ";

    /** The Constant LINE_SEPARATOR. */
    private static final String LINE_SEPARATOR = "\n     ";
}