package cz.cuni.amis.pogamut.shady;

import java.util.Collections;
import java.util.List;

/**
 * Representation of one node in the if-then tree. The root node is always
 * called "root" and all nodes must have different names.
 * @author Honza
 */
public class ShadeNode {

    private final String name;
    private final String descr;
    private final List<NodeElement> elementsUm;

    /**
     * Create new node with specified name, description and elements.
     * @param name name of the node, used in {@link NodeCall}
     * @param descr description of this node, what is what and so on
     * @param elements elements of this node, possible paths engine can choose
     *        when it descends into this node. Elements are evaluated using
     *        priority and trigger until the one with highest priority and
     *        enabled trigger is selected.
     */
    ShadeNode(String name, String descr, List<NodeElement> elements) {
        this.name = name;
        this.descr = descr;
        this.elementsUm = Collections.unmodifiableList(elements);
    }

    /**
     * Get identifier of the node. That has some special rules and is quite
     * similar to FQN name of class.
     * @return identifier of node.
     */
    public String getName() {
        return name;
    }

    /**
     * Get list of all elements of this node.
     * @return unmodifiable list
     */
    public List<NodeElement> getElements() {
        return elementsUm;
    }

}
