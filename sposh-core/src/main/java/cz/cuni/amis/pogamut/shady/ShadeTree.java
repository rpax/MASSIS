package cz.cuni.amis.pogamut.shady;

import cz.cuni.amis.pogamut.sposh.exceptions.MissingRootException;
import java.util.Collections;
import java.util.List;

/**
 * This class contains all nodes of one shade plan.
 * @author Honza
 */
public class ShadeTree {
    public final static String ROOT = "root";
    private final List<ShadeNode> nodesUm;

    /**
     * Create new tree containing all nodes.
     * @param nodes
     */
    ShadeTree(List<ShadeNode> nodes) {
        this.nodesUm = Collections.unmodifiableList(nodes);
    }

    /**
     * Find root of the plan.
     * @return the root. If no root is found, throw {@link MissingRootException}
     */
    public ShadeNode getRoot() {
        for (ShadeNode node : nodesUm)
            if (ROOT.equals(node.getName()))
                return node;
        throw new MissingRootException(ROOT);
    }

    /**
     * Find the node of this plan with specified name and return it.
     * @param name name of the node we are looking for.
     * @return found node or null if no such node exists.
     */
    public ShadeNode findNode(String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
