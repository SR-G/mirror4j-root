package org.tensin.mirror4j.plugins.ssh;

import org.junit.Test;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.model.State;
import org.tensin.mirror4j.model.events.EventZtamp;

/**
 * The Class SSHExecutionTestCase.
 */
public class SSHExecutionTestCase {

    /**
     * Test ssh execution.
     * 
     * @throws Mirror4JException
     *             the mirror exception
     */
    @Test
    public void testSSHExecution() throws Mirror4JException {
        final SSHExecution operation = new SSHExecution();
        operation.setActive(true);
        operation.setCommand("echo 1");
        operation.setHostname("jupiter");
        operation.setUsername("sergio");
        operation.execute(new EventZtamp(State.POSE, "00001"));
    }

}
