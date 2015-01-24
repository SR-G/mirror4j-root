package org.tensin.mirror4j.model;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Visitor;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;
import org.tensin.mirror4j.model.configuration.IDefinition;
import org.tensin.mirror4j.model.operations.IOperation;

/**
 * The Class MirrorConfigurationVisitor.
 */
public class MirrorConfigurationVisitor implements Visitor {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The available operations. */
    private final Set<Class<? extends IOperation>> availableOperations;

    /** The available definitions. */
    private final Set<Class<? extends IDefinition>> availableDefinitions;

    /**
     * Instantiates a new mirror configuration visitor.
     *
     * @param availableOperations
     *            the available operations
     * @param availableDefinitions
     *            the available definitions
     */
    public MirrorConfigurationVisitor(final Set<Class<? extends IOperation>> availableOperations, final Set<Class<? extends IDefinition>> availableDefinitions) {
        super();
        this.availableOperations = availableOperations;
        this.availableDefinitions = availableDefinitions;
    }

    /**
     * Inject definition class.
     *
     * @param node
     *            the node
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void injectDefinitionClass(final NodeMap<InputNode> node) throws Mirror4JException {
        final String definitionNode = node.getName();
        final InputNode inputNode = node.getNode();
        boolean found = false;
        for (final Class<? extends IDefinition> clazz : availableDefinitions) {
            final Root annotation = clazz.getAnnotation(Root.class);
            if (annotation.name().equals(definitionNode)) {
                LOGGER.info("Registering definition [" + definitionNode + "] with associated class [" + clazz.getName() + "]");
                inputNode.getAttributes().put("class", clazz.getName());
                found = true;
                break;
            }
        }
        if (!found) {
            throw new Mirror4JException("Found unknow definition [" + definitionNode + "] (no associated class)");
        }
    }

    /**
     * Inject operation class.
     *
     * @param node
     *            the node
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void injectOperationClass(final NodeMap<InputNode> node) throws Mirror4JException {
        final String operationNode = node.getName();
        final InputNode inputNode = node.getNode();

        boolean found = false;
        for (final Class<? extends IOperation> clazz : availableOperations) {
            final Root annotation = clazz.getAnnotation(Root.class);
            if (annotation.name().equals(operationNode)) {
                LOGGER.info("Registering operation [" + operationNode + "] with associated class [" + clazz.getName() + "]");
                inputNode.getAttributes().put("class", clazz.getName());
                found = true;
                break;
            }
        }

        if (!found) {
            throw new Mirror4JException("Found unknow operation [" + operationNode + "] (no associated class)");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.simpleframework.xml.strategy.Visitor#read(org.simpleframework.xml.strategy.Type, org.simpleframework.xml.stream.NodeMap)
     */
    @Override
    public void read(final Type type, final NodeMap<InputNode> node) throws Exception {
        if (type.getType().equals(org.tensin.mirror4j.model.operations.IOperation.class)) {
            injectOperationClass(node);
        }

        if (type.getType().equals(org.tensin.mirror4j.model.configuration.IDefinition.class)) {
            injectDefinitionClass(node);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.simpleframework.xml.strategy.Visitor#write(org.simpleframework.xml.strategy.Type, org.simpleframework.xml.stream.NodeMap)
     */
    @Override
    public void write(final Type type, final NodeMap<OutputNode> node) throws Exception {
    }
}