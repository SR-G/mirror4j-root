package org.tensin.mirror4j.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.tensin.mirror4j.model.State;

/**
 * The Class CollectionHelper.
 */
public class CollectionHelper {

    /**
	 * Convert collection to string.
	 *
	 * @param items the items
	 * @return the string
	 */
	public static String convertCollectionToString(Iterable<?> items) {
        final StringBuilder sb = new StringBuilder();
        String sep = "";
        for (final Object item : items) {
        	sb.append(sep).append(item.toString());
        	sep = ", ";
        }
		return sb.toString();
	}

    /**
     * Method. Convert a string separated by comma to a list
     * 
     * @param value
     *            The values in a string
     * @return Collection
     */
    public static Collection<String> convertStringToCollection(final String value) {
        final Collection<String> result = new ArrayList<String>();
        if (StringUtils.isNotEmpty(value)) {
            final String[] tempArray = value.split("[ ,;:]+");
            for (final String element : tempArray) {
                result.add(element.trim());
            }
        }
        return result;
    }

    /**
     * Dump du contenu d'une liste.
     * 
     * @param l
     *            La liste à dumper
     * @return La représentation textuelle
     */
    public static String singleDump(final Collection<?> l) {
        final StringBuilder sb = new StringBuilder();
        if (l == null) {
            sb.append("[]");
        } else if (l.isEmpty()) {
            sb.append("[]");
        } else {
            final Iterator<?> iterator = l.iterator();
            int cnt = 0;
            sb.append("[");
            while (iterator.hasNext()) {
                if (cnt++ > 0) {
                    sb.append(", ");
                }
                sb.append(iterator.next().toString());
            }
            sb.append("]");
        }
        return (sb.toString());
    }

	/**
     * Single dump.
     *
     * @param l the l
     * @return the string
     */
    public static String singleDump(final Object[] l) {
        final StringBuilder sb = new StringBuilder();
        if (l == null) {
            sb.append("[]");
        } else if (l.length == 0) {
            sb.append("[]");
        } else {
            int cnt = 0;
            sb.append("[");
            for (final Object o : l) {
                if (cnt++ > 0) {
                    sb.append(", ");
                }
                sb.append(o.toString());
            }
            sb.append("]");
        }
        return (sb.toString());
    }
}
