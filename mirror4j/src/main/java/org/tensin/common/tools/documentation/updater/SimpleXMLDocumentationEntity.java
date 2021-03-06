package org.tensin.common.tools.documentation.updater;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * The Class SimpleXMLDocumentationEntity.
 *
 * @author j385649
 * @version $Revision: 1.4.2.5 $
 * @since 26 oct. 2011 14:40:52
 */
public class SimpleXMLDocumentationEntity {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** PATH_PREFIX. */
    public final static String PATH_PREFIX = "PATH_";

    /** Nom. */
    private String name;

    /** Balise XML. */
    private String baliseName;

    /** clazz. */
    private Class<?> clazz;

    /** inline. */
    private boolean inline;

    /** Description. */
    private String description;

    /** The example. */
    private String example;

    /** Required. */
    private boolean required;

    /** isText. */
    private boolean isText;

    /** isAttribute. */
    private boolean isAttribute;

    /** isElement. */
    private boolean isElement;

    /** isElementList. */
    private boolean isElementList;

    /** entités composantes. */
    private final LinkedHashMap<String, SimpleXMLDocumentationEntity> entities;

    /**
     * Constructor.
     *
     * @param clazz
     *            the clazz
     */
    public SimpleXMLDocumentationEntity(final Class<?> clazz) {
        this.clazz = clazz;
        name = clazz.getSimpleName();
        baliseName = name;
        entities = new LinkedHashMap<String, SimpleXMLDocumentationEntity>();
    }

    /**
     * Constructor.
     *
     * @param field
     *            the field
     */
    public SimpleXMLDocumentationEntity(final Field field) {
        clazz = field.getType();
        name = field.getName();
        baliseName = name;
        entities = new LinkedHashMap<String, SimpleXMLDocumentationEntity>();
    }

    /**
     * Constructor.
     *
     * @param method
     *            the method
     */
    public SimpleXMLDocumentationEntity(final Method method) {
        clazz = method.getReturnType();
        name = method.getName().toLowerCase().charAt(3) + method.getName().substring(4);
        baliseName = name;
        entities = new LinkedHashMap<String, SimpleXMLDocumentationEntity>();
    }

    /**
     * Constructor.
     *
     * @param name
     *            the name
     * @param baliseName
     *            the balise name
     * @param entityList
     *            the entity list
     */
    public SimpleXMLDocumentationEntity(final String name, final String baliseName, final List<SimpleXMLDocumentationEntity> entityList) {
        clazz = null;
        this.name = name;
        this.baliseName = baliseName;
        entities = new LinkedHashMap<String, SimpleXMLDocumentationEntity>();
        for (final SimpleXMLDocumentationEntity subEntity : entityList) {
            entities.put(subEntity.getName(), subEntity);
        }
    }

    /**
     * Adds the and parse.
     *
     * @param description
     *            the description
     * @param example
     *            the example
     * @param entityList
     *            the entity list
     */
    private void addAndParse(final String description, final String example, final List<SimpleXMLDocumentationEntity> entityList) {
        for (final SimpleXMLDocumentationEntity subEntity : entityList) {
            subEntity.setDescription(description);
            subEntity.setExample(example);
            entities.put(subEntity.getName(), subEntity);

            if (subEntity.getClazz() != null) {
                final boolean isTypeAlreadyDiscovered = (SimpleXMLDocumentation.getType(subEntity.getClazz()) != null);
                if (!isTypeAlreadyDiscovered) {
                    SimpleXMLDocumentation.registerType(subEntity.getClazz());
                    subEntity.parse();
                }
            } else {
                final boolean isPathAlreadyDiscovered = (SimpleXMLDocumentation.getType(subEntity.getName()) != null);
                if (!isPathAlreadyDiscovered) {
                    SimpleXMLDocumentation.registerType(subEntity.getName());
                    final Collection<SimpleXMLDocumentationEntity> subEntities = subEntity.getEntities().values();
                    for (final SimpleXMLDocumentationEntity se : subEntities) {
                        final boolean isTypeAlreadyDiscovered = (SimpleXMLDocumentation.getType(se.getClazz()) != null);
                        if (!isTypeAlreadyDiscovered) {
                            SimpleXMLDocumentation.registerType(se.getClazz());
                            se.parse();
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the balise name.
     *
     * @return the balise name
     */
    public String getBaliseName() {
        return baliseName;
    }

    /**
     * Gets the boolean value.
     *
     * @param annotation
     *            the annotation
     * @param fieldName
     *            the field name
     * @return the boolean value
     */
    private boolean getBooleanValue(final Annotation annotation, final String fieldName) {
        final Object o = AnnotationHelper.getFieldValue(annotation, fieldName);
        return ((Boolean) o).booleanValue();
    }

    /**
     * Method.
     *
     * @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the entities.
     *
     * @return the entities
     */
    public Map<String, SimpleXMLDocumentationEntity> getEntities() {
        return entities;
    }

    /**
     * Gets the example.
     *
     * @return the example
     */
    public String getExample() {
        return example;
    }

    /**
     * Method.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the primitive xsd.
     *
     * @return the primitive xsd
     */
    public String getPrimitiveXSD() {
        final String xsd = "xsd:string";
        if (isPrimitive()) {
            final Class<?> primitiveClass = getClazz();
            if (primitiveClass == String.class) {
                return xsd;
            }
            if ((primitiveClass == int.class) || (primitiveClass == Integer.class)) {
                return "xsd:integer";
            }
            if ((primitiveClass == long.class) || (primitiveClass == Long.class)) {
                return "xsd:long";
            }
            if ((primitiveClass == boolean.class) || (primitiveClass == Boolean.class)) {
                return "xsd:boolean";
            }
        }
        return xsd;
    }

    /**
     * Method.
     *
     * @return the type
     */
    public SimpleXMLDocumentationType getType() {
        if (clazz != null) {
            return SimpleXMLDocumentation.getType(clazz);
        } else {
            return SimpleXMLDocumentation.getType(name);
        }
    }

    /**
     * Gets the type name.
     *
     * @return the type name
     */
    public String getTypeName() {
        if (clazz == null) {
            // LOGGER.error("clazz null pour name="+name);
            return name;
        }
        final SimpleXMLDocumentationType type = getType();
        if (type != null) {
            return type.getName();
        } else {
            LOGGER.error("Type null pour name [" + name + "]");
            return "TYPENULL";
        }
    }

    /**
     * Checks if is attribute.
     *
     * @return true, if is attribute
     */
    public boolean isAttribute() {
        return isAttribute;
    }

    /**
     * Checks if is element.
     *
     * @return true, if is element
     */
    public boolean isElement() {
        return isElement;
    }

    /**
     * Checks if is element list.
     *
     * @return true, if is element list
     */
    public boolean isElementList() {
        return isElementList;
    }

    /**
     * Checks if is inline.
     *
     * @return true, if is inline
     */
    public boolean isInline() {
        return inline;
    }

    /**
     * Method.
     *
     * @return true, if is primitive
     */
    public boolean isPrimitive() {
        return getType().isPrimitive();
    }

    /**
     * Checks if is required.
     *
     * @return true, if is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Checks if is text.
     *
     * @return true, if is text
     */
    public boolean isText() {
        return isText;
    }

    /**
     * Method.
     *
     */
    public void parse() {

        /* Description de la classe */
        final SimpleXMLDocumentationType type = SimpleXMLDocumentation.getType(getClazz());
        for (final Annotation annotation : getClazz().getAnnotations()) {
            // LOGGER.info(">> Annotation trouvée, class [" + annotation.getClass().getName() + "], type réel [" + annotation.annotationType().getName() + "]");
            if (AnnotationHelper.isInstance(annotation, Description.class)) {
                type.setDescription((String) AnnotationHelper.getFieldValue(annotation, "value"));
                // type.setDocumentation(descriptionAnnotation.documentation());
            }
            if (AnnotationHelper.isInstance(annotation, Root.class)) {
                type.setBaliseName((String) AnnotationHelper.getFieldValue(annotation, "name"));
            }
        }

        final List<Class<?>> lstClass = new ArrayList<Class<?>>();
        Class<?> currentClass = getClazz();
        do {
            lstClass.add(0, currentClass);
            currentClass = currentClass.getSuperclass();
        } while ((currentClass != null) && (currentClass != Object.class));
        // StringBuffer sbClass = new StringBuffer("Empilement ["+getClazz()+"] :");
        // for (Class<?> clazz : lstClass) {
        // sbClass.append("\n  - "+clazz.getName());
        // }
        // LOGGER.info(sbClass.toString());

        /* Entités de la classe */
        for (final Class<?> usedClass : lstClass) {
            // LOGGER.info(">> Classe trouvée [" + usedClass.getName() + "]");
            for (final Field field : usedClass.getDeclaredFields()) {
                // LOGGER.info("  >> Field trouvé [" + field.getName() + "]");
                String fieldDescription = null;
                String fieldExample = null;
                String pathValue = null;
                List<SimpleXMLDocumentationEntity> entityList = new ArrayList<SimpleXMLDocumentationEntity>();
                for (final Annotation annotation : field.getAnnotations()) {
                    // LOGGER.info("    >> Annotation de champ trouvée, class [" + annotation.getClass().getName() + "], type réel [" + annotation.annotationType().getName() + "]");
                    if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.Text.class)) {
                        final SimpleXMLDocumentationEntity subEntity = new SimpleXMLDocumentationEntity(field);
                        subEntity.setText(true);
                        subEntity.setRequired(getBooleanValue(annotation, "required"));
                        fieldDescription = type.getDescription();
                        entityList.add(subEntity);
                    }
                    if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.Attribute.class)) {
                        final SimpleXMLDocumentationEntity subEntity = new SimpleXMLDocumentationEntity(field);
                        subEntity.setAttribute(true);
                        subEntity.setRequired(getBooleanValue(annotation, "required"));
                        subEntity.setBaliseName(StringUtils.defaultIfEmpty((String) AnnotationHelper.getFieldValue(annotation, "name"),
                                subEntity.getBaliseName()));
                        entityList.add(subEntity);
                    }
                    if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.Element.class)) {
                        final SimpleXMLDocumentationEntity subEntity = new SimpleXMLDocumentationEntity(field);
                        subEntity.setElement(true);
                        subEntity.setRequired(getBooleanValue(annotation, "required"));
                        subEntity.setBaliseName(StringUtils.defaultIfEmpty((String) AnnotationHelper.getFieldValue(annotation, "name"),
                                subEntity.getBaliseName()));
                        entityList.add(subEntity);
                    }
                    if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.ElementList.class)) {
                        final SimpleXMLDocumentationEntity subEntity = new SimpleXMLDocumentationEntity(field);
                        subEntity.setElementList(true);
                        final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        subEntity.setClazz((Class<?>) parameterizedType.getActualTypeArguments()[0]);
                        subEntity.setInline(getBooleanValue(annotation, "inline"));
                        subEntity.setRequired(getBooleanValue(annotation, "required"));
                        subEntity.setBaliseName(StringUtils.defaultIfEmpty((String) AnnotationHelper.getFieldValue(annotation, "name"),
                                subEntity.getBaliseName()));
                        entityList.add(subEntity);
                    }
                    if (AnnotationHelper.isInstance(annotation, ElementListUnion.class)) {
                        for (final Annotation o : (Annotation[]) AnnotationHelper.getFieldValue(annotation, "value")) {
                            final Class<?> parameterizedClass = (Class<?>) AnnotationHelper.getFieldValue(o, "type");
                            final SimpleXMLDocumentationEntity subEntity = new SimpleXMLDocumentationEntity(parameterizedClass);
                            subEntity.setElementList(true);
                            subEntity.setClazz(parameterizedClass);
                            subEntity.setInline(getBooleanValue(o, "inline"));
                            subEntity.setRequired(getBooleanValue(o, "required"));
                            subEntity.setBaliseName(StringUtils.defaultIfEmpty((String) AnnotationHelper.getFieldValue(o, "name"), subEntity.getBaliseName()));
                            entityList.add(subEntity);
                        }
                    }
                    if (AnnotationHelper.isInstance(annotation, Description.class)) {
                        fieldDescription = (String) AnnotationHelper.getFieldValue(annotation, "value");
                        fieldExample = (String) AnnotationHelper.getFieldValue(annotation, "example");
                    }
                    if (AnnotationHelper.isInstance(annotation, Path.class)) {
                        pathValue = (String) AnnotationHelper.getFieldValue(annotation, "value");
                    }
                }

                /* Cas où on trouve un @Path */
                if (pathValue != null) {
                    fieldDescription = StringUtils.defaultIfBlank(fieldDescription, "(@TODO) Description de " + pathValue);
                    final SimpleXMLDocumentationEntity pathEntity = new SimpleXMLDocumentationEntity(PATH_PREFIX + pathValue, pathValue, entityList);
                    pathEntity.setElement(true);
                    pathEntity.setRequired(false);
                    pathEntity.setDescription(fieldDescription);
                    pathEntity.setExample(fieldExample);
                    entityList = new ArrayList<SimpleXMLDocumentationEntity>();
                    entityList.add(pathEntity);
                } else {
                    /* Description par defaut */
                    fieldDescription = StringUtils.defaultIfBlank(fieldDescription, "(@TODO) Description de " + field.getName());
                }

                /* On ajoute et on part en recursif */
                addAndParse(fieldDescription, fieldExample, entityList);
            }

            /* Methodes */
            for (final Method method : usedClass.getDeclaredMethods()) {
                if (method.getName().startsWith("get")) {
                    String methodDescription = null;
                    String pathValue = null;
                    List<SimpleXMLDocumentationEntity> entityList = new ArrayList<SimpleXMLDocumentationEntity>();
                    for (final Annotation annotation : method.getAnnotations()) {
                        if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.Text.class)) {
                            LOGGER.error("Text pas implemente pour " + method.getName() + " sur " + usedClass.getName());
                        }
                        if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.Attribute.class)) {
                            // LOGGER.info("Attribute OK pour "+method.getName()+ " sur " + usedClass.getName());
                            final SimpleXMLDocumentationEntity subEntity = new SimpleXMLDocumentationEntity(method);
                            subEntity.setAttribute(true);
                            subEntity.setRequired(getBooleanValue(annotation, "required"));
                            subEntity.setBaliseName(StringUtils.defaultIfEmpty((String) AnnotationHelper.getFieldValue(annotation, "name"),
                                    subEntity.getBaliseName()));
                            entityList.add(subEntity);
                        }
                        if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.Element.class)) {
                            // LOGGER.info("Element OK pour "+method.getName()+ " sur " + usedClass.getName());
                            final SimpleXMLDocumentationEntity subEntity = new SimpleXMLDocumentationEntity(method);
                            subEntity.setElement(true);
                            subEntity.setRequired(getBooleanValue(annotation, "required"));
                            subEntity.setBaliseName(StringUtils.defaultIfEmpty((String) AnnotationHelper.getFieldValue(annotation, "name"),
                                    subEntity.getBaliseName()));
                            entityList.add(subEntity);
                        }
                        if (AnnotationHelper.isInstance(annotation, org.simpleframework.xml.ElementList.class)) {
                            LOGGER.error("ElementList pas implemente pour " + method.getName() + " sur " + usedClass.getName());
                        }
                        if (AnnotationHelper.isInstance(annotation, ElementListUnion.class)) {
                            LOGGER.error("ElementListUnion pas implemente pour " + method.getName() + " sur " + usedClass.getName());
                        }
                        if (AnnotationHelper.isInstance(annotation, Description.class)) {
                            methodDescription = (String) AnnotationHelper.getFieldValue(annotation, "value");
                        }
                        if (AnnotationHelper.isInstance(annotation, Path.class)) {
                            pathValue = (String) AnnotationHelper.getFieldValue(annotation, "value");
                        }
                    }

                    /* Cas où on truove un @Path */
                    if (pathValue != null) {
                        methodDescription = StringUtils.defaultIfBlank(methodDescription, "(@TODO) Description de " + pathValue);
                        final SimpleXMLDocumentationEntity pathEntity = new SimpleXMLDocumentationEntity(PATH_PREFIX + pathValue, pathValue, entityList);
                        pathEntity.setElement(true);
                        pathEntity.setRequired(false);
                        pathEntity.setDescription(methodDescription);
                        entityList = new ArrayList<SimpleXMLDocumentationEntity>();
                        entityList.add(pathEntity);
                    } else {
                        methodDescription = StringUtils.defaultIfBlank(methodDescription, "(@TODO) Description de " + method.getName());
                    }

                    /* On ajoute et on part en recursif */
                    addAndParse(methodDescription, "", entityList);
                }
            }

        }
    }

    /**
     * Sets the attribute.
     *
     * @param isAttribute
     *            the new attribute
     */
    public void setAttribute(final boolean isAttribute) {
        this.isAttribute = isAttribute;
    }

    /**
     * Sets the balise name.
     *
     * @param baliseName
     *            the new balise name
     */
    public void setBaliseName(final String baliseName) {
        this.baliseName = baliseName;
    }

    /**
     * Method.
     *
     * @param clazz
     *            the new clazz
     */
    public void setClazz(final Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Sets the element.
     *
     * @param isElement
     *            the new element
     */
    public void setElement(final boolean isElement) {
        this.isElement = isElement;
    }

    /**
     * Sets the element list.
     *
     * @param isElementList
     *            the new element list
     */
    public void setElementList(final boolean isElementList) {
        this.isElementList = isElementList;
    }

    /**
     * Sets the example.
     *
     * @param example
     *            the new example
     */
    public void setExample(final String example) {
        this.example = example;
    }

    /**
     * Sets the inline.
     *
     * @param inline
     *            the new inline
     */
    public void setInline(final boolean inline) {
        this.inline = inline;
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
     * Sets the required.
     *
     * @param required
     *            the new required
     */
    public void setRequired(final boolean required) {
        this.required = required;
    }

    /**
     * Sets the text.
     *
     * @param isText
     *            the new text
     */
    public void setText(final boolean isText) {
        this.isText = isText;
    }
}
