package org.tensin.common.tools.documentation.updater;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class SimpleXMLDocumentationOutputAPT.
 */
public class SimpleXMLDocumentationOutputAPT implements
ISimpleXMLDocumentationOutput {

    /** DEFAULT_BASE_REPLACEMENT_TOKEN. */
    private static final String DEFAULT_BASE_REPLACEMENT_TOKEN = "AUTOGENERATED_CONTENT";

    /** syntaxer. */
    protected ISyntaxer syntaxer;

    /** baseReplacementToken. */
    private String baseReplacementToken = DEFAULT_BASE_REPLACEMENT_TOKEN;

    /**
     * Method.
     * 
     * @param currentEntity
     *            the current entity
     * @param alreadyDisplayed
     *            the already displayed
     * @return the string
     */
    protected String format(final SimpleXMLDocumentationEntity currentEntity,
            final Set<String> alreadyDisplayed) {
        /* Doublons */
        // currentEntity.checkDoublons(currentEntity.getType().getBaliseName(),
        // currentEntity.getType().getName());

        /* Titre */
        final StringBuffer sb = new StringBuffer();
        sb.append(syntaxer.buildHeader(2, currentEntity.getType().getAnchor(),
                currentEntity.getType().getAnchor()));
        sb.append(currentEntity.getType().getDescription() + "\n\n");

        /* Exemple */
        final StringBuffer sbExemple = new StringBuffer(syntaxer.buildHeader(3,
                "Example"));
        sbExemple.append(syntaxer.buildQuoteStart());
        sbExemple.append("&lt;" + currentEntity.getType().getBaliseName());

        /* Enum */
        if (currentEntity.getType().isEnum()) {
            final StringBuffer sbEnum = new StringBuffer(syntaxer.buildHeader(3,
                    "Enumeration"));
            sbEnum.append(syntaxer.buildTableHeader(new String[] { "Valeur" }));
            // sbEnum.append("*-----------------+\n");
            // sbEnum.append("| <<Valeur>>      |\n");
            // sbEnum.append("*-----------------+\n");
            final List<String> enumerations = currentEntity.getType()
                    .getEnumeration();
            for (final String enumeration : enumerations) {
                sbEnum.append(syntaxer
                        .buildTableRow(new String[] { enumeration }));
                // sbEnum.append("| " + enumeration + "|\n");
                // sbEnum.append("*-----------------+\n");
            }
            sbEnum.append(syntaxer.buildTableFooter());
            sb.append(sbEnum + "\n\n");
        }

        /* Attributs */
        final int initialAttributeLineSize = ("&lt;" + currentEntity.getType()
                .getBaliseName()).length();
        int currentAttributeLineSize = initialAttributeLineSize;
        String currentAttributeLine = "";
        int nbAttributs = 0;
        final StringBuffer sbAttributs = new StringBuffer(syntaxer.buildHeader(3,
                "Attributes"));
        sbAttributs.append(syntaxer.buildTableHeader(new String[] {
                "Attributes", "Type", "Required", "Description"

        }));
        // sbAttributs.append("*-----------------+-------------------+-------*-------------------+\n");
        // sbAttributs.append("| <<Attributs>>   | <<Type>>          |<<Req>>| <<Description>>   |\n");
        // sbAttributs.append("*-----------------+-------------------+-------*-------------------+\n");
        Iterator<String> itEntities = currentEntity.getEntities().keySet()
                .iterator();
        while (itEntities.hasNext()) {
            final String name = itEntities.next();
            final SimpleXMLDocumentationEntity entity = currentEntity.getEntities()
                    .get(name);
            if (entity.isAttribute()) {
                nbAttributs++;
                sbAttributs.append(syntaxer.buildTableRow(new String[] {
                        entity.getBaliseName(), getTableLink(entity),
                        getRequired(entity), entity.getDescription() }));
                // sbAttributs.append("| " + entity.getBaliseName() + " | " +
                // getLink(entity) + " | " + getRequired(entity) + " | " +
                // entity.getDescription() + " |\n");
                // sbAttributs.append("*-----------------+-------------------+-------*-------------------+\n");

                String attributeExemple = " " + entity.getBaliseName();
                if (StringUtils.isEmpty(entity.getExample())) {
                    attributeExemple += "='...'";
                } else {
                    attributeExemple += "='" + entity.getExample() + "'";
                }
                if ((currentAttributeLineSize + attributeExemple.length()) > 80) {
                    sbExemple.append(currentAttributeLine + "\n");
                    currentAttributeLine = StringUtils.repeat(" ",
                            initialAttributeLineSize);
                    currentAttributeLineSize = initialAttributeLineSize;
                }
                currentAttributeLine += attributeExemple;
                currentAttributeLineSize += attributeExemple.length();

            }
        }
        sbAttributs.append(syntaxer.buildTableFooter());
        sbExemple.append(currentAttributeLine + "&gt;");
        if (nbAttributs > 0) {
            sb.append(sbAttributs + "\n\n");
        }

        /* Element */
        int nbElements = 0;
        final StringBuffer sbElements = new StringBuffer(syntaxer.buildHeader(3,
                "Elements"));
        sbElements.append(syntaxer.buildTableHeader(new String[] { "Elements",
                "Type", "Required", "Description"

        }));
        // sbElements.append("*-----------------+-------------------+-------*-------------------+\n");
        // sbElements.append("| <<Elements>>    | <<Type>>          |<<Req>>| <<Description>>   |\n");
        // sbElements.append("*-----------------+-------------------+-------*-------------------+\n");
        itEntities = currentEntity.getEntities().keySet().iterator();
        while (itEntities.hasNext()) {
            final String name = itEntities.next();
            final SimpleXMLDocumentationEntity entity = currentEntity.getEntities()
                    .get(name);
            if (entity.isElement()) {
                nbElements++;
                sbElements.append(syntaxer.buildTableRow(new String[] {
                        entity.getBaliseName(), getTableLink(entity),
                        getRequired(entity), entity.getDescription() }));
                // sbElements.append("| " + entity.getBaliseName() + " | " +
                // getLink(entity) + " | " + getRequired(entity) + " | " +
                // entity.getDescription() + " |\n");
                // sbElements.append("*-----------------+-------------------+-------*-------------------+\n");
                // sbElements.append("*-----------------+-------------------+---+-------------------+\n");

                sbExemple.append("\n   &lt;!-- Description de '"
                        + entity.getBaliseName() + "' --&gt;");
                sbExemple.append("\n   &lt;" + entity.getBaliseName() + "&gt;");
                if (entity.isPrimitive()) {
                    sbExemple.append(". . .");
                } else {
                    sbExemple.append("\n   . . .\n   ");
                }
                sbExemple.append("&lt;/" + entity.getBaliseName() + "&gt;\n");
            } else if (entity.isElementList()) {
                nbElements++;
                if (entity.isInline()) {
                    sbElements.append(syntaxer.buildTableRow(new String[] {
                            "List of " + getTableLink(entity),
                            getTableLink(entity), getRequired(entity),
                            entity.getDescription() }));
                    // sbElements.append("| Liste de " + getLink(entity) + " | "
                    // + getLink(entity) + " | " + getRequired(entity) + " | " +
                    // entity.getDescription() + " |\n");

                    sbExemple.append("\n   &lt;!-- List of '"
                            + entity.getType().getBaliseName() + "' --&gt;");
                    sbExemple.append("\n   &lt;"
                            + entity.getType().getBaliseName() + "&gt;");
                    sbExemple.append("\n   . . .");
                    sbExemple.append("\n   &lt;/"
                            + entity.getType().getBaliseName() + "&gt;");
                    sbExemple.append("\n   .");
                    sbExemple.append("\n   .");
                    sbExemple.append("\n   .");
                    sbExemple.append("\n   &lt;"
                            + entity.getType().getBaliseName() + "&gt;");
                    sbExemple.append("\n   . . .");
                    sbExemple.append("\n   &lt;/"
                            + entity.getType().getBaliseName() + "&gt;\n");
                } else {
                    sbElements.append(syntaxer.buildTableRow(new String[] {
                            entity.getBaliseName(), getTableLink(entity),
                            getRequired(entity), entity.getDescription() }));
                    // sbElements.append("| " + entity.getBaliseName() +
                    // " | Liste de " + getLink(entity) + " | " +
                    // getRequired(entity) + " | " + entity.getDescription() +
                    // " |\n");
                    sbExemple.append("\n   &lt;!-- Description de '"
                            + entity.getBaliseName() + "' --&gt;");
                    sbExemple.append("\n   &lt;" + entity.getBaliseName()
                            + "&gt;");
                    sbExemple.append("\n   . . .");
                    sbExemple.append("\n   &lt;/" + entity.getBaliseName()
                            + "&gt;\n");
                }
            }
        }
        if (nbElements > 0) {
            sbElements.append(syntaxer.buildTableFooter());
            sb.append(sbElements).append("\n\n");
        }

        /* Text */
        int nbTexts = 0;
        final StringBuffer sbTexts = new StringBuffer(syntaxer.buildHeader(3,
                "Content"));
        sbTexts.append(syntaxer.buildTableHeader(new String[] { "Type", "Required",
                "Description"

        }));
        // sbTexts.append("*-------------------+-------*-------------------+\n");
        // sbTexts.append("| <<Type>>          |<<Req>>| <<Description>>   |\n");
        // sbTexts.append("*-------------------+-------*-------------------+\n");
        itEntities = currentEntity.getEntities().keySet().iterator();
        while (itEntities.hasNext()) {
            final String name = itEntities.next();
            final SimpleXMLDocumentationEntity entity = currentEntity.getEntities()
                    .get(name);
            if (entity.isText()) {
                nbTexts++;
                sbTexts.append(syntaxer.buildTableRow(new String[] {
                        getTableLink(entity), getRequired(entity),
                        entity.getDescription() }));
                // sbTexts.append("| " + getLink(entity) + " | " +
                // getRequired(entity) + " | " + entity.getDescription() +
                // " |\n");
                // sbTexts.append("*-------------------+-------*-------------------+\n");
                sbExemple.append("...");
            }
        }
        if (nbTexts > 0) {
            sbTexts.append(syntaxer.buildTableFooter());
            sb.append(sbTexts + "\n\n");
        }

        /* Fin de l'exemple */
        if ((nbElements == 0) && (nbTexts == 0)) {
            final int s = sbExemple.length();
            sbExemple.delete(s - 4, s).append(" /&gt;\n");
        } else {
            sbExemple.append("&lt;/" + currentEntity.getType().getBaliseName()
                    + "&gt;\n");
        }
        sbExemple.append(syntaxer.buildQuoteEnd()).append("\n");
        if (!currentEntity.getType().isEnum()) {
            sb.append(sbExemple);
        }

        /* Classe d'implementation */
        if (currentEntity.getType().getName().indexOf(".") > -1) {
            final String fullName = currentEntity.getType().getName();
            final String packageName = fullName.substring(0,
                    fullName.lastIndexOf("."));
            final String className = fullName
                    .substring(fullName.lastIndexOf(".") + 1);
            sb.append(syntaxer.buildHeader(3, "Implementation"));
            sb.append(syntaxer.buildTableHeader(2));
            sb.append(syntaxer.buildTableRow(new String[] { "Package",
                    packageName }));
            sb.append(syntaxer
                    .buildTableRow(new String[] { "Class", className }));

            // sb.append("*----------+---------------------------------------------------------------+\n");
            // sb.append("| Package  | " + packageName + " |\n");
            // sb.append("*----------+---------------------------------------------------------------+\n");
            // sb.append("| Classe   | " + className + " |\n");
            // sb.append("*----------+---------------------------------------------------------------+\n\n");
            sb.append(syntaxer.buildTableFooter());
        }

        /* Documentation */
        final String documentation = currentEntity.getType().getDocumentation();
        if (StringUtils.isNotBlank(documentation)) {
            final StringBuffer sbDocumentation = new StringBuffer(
                    syntaxer.buildHeader(3, "Documentation"));
            sbDocumentation
            .append("    Une documentation complète est disponible {{{"
                    + documentation + "}ici}}\n");
            sb.append(sbDocumentation);
        }

        /* Entités suivantes.... */
        itEntities = currentEntity.getEntities().keySet().iterator();
        while (itEntities.hasNext()) {
            final String name = itEntities.next();
            final SimpleXMLDocumentationEntity subEntity = currentEntity
                    .getEntities().get(name);
            if (!subEntity.isPrimitive()
                    && !alreadyDisplayed.contains(subEntity.getTypeName())) {
                alreadyDisplayed.add(subEntity.getTypeName());
                sb.append("\n\n\n" + format(subEntity, alreadyDisplayed));

            }
        }

        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tensin.beerduino.tools.ISimpleXMLDocumentation#generate(org.tensin
     * .beerduino.tools.SimpleXMLDocumentationEntity)
     */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.tools.documentation.updater.ISimpleXMLDocumentationOutput#generate(java.lang.Class,
     *      org.tensin.common.tools.documentation.updater.SimpleXMLDocumentationEntity)
     */
    @Override
    public String generate(final Class<?> racine,
            final SimpleXMLDocumentationEntity entity)
                    throws SimpleXMLDocumentationException {
        syntaxer = new SyntaxerAPT();
        final StringBuilder sb = new StringBuilder("{{Documentation}} ");
        sb.append("(" + getCurrentDateWithDateFormat("yyyy-MM-dd HH:mm:ss")
                + ")\n\n");
        sb.append(summaryInAPT(1, entity, 1));
        sb.append(format(entity, new TreeSet<String>()));
        return sb.toString();
    }

    /**
     * Gets the base replacement token.
     * 
     * @return the base replacement token
     */
    private String getBaseReplacementToken() {
        return baseReplacementToken;
    }

    /**
     * Méthode getCurrentDateWithDateFormat.
     * 
     * @param pattern
     *            the pattern
     * @return current date with date format String
     */
    public String getCurrentDateWithDateFormat(final String pattern) {
        return getDateWithDateFormat(pattern, new Date());
    }

    /**
     * Méthode getDateWithDateFormat.
     * 
     * @param pattern
     *            the pattern
     * @param date
     *            the date
     * @return the date with date format String
     */
    public String getDateWithDateFormat(final String pattern, final Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * Method.
     * 
     * @param entity
     *            the entity
     * @return the link
     */
    private String getLink(final SimpleXMLDocumentationEntity entity) {
        if (entity.isPrimitive()) {
            return entity.getType().getSimpleName();
        } else {
            return syntaxer.buildLink(entity.getType().getBaliseName(), entity
                    .getType().getAnchor());
        }
    }

    /**
     * Method.
     * 
     * @param entity
     *            the entity
     * @return the required
     */
    private String getRequired(final SimpleXMLDocumentationEntity entity) {
        if (entity.isRequired()) {
            return " X ";
        } else {
            return "   ";
        }
    }

    /**
     * Method.
     * 
     * @return the syntaxer
     */
    public ISyntaxer getSyntaxer() {
        return syntaxer;
    }

    /**
     * Gets the table link.
     *
     * @param entity the entity
     * @return the table link
     */
    private String getTableLink(final SimpleXMLDocumentationEntity entity) {
        if (entity.isPrimitive()) {
            return entity.getType().getSimpleName();
        } else {
            return syntaxer.buildHTMLLink(entity.getType().getBaliseName(),
                    entity.getType().getAnchor());
        }
    }

    /**
     * Checks if is mode merge.
     * 
     * @return true, if is mode merge {@inheritDoc}
     * @see org.tensin.common.tools.documentation.updater.ISimpleXMLDocumentationOutput#isModeMerge()
     */
    @Override
    public boolean isModeMerge() {
        return StringUtils.isNotEmpty(getBaseReplacementToken());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.tools.documentation.updater.ISimpleXMLDocumentationOutput#mergeContent(java.lang.Class,
     *      org.tensin.common.tools.documentation.updater.SimpleXMLDocumentationEntity,
     *      java.lang.String)
     */
    @Override
    public String mergeContent(final String newContent,
            final String sourceFileContent)
                    throws SimpleXMLDocumentationException {
        final String startToken = syntaxer
                .buildCommentStart(getBaseReplacementToken());
        final String endToken = syntaxer.buildCommentEnd(getBaseReplacementToken());
        try {
            final String existingContent = org.apache.commons.io.FileUtils
                    .readFileToString(new File(sourceFileContent));
            return mergeContent(existingContent, newContent, startToken,
                    endToken);
        } catch (final IOException e) {
            throw new SimpleXMLDocumentationException(e);
        }

    }

    /**
     * Method.
     * 
     * @param existingContent
     *            the existing content
     * @param newContent
     *            the new content
     * @param startToken
     *            the start token
     * @param endToken
     *            the end token
     * @return the string
     */
    private String mergeContent(final String existingContent,
            final String newContent, final String startToken,
            final String endToken) {
        if ((existingContent == null) || StringUtils.isEmpty(startToken)
                || StringUtils.isEmpty(endToken)) {
            return null;
        }
        final int strLen = existingContent.length();
        final int openLen = startToken.length();
        final int closeLen = endToken.length();
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        int previousEnd = 0;
        while (pos < (strLen - closeLen)) {
            int start = existingContent.indexOf(startToken, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            final int end = existingContent.indexOf(endToken, start);
            if (end < 0) {
                break;
            }
            sb.append(existingContent.substring(previousEnd, start)).append(
                    "\n");
            sb.append(newContent).append("\n");
            sb.append(endToken);
            pos = end + closeLen;
            previousEnd = pos;
        }
        // On copie la fin ...
        if (previousEnd >= 0) {
            sb.append(existingContent.subSequence(previousEnd, strLen));
        }

        return sb.toString();
    }

    /**
     * Sets the base replacement token.
     * 
     * @param baseReplacementToken
     *            the new base replacement token
     */
    public void setBaseReplacementToken(final String baseReplacementToken) {
        this.baseReplacementToken = baseReplacementToken;
    }

    /**
     * Method.
     * 
     * @param syntaxer
     *            the new syntaxer
     */
    public void setSyntaxer(final ISyntaxer syntaxer) {
        this.syntaxer = syntaxer;
    }

    /**
     * Method.
     * 
     * @param profondeur
     *            the profondeur
     * @param root
     *            the root
     * @param indice
     *            the indice
     * @return the string
     */
    private String summaryInAPT(final int profondeur,
            final SimpleXMLDocumentationEntity root, final int indice) {
        final StringBuffer sb = new StringBuffer();
        sb.append("    " + StringUtils.repeat(" ", profondeur * 2) + "* {{{"
                + root.getType().getAnchor() + "}");
        sb.append(root.getType().getBaliseName() + "}}\n\n");
        final Collection<SimpleXMLDocumentationEntity> subEntities = root
                .getEntities().values();
        for (final SimpleXMLDocumentationEntity subEntity : subEntities) {
            if (subEntity.isElement() || subEntity.isElementList()
                    || subEntity.isText()) {
                if (!subEntity.isPrimitive()) {
                    sb.append(summaryInAPT(profondeur + 1, subEntity, indice));
                }
            }
        }
        return sb.toString();
    }

}