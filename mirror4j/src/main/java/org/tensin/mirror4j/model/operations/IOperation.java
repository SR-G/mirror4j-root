package org.tensin.mirror4j.model.operations;

import org.tensin.mirror4j.model.Mirror4JException;
import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Interface IOperation.
 */
public interface IOperation {

    /**
     * Execute.
     *
     * @param event the event
     * @throws Mirror4JException             the mirror exception
     */
    public void execute(final IEvent event) throws Mirror4JException;

    /**
     * Start.
     * 
     * @throws Mirror4JException
     *             the mirror exception
     */
    public void start() throws Mirror4JException;

    /**
     * Stop.
     * 
     * @throws Mirror4JException
     *             the mirror exception
     */
    public void stop() throws Mirror4JException;

}
