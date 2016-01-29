package cz.cuni.amis.pogamut.sposh.elements;

import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeEvent;
import java.util.*;

/**
 * Base class for each element of Yaposh plan. It can register listeners on
 * changes of this element and has link to parent element.
 *
 * @author HonzaH
 * @param T Type of this. Needed for listeners, so Drive is Drive&lt;Drive&gt;
 */
public abstract class PoshElement<T extends PoshElement, PARENT extends PoshElement> {

    /**
     * PoshElement, that is parent of this DN. Root doesn't have one (is
     * <code>null</code>), rest does have.
     */
    private PARENT parent;
    /**
     * Listeners on events of this DN.
     */
    private final Set<PoshElementListener<T>> elementListeners = new HashSet<PoshElementListener<T>>();
    private final Set<PoshElementListener<T>> elementListenersUm = Collections.unmodifiableSet(elementListeners);

    /**
     * Get list of listeners that listen for changes of this node (new child,
     * node deletion, childMoved and change of properties)
     *
     * @return unmodifiable list of listeners, never null,
     */
    public final Set<PoshElementListener<T>> getElementListeners() {
        return elementListenersUm;
    }

    /**
     * Add new listener for events of this node.
     *
     * @param listener listener to add
     * @return <tt>true</tt> if listener was added. <tt>false</tt> if listener
     * was already among listeners.
     */
    public final synchronized boolean addElementListener(PoshElementListener<T> listener) {
        return elementListeners.add(listener);
    }

    /**
     * Remove listener from list of listeners of this node.
     *
     * @param listener listener to remove
     * @return Did listeners of this element of this element contain passed
     * listener?
     */
    public final synchronized boolean removeElementListener(PoshElementListener<T> listener) {
        return elementListeners.remove(listener);
    }

    /**
     * Notify all listeners about change of a property.
     *
     * @param name name of property
     * @param o old value, can be null, but reciever has to be able to handle it
     * @param n new value, can be null, but reciever has to be able to handle it
     */
    protected final synchronized void firePropertyChange(String name, Object o, Object n) {
        PoshElementListener[] listeners = elementListenersUm.toArray(new PoshElementListener[0]);
        for (PoshElementListener listener : listeners) {
            listener.propertyChange(new PropertyChangeEvent(this, name, o, n));
        }
    }

    /**
     * Get data flavour of posh plan element,used during DnD from palette to
     * PoshScene.
     *
     * @return dataFlavour of posh plan element, never null.
     */
    public abstract DataFlavor getDataFlavor();

    /**
     * Get type of the element.
     *
     * @return Type of element.
     */
    public abstract LapType getType();

    /**
     * Get list of children of this node. Most likely auto generated every time
     * this method is called.
     *
     * @return List of all children of this node.
     */
    public abstract List<? extends PoshElement> getChildDataNodes();

    /**
     * Notify all listeners (mostly associated Widgets) that this dataNode has a
     * new child. After that emit recursivly children of added element in.
     *
     * @param childNode
     */
    protected final synchronized void emitChildNode(PoshElement emitedChild) {
        // emit child
        PoshElementListener<T>[] listenersArray = elementListenersUm.toArray(new PoshElementListener[]{});
        for (PoshElementListener<T> listener : listenersArray) {
            listener.childElementAdded((T) this, emitedChild);
        }
    }

    /**
     * Notify all listeners (associated Widgets) that one child of this node has
     * changed order(position).
     *
     * @param childNode
     */
    protected final synchronized void emitChildMove(PoshElement childNode, int oldIndex, int newIndex) {
        PoshElementListener[] listenersArray = elementListenersUm.toArray(new PoshElementListener[]{});

        for (PoshElementListener<T> listener : listenersArray) {
            listener.childElementMoved((T) this, childNode, oldIndex, newIndex);
        }
    }

    /**
     * Tell all listeners that a child of this element has been deleted. Dont
     * remove listeners, it is job of listener to remove itself.
     */
    protected final synchronized void emitChildDeleted(PoshElement child, int removedChildPosition) {
        PoshElementListener[] listenersArray = elementListenersUm.toArray(new PoshElementListener[]{});

        for (PoshElementListener<T> listener : listenersArray) {
            listener.childElementRemoved((T) this, child, removedChildPosition);
        }
    }

    /**
     * Set parent
     * <code>PoshElement</code> of the node.
     *
     * @param parent
     */
    protected void setParent(PARENT parent) {
        this.parent = parent;
    }

    /**
     * Get parent of the node. Null, if the node is root.
     *
     * @return parent of the node, or null if node is root.
     */
    public PARENT getParent() {
        return this.parent;
    }

    /**
     * Get root node of POSH plan this node belongs to.
     *
     * @return Root node of POSH plan or null if I am unable to reach it.
     */
    public final PoshPlan getRootNode() {
        PoshElement cur = this;
        while (cur.getParent() != null) {
            cur = cur.getParent();
        }
        if (cur instanceof PoshPlan) {
            return (PoshPlan) cur;
        }
        return null;
    }

    /**
     * Move child to the @newIndex. After child was moved (if it was moved),
     * notify listeners.
     *
     * @return if node succesfully moved
     */
    public abstract boolean moveChild(int newIndex, PoshElement child);

    /**
     * Is this element present as a a child in its parent? If element has no
     * parent, return false. Used during adding of a new element as a sanity
     * check (two elements could think that the node is their child).
     *
     * @return true if element is a child of parent, false if element is not a
     * child of its parent or it has no parent at all (null).
     */
    protected final boolean isChildOfParent() {
        if (parent != null) {
            return parent.getChildDataNodes().contains(this);
        }
        return false;
    }

    /**
     * Get all children of this element that have @selectType.
     *
     * @param selectType Type of children we are looking for.
     * @return Newly allocated list of all children of this node that have {@link #getType()
     * } == @selectType.
     */
    public final List<? extends PoshElement> getChildren(LapType selectType) {
        List<PoshElement> selectedChildren = new LinkedList<PoshElement>();
        for (PoshElement node : this.getChildDataNodes()) {
            if (node.getType() == selectType) {
                selectedChildren.add(node);
            }
        }
        return selectedChildren;
    }

    /**
     * Get index of a @child. Id is determined as index of @child in all
     * children of same type.
     */
    public final int getChildId(PoshElement child) {
        assert child.getParent() == this;
        List<? extends PoshElement> sameTypeChildren = getChildren(child.getType());
        return sameTypeChildren.indexOf(child);
    }
    
    /**
     * Get Id of this element.
     * @throws NullPointerException if parent is null.
     */
    public final int getId() {
        return parent.getChildId(this);
    }
}
