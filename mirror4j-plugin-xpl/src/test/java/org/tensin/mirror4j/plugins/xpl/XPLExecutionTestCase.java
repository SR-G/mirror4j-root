package org.tensin.mirror4j.plugins.xpl;

import org.junit.Test;
import org.tensin.mirror4j.model.Mirror4JException;
import org.tensin.mirror4j.model.State;
import org.tensin.mirror4j.model.events.EventZtamp;

/**
 * The Class XPLExecutionTestCase.
 */
public class XPLExecutionTestCase {

    /**
     * Start java xpl hub.
     */
    private void startJavaXPLHub() throws Mirror4JException {

    }

    /**
     * Test xpl event.
     * 
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    @Test
    public void testXPLEvent() throws Mirror4JException {
        startJavaXPLHub();

        final XPLExecution operation = new XPLExecution();
        operation.start();
        operation.setActive(true);
        operation.execute(new EventZtamp(State.POSE, "00001"));
    }
}
