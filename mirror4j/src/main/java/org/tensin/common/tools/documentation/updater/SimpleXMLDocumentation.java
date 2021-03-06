package org.tensin.common.tools.documentation.updater;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class SimpleXMLDocumentation.
 *
 * @author j385649
 * @since 26 oct. 2011 14:40:52
 */
public class SimpleXMLDocumentation {

    /**
     * Method.
     *
     */
    private static void affecteIndices() {
        int indice = 0;
        final Iterator<String> itTypes = types.keySet().iterator();
        while (itTypes.hasNext()) {
            final SimpleXMLDocumentationType type = types.get(itTypes.next());
            if (!type.isPrimitive()) {
                indice++;
                type.setIndice(indice);
            }
        }
    }

    /**
     * Convert.
     *
     * @param racine
     *            the racine
     * @param destination
     *            the destination
     * @param converter
     *            the converter
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws SimpleXMLDocumentationException
     *             the simple xml documentation exception
     */
    public static void convert(final Class<?> racine, final String destination, final ISimpleXMLDocumentationOutput converter) throws IOException,
            SimpleXMLDocumentationException {
        LOGGER.info("Converting class [" + racine.getName() + "] to file [" + destination + "] with converter [" + converter.getClass().getSimpleName() + "]");
        initSimpleXMLDocumentation();
        final SimpleXMLDocumentation sxmldoc = new SimpleXMLDocumentation(racine);
        affecteIndices();

        // converter.mergeContent(racine, sxmldoc.getRoot(), destination, TOKEN_APT_AUTOGENERATED_CONTENT);
        String newContent = converter.generate(racine, sxmldoc.getRoot());
        if (converter.isModeMerge()) {
            newContent = converter.mergeContent(newContent, destination);
        }
        org.apache.commons.io.FileUtils.writeStringToFile(new File(destination), newContent);
    }

    /**
     * Generate content.
     *
     * @param racine
     *            the racine
     * @param converter
     *            the converter
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws SimpleXMLDocumentationException
     *             the simple xml documentation exception
     */
    public static String generateContent(final Class<?> racine, final ISimpleXMLDocumentationOutput converter) throws IOException,
            SimpleXMLDocumentationException {
        // initSimpleXMLDocumentation();
        final SimpleXMLDocumentation sxmldoc = new SimpleXMLDocumentation(racine);
        affecteIndices();
        return converter.generate(racine, sxmldoc.getRoot());
    }

    /**
     * Method.
     *
     * @param clazz
     *            the clazz
     * @return the type
     */
    static SimpleXMLDocumentationType getType(final Class<?> clazz) {
        return SimpleXMLDocumentation.types.get(clazz.getName());
    }

    /**
     * Method.
     *
     * @param pathName
     *            the path name
     * @return the type
     */
    static SimpleXMLDocumentationType getType(final String pathName) {
        return SimpleXMLDocumentation.types.get(pathName);
    }

    /**
     * Method.
     *
     */
    private static void initSimpleXMLDocumentation() {
        types = new LinkedHashMap<String, SimpleXMLDocumentationType>();
    }

    /**
     * Method.
     *
     * @param clazz
     *            the clazz
     */
    static void registerType(final Class<?> clazz) {
        final SimpleXMLDocumentationType type = new SimpleXMLDocumentationType(clazz);
        SimpleXMLDocumentation.types.put(type.getName(), type);
    }

    /**
     * Method.
     *
     * @param name
     *            the name
     */
    static void registerType(final String name) {
        final SimpleXMLDocumentationType type = new SimpleXMLDocumentationType(name);
        SimpleXMLDocumentation.types.put(type.getName(), type);
    }

    /**
     * Method.
     *
     */
    static void showAllTypes() {
        final StringBuilder typesOutput = new StringBuilder();
        final Iterator<String> itTypes = types.keySet().iterator();
        while (itTypes.hasNext()) {
            final String typeName = itTypes.next();
            final SimpleXMLDocumentationType type = types.get(typeName);
            typesOutput.append("TYPE [").append(type.isPrimitive()).append("] : ").append(typeName).append("\n");
        }
        LOGGER.info("Liste des types : \n" + typesOutput);

    }

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** Types. */
    private static LinkedHashMap<String, SimpleXMLDocumentationType> types;

    /** root. */
    private final SimpleXMLDocumentationEntity root;

    /**
     * Constructor.
     *
     * @param clazz
     *            the clazz
     */
    private SimpleXMLDocumentation(final Class<?> clazz) {
        root = new SimpleXMLDocumentationEntity(clazz);
        registerType(clazz);
        root.parse();
    }

    /**
     * Gets the root.
     *
     * @return the root
     */
    public SimpleXMLDocumentationEntity getRoot() {
        return root;
    }
}
