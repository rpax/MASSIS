package cz.cuni.amis.pogamut.shady;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.MissingRootException;
import cz.cuni.amis.pogamut.sposh.exceptions.NoEligibleElementException;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class is taking a shade plan (already parsed in form of {@link ShadeTree}
 * and executing it using supplied {@link IWorkExecutor}.
 *
 * Shade engine is an if-then tree like structure, that is evaluated during
 * every iteration. It is very simple, but it should be the advantage, not
 * disadvantage. Posh engine is very complicated with many features very few
 * know about, much less actually use.
 *
 * @author Honza
 */
public class ShadeEngine<EXECUTOR extends IWorkExecutor> {

    private final ShadeTree plan;
    private final EXECUTOR executor;

    /**
     * Create new engine with set plan and executor.
     * @param plan plan that this engine will execute.
     * @param executor executor used to execute queries and actions.
     */
    public ShadeEngine(ShadeTree plan, EXECUTOR executor) {
        this.plan = plan;
        this.executor = executor;
    }

    /**
     * Take the plan evaluate it and run the target primitive.
     * 
     */
    public void evaluate() throws MissingRootException, NoEligibleElementException {
        ShadeNode node = plan.getRoot();

        NodeCall call = selectCall(node);

        // Ok, now we have call that is not another node
    }

    /**
     * Go throught the node tree and find the leaf ({@link NodeElement) that
     * doesn't call another {@link ShadeNode node} and return the call of
     * the leaf.
     * @param node node where we start the search. Node must be part
     *             of the {@link ShadeEngine#plan} (not checked).
     * @return Found call
     */
    protected NodeCall selectCall(ShadeNode node) throws NoEligibleElementException {
        // Get element with highest priority and enabled trigger
        NodeElement elem = getSelectedElement(node);
        NodeCall call = elem.getCall();
        ShadeNode childNode = plan.findNode(call.getName());

        if (childNode != null) {
            return selectCall(childNode);
        }
        return call;
    }

    /**
     * Take the node and find which of its elements should be selected
     * according to highest priority and enabled trigger.
     * @param node node for searched elements
     * @return selected node
     */
    protected NodeElement getSelectedElement(ShadeNode node) throws NoEligibleElementException {
        // sort by priority, from highest to the lowest
        SortedMap<BigDecimal, NodeElement> elements =
                new TreeMap<BigDecimal, NodeElement>(Collections.reverseOrder());
        for (NodeElement elem : node.getElements()) {
            BigDecimal elementPriority = elem.getPriority().execute(executor);
            elements.put(elementPriority, elem);
        }

        // For every element according to descending priority check trigger
        // and if it is satisfied, execute the call of the element.
        for (NodeElement elem : elements.values()) {
            BigDecimal triggerResult = elem.getTrigger().execute(executor);

            // if trigger evaluates true...
            if (!BigDecimal.ZERO.equals(triggerResult)) {
                return elem;
            }
        }
        throw new NoEligibleElementException("Node \"" + node.getName() + "\" doesn't have even one elegible element.");
    }

    protected BigDecimal executeCall(NodeCall call) {
        VariableContext ctx = new VariableContext();
        List<IArgument> args = call.getArgs();

        for (int argIdx = 0; argIdx < args.size(); ++argIdx) {
            ctx.put(Integer.toString(argIdx), args.get(argIdx).getValue());
        }
        Object result;
        
        // TODO: this would not work at all!!!
        
        try {
        	result = executor.executeSense(call.getName(), ctx);
        } catch (Exception e) {
        	result = executor.executeAction(call.getName(), ctx);
        }

        return (BigDecimal) result;
    }
}

/**
 * Node call with list of parameters specified by the caller. This object is
 * immutable.
 * @author Honza
 */
class CallContext {

    /**
     * Specification of the call.
     */
    private final String name;
    /**
     * Variable context of the call. Basically list of arguments passed to
     * the called node/function.
     */
    private final List<IArgument> ctx;

    /**
     * Create new {@link CallContext}. It is done by taking
     * the {@link NodeCall call} and current {@link CallContext}, and merging
     * them together, because we need to replace variables 
     * in the {@link NodeCall}.
     * @param call what is being called
     * @param ctx
     */
    public CallContext(NodeCall call, List<IArgument> ctx) {
        this.name = call.getName();
        this.ctx = ctx;
    }
}
