package org.tensin.mirror4j;

import org.tensin.mirror4j.Starter;

/**
 * The Class StartMirror.
 */
public class StartMirror {

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws Exception
     *             the exception
     */
    public static void main(final String[] args) throws Exception {
        Starter.main(new String[] { "-d", "/dev/hidraw1", "--debug" });
    }

}
