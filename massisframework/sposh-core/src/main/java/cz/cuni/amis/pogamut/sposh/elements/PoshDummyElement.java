package cz.cuni.amis.pogamut.sposh.elements;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * This is class used as intermediate class between PoshElement and DataNodes of
 * POSH elements. Some common functions are implemented here.
 *
 * @author HonzaH
 */
public abstract class PoshDummyElement<THIS extends PoshElement, PARENT extends PoshElement> extends PoshElement<THIS, PARENT> {

    /**
     * Regular pattern, only text that matches this pattern can be a name of the
     * POSH element. This pattern must be same as token
     * <code>NAME</code> in the PoshParser.jj
     * <p/>
     * Because of dash (-), not all strings that match this pattern are FQN.
     */
    public static final String IDENT_PATTERN = "([a-zA-Z][_\\-a-zA-Z0-9]*\\.)*[a-zA-Z][_\\-a-zA-Z0-9]*";

    /**
     * Take the @list of elements and one @child of the @list. Move the @child
     * so that it is at the @newIndex and the rest of elements keep their order.
     * Once it is done, emit the event. If @child is not part of the @list,
     * throw {@link NoSuchElementException}, if @newIndex is same as the index
     * the @child is currently at, don't do anything.
     *
     * @param <T>
     * @param list
     * @param child
     * @param newIndex Index
     * @return did method move and emited?
     */
    protected <T extends PoshElement> boolean moveChildInList(List<T> list, T child, int newIndex) {
        int oldIndex = list.indexOf(child);
        if (oldIndex == -1) {
            throw new NoSuchElementException("Child " + child + "is not in collection.");
        }
        if (newIndex == oldIndex) {
            return false;
        }

        list.remove(oldIndex);
        list.add(newIndex, child);

        emitChildMove(child, oldIndex, newIndex);

        return true;
    }

    /**
     * Is passed name used by one of elements?
     *
     * @param name name to be tested
     * @param elements elements againt which the test is done
     * @return true if name is used, false otherwise
     */
    protected static boolean isUsedName(String name, List<? extends INamedElement> elements) {
        for (INamedElement element : elements) {
            String elementName = element.getName();
            if (elementName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get valid name for new element, but name can't be same as any name used
     * by passed elements.
     *
     * @param template string used as prefix of created name
     * @param elements elements that must have different name that created name
     * @return valid name that can be immediately used as OK name of new drive.
     */
    protected static String getUnusedName(String template, List<? extends INamedElement> elements) {
        int i = 1;
        while (isUsedName(template + i, elements)) {
            i++;
        }
        return template + i;
    }

    /**
     * Get index of @element in all @elements.
     *
     * @param <ELEMENT> Type of elements.
     * @param elements List of elements where
     * @param element element for which we are looking for in elements.
     * @return Found index.
     * @throws IllegalArgumentException If element is not among elements.
     */
    protected <ELEMENT extends PoshElement & INamedElement> int getElementId(List<ELEMENT> elements, ELEMENT element) {
        int elementIndex = elements.indexOf(element);
        if (elementIndex == -1) {
            throw new IllegalArgumentException("Element of type '" + element.getType() + "' with name '" + element.getName() + "' not present in the plan.");
        }
        return elementIndex;
    }
}
