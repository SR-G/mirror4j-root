package org.tensin.mirror4j.model.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Commit;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.helpers.CollectionHelper;
import org.tensin.mirror4j.model.State;
import org.tensin.mirror4j.model.operations.IOperation;

/**
 * The Class AbstractConfiguration.
 */
public abstract class AbstractConfiguration {

    /**
     * The operations. Additionnal processing is done in the Simple Visitor used when dezerializing XML (in order to dynamically find the right implementations
     * based on the annotations used in the ? implements IOperation). This is better than using an ElementListUnion as at this point, we don't know all the
     * available implementations (as they can be given at runtime through additionnal plugins).
     */
    @ElementList(inline = false, name = "operations", required = false)
    @Description(value = "The list of operations to run when the condition - id and state for example - are met")
    private Collection<IOperation> operations = new ArrayList<IOperation>();

    /** The state. */
    @Attribute(required = true, name = "state")
    @Description(value = "The ztamp state on which to react (POSE, RETIRE, ENVERS, ...)")
    private String readState;
    
    /** The states. */
    private Collection<State> states = new ArrayList<State>();

    /**
     * Commit.
     *
     * @throws Mirror4JException the mirror4 j exception
     */
    @Commit
    public void commit() throws Mirror4JException {
		states.clear();
	    for (final String s : CollectionHelper.convertStringToCollection(readState)) {
	    	states.add(State.build(s));
	    }
    }
    
	/**
	 * Dump states.
	 *
	 * @return the string
	 */
	public String dumpStates() {
		return CollectionHelper.convertCollectionToString(getStates());
	}

    /**
     * Gets the operations.
     * 
     * @return the operations
     */
    public Collection<IOperation> getOperations() {
        return operations;
    }



    /**
     * Gets the state.
     *
     * @return the state
     */
    public Collection<State> getStates() {
		return states;
	}

	/**
     * Checks for state.
     * 
     * @param wantedState
     *            the wanted state
     * @return true, if successful
     */
    public boolean hasState(final State wantedState) {
        return getStates().contains(wantedState);
    }

	/**
     * Sets the operations.
     * 
     * @param operations
     *            the new operations
     */
    public void setOperations(final Collection<IOperation> operations) {
        this.operations = operations;
    }

    /**
     * Sets the state.
     *
     * @param states the new states
     */
	public void setStates(Collection<State> states) {
		this.states = states;
	}
}