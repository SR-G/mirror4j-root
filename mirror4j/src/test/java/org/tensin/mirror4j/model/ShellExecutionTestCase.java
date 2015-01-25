package org.tensin.mirror4j.model;

import org.tensin.mirror4j.AbstractMirror4JTestCase;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.model.events.EventZtamp;
import org.tensin.mirror4j.model.operations.ShellExecution;

/**
 * The Class ShellExecutionTestCase.
 */
public class ShellExecutionTestCase extends AbstractMirror4JTestCase {

    /**
     * Test shell execution.
     *
     * @throws Mirror4JException
     *             the mirror exception
     */
    // @Test
    public void testShellExecution() throws Mirror4JException {
        final ShellExecution operation = new ShellExecution();
        operation.setActive(true);
        operation.setCommand("ls -1");
        operation.execute(new EventZtamp(State.POSE, "00001"));
    }
}
