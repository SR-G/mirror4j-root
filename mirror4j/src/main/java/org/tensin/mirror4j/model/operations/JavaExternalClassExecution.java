package org.tensin.mirror4j.model.operations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Class OperationShellExecution.
 */
@Root(name = "java")
@Description(value = "Java execution : executes any java class available in the classpath")
public class JavaExternalClassExecution extends AbstractOperation implements IOperation {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The java operation class. */
    @Attribute(required = true)
    @Description("The java class to launch (must be available on the inital classpath of the mirror4j process)")
    @BeanField
    private String javaOperationClass;

    /** The java operation method. */
    @Attribute(required = true)
    @Description("The java method name to run")
    @BeanField
    private String javaOperationMethod;

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute()
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        LOGGER.info("Now executing java operation [" + javaOperationMethod + "] on class [" + javaOperationClass + "]");
        try {
            final Class<?> clazz = Class.forName(javaOperationClass);
            if (clazz != null) {
                final Object o = clazz.newInstance();
                final Method m = clazz.getMethod(javaOperationMethod, new Class[] {});
                if (m == null) {
                    LOGGER.error("Can't find method name [" + javaOperationMethod + "] on class [" + javaOperationClass + "]");
                } else {
                    m.invoke(o, new Object[] {});
                }
            } else {
                LOGGER.error("Can't find class [" + javaOperationClass + "]");
            }
        } catch (final ClassNotFoundException e) {
            throw new Mirror4JException(e);
        } catch (final InstantiationException e) {
            throw new Mirror4JException(e);
        } catch (final IllegalAccessException e) {
            throw new Mirror4JException(e);
        } catch (final SecurityException e) {
            throw new Mirror4JException(e);
        } catch (final NoSuchMethodException e) {
            throw new Mirror4JException(e);
        } catch (final IllegalArgumentException e) {
            throw new Mirror4JException(e);
        } catch (final InvocationTargetException e) {
            throw new Mirror4JException(e);
        }

    }

    /**
     * Gets the java operation class.
     *
     * @return the java operation class
     */
    public String getJavaOperationClass() {
        return javaOperationClass;
    }

    /**
     * Gets the java operation method.
     *
     * @return the java operation method
     */
    public String getJavaOperationMethod() {
        return javaOperationMethod;
    }

    /**
     * Sets the java operation class.
     *
     * @param javaOperationClass
     *            the new java operation class
     */
    public void setJavaOperationClass(final String javaOperationClass) {
        this.javaOperationClass = javaOperationClass;
    }

    /**
     * Sets the java operation method.
     *
     * @param javaOperationMethod
     *            the new java operation method
     */
    public void setJavaOperationMethod(final String javaOperationMethod) {
        this.javaOperationMethod = javaOperationMethod;
    }

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#start()
     */
    @Override
    public void start() throws Mirror4JException {

    }

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#stop()
     */
    @Override
    public void stop() throws Mirror4JException {

    }
}