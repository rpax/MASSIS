package cz.cuni.amis.pogamut.sposh.elements;

import java.awt.Desktop;
import java.beans.PropertyChangeListener;

/**
 * Mainly for widgets. This is the listener that can be registered with the
 * {@link PoshElement} to listen for various events that happens to the POSH
 * element (new node, deleted node...)
 * <p/>
 * Thanks to generics, listeners can have type check for parent, but not for
 * child because of multiple possible types of children (e.g. DC has two types
 * of children, {@link Sense} and {@link DriveElement}, {@link PoshPlan} has {@link ActionPattern}
 * and {@link Competence}).
 *
 * @author HonzaH
 * @param <PARENT> Type of parent that the listeners will be notified about.
 */
public interface PoshElementListener<PARENT extends PoshElement> extends PropertyChangeListener {

    /**
     * Event handler will be notified that parent has added a new child among
     * its children.
     *
     * @param parent parent that has a new child
     * @param child Child that has been added.
     */
    void childElementAdded(PARENT parent, PoshElement child);

    /**
     * Evenet handler will be notified that parent has moved a child.
     *
     * @param parent Parent element of child.
     * @param child child that has been moved.
     * @param oldIndex Index of the child before move.
     * @param newIndex Absolute position of the moved child with its own
     * type. E.g. {@link DriveCollection} has two types of children, {@link Sense}s
     * and {@link DriveElement}. First sense has absolute index 0 and first
     * drive has absolute index 0.
     */
    void childElementMoved(PARENT parent, PoshElement child, int oldIndex, int newIndex);

    /**
     * Event handler will be notified when parent has removed child.
     *
     * @param parent parent element of child
     * @param child child element that has been removed
     * @param removedChildPosition What was position of removed child among
     * children
     */
    void childElementRemoved(PARENT parent, PoshElement child, int removedChildPosition);
}
