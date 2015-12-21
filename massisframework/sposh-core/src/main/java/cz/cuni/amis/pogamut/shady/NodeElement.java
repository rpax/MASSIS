package cz.cuni.amis.pogamut.shady;

import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;

/**
 * Every node has multiple elemnts, binary tree has two, our if-then tree
 * structure has a variable ammount. This class represents one of possible
 * choices in the tree engine can select.
 *
 * @author Honza
 */
public class NodeElement {

    private final IQuery priority;
    private final IQuery trigger;
    private final NodeCall call;

    /**
     * Create new node element.
     * @param priority query that is used to determine priority of this element
     * @param trigger trigger to determine if this node can be selected
     * @param call what to call if the element has the highest priority in
     *        the node and trigger is not zero.
     */
    NodeElement(IQuery priority, IQuery trigger, NodeCall call) {
        this.priority = priority;
        this.trigger = trigger;
        this.call = call;
    }

    /**
     * Get query that can be used to determine priority of this element.
     * @return the priority query
     */
    public IQuery getPriority() {
        return priority;
    }

    /**
     * Get query that can be used to determine if this element is eligible
     * for execution (i.e. if procondition for using call are fulfilles).
     * That alone is not sufficient, priority of this element must be highest
     * in the node.
     *
     * @return the trigger query.
     */
    public IQuery getTrigger() {
        return trigger;
    }

    /**
     * Get node call, that is either another node or action executed
     * by {@link IWorkExecutor}.
     *
     * @return the call
     */
    public NodeCall getCall() {
        return call;
    }


}
