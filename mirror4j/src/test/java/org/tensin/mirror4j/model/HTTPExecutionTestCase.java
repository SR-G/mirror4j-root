package org.tensin.mirror4j.model;

import org.junit.Test;
import org.tensin.mirror4j.AbstractMirror4JTestCase;
import org.tensin.mirror4j.model.events.EventZtamp;
import org.tensin.mirror4j.model.operations.HTTPExecution;

/**
 * The Class ShellExecutionTestCase.
 */
public class HTTPExecutionTestCase extends AbstractMirror4JTestCase {

    /**
     * Test shell execution.
     * 
     * @throws Mirror4JException
     *             the mirror exception
     */
    @Test
    public void testShellExecution() throws Mirror4JException {
        final HTTPExecution operation = new HTTPExecution();
        operation.setActive(true);
        operation.setUrl("http://www.google.fr");
        operation.execute(new EventZtamp(State.POSE, "00001"));
    }

}
