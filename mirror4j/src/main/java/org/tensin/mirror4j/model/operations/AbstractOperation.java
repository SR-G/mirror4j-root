package org.tensin.mirror4j.model.operations;

import org.simpleframework.xml.Attribute;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.bean.BeanHelper;
import org.tensin.mirror4j.bean.Method;

/**
 * The Class AbstractOperation.
 */
public abstract class AbstractOperation {

    /** The name. */
    @Attribute(required = false)
    @Description(value = "Optional logical name of the operation to run")
    @BeanField
    private String name;

    /** The active. */
    @Attribute(required = false)
    @Description(value = "Optional description of the operation to run")
    @BeanField(exclude = { Method.TO_STRING })
    private boolean active = true;

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return BeanHelper.equals(this, obj);
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return BeanHelper.hashCode(this);
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active
     *            the new active
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return BeanHelper.toString(this);
    }
}
