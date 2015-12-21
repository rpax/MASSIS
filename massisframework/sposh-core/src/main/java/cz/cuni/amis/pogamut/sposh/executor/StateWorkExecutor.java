package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This class is an {@link IWorkExecutor}, that considers actions to be have state,
 * in this executor, primitive is working in phases INIT, RUN*, DONE.
 * <p/>
 * Thanks to state nature of primitive, primitive is far easier to program 
 * in discrete time-slice (time slice = primitive can be called multiple times consequentially)
 * nature of Pogamut. If primitive wasn't notified that it is about to end,
 * things like switching from primitive "walk" to "shoot" could be troublesome.
 * Imagine it, "walk" primitive will compute path to destination and suddently
 * new primitive "shoot" is now called. What about path the bot is following? It
 * will still follow it, although it is supposed to stop and start shooting.
 * To handle this correctlty is troublesome for few states, for many states, it is madness.
 * <p/>
 * StateWorkExecutor would do this: {@code ..., walk.INIT, walk.RUN*, walk.DONE, shoot.INIT, shoot.RUN*, shoot.DONE....},
 * primitive walk would have DONE called before shoot.INIT would be called,
 * allowing it to stop walking. Same thing is valid for state shoot too.
 * <p/>
 * Since we have phase DONE to cleanup some stuff before we switch to another,
 * where another can be nearly anything, what state bot should be in when DONE
 * phase is DONE. In neutral bot state (precise neutral state is defined by programmer,
 * in unreal, that would probably be standing, not shooting.).
 * <p/>
 * What if we don't want to switch to neutral bot state after primitive is DONE?
 * Don't, there is no explicit need, and in many situation it is meaningless (such as
 * primtive "enter_ducts" where bot would entering INIT in standing state, but left 
 * DONE crouching).
 *
 * @see IAction
 * @author Honza
 */
public class StateWorkExecutor implements IWorkExecutor {

    /**
     * Map that maps primtive name to {@link IAction}.
     */
    protected final HashMap<String, IAction> actions = new HashMap<String, IAction>();
    /**
     * Map that maps primitive name to its respective {@link ISense}.
     */
    protected final HashMap<String, ISense> senses = new HashMap<String, ISense>();
    /**
     * Primitive that is currently being executed. If no such primitive, null is used.
     */
    protected String currentActionName;
    /**
     * String representation of current variable context. If ctx is changed, we
     * have to do done and init phase for the action.
     * e.g. GoToBase($target = "our") and GoToBase($target = "enemy") have same 
     * action name, but different target.
     */
    protected String currentVariableContext;
    /**
     * Log where we put
     */
    protected Logger log;
    
    public StateWorkExecutor() {
        this.log = Logger.getLogger(getClass().getSimpleName());
    }

    public StateWorkExecutor(Logger log) {
        this.log = log;
    }

    /**
     * Get logger of this {@link IWorkExecutor}.
     * @return
     */
    public Logger getLogger() {
        return log;
    }

    /**
     * Is name used in some primtive in this the work executor.
     * @param name queried name
     * @return if the name is not yet used in the {@link StateWorkExecutor}.
     */
    public synchronized boolean isNameUsed(String name) {
        return isSense(name) || isAction(name);
    }

    /**
     * Is there an action with the name.
     * @param name queries name
     */
    protected boolean isAction(String name) {
        return actions.containsKey(name);
    }

    /**
     * Is there a sense with the name.
     * @param name queries name
     */
    protected boolean isSense(String name) {
        return senses.containsKey(name);
    }

    /**
     * Add new {@link IAction} with primitive name.
     * @param name name that will be used for this {@link IAction} in posh plan.
     * @param action primitive that will be executed when executor will be asked to execute primtive with name.
     * @throws IllegalArgumentException if primitive with name already exists in {@link StateWorkExecutor}.
     */
    public synchronized void addAction(String name, IAction action) {
        if (isNameUsed(name)) {
            throw new IllegalArgumentException("Primtive with name \"" + name + "\" is already present in executor.");
        }
        actions.put(name, action);
    }

    /**
     * Add new {@link ISense} with primtive name.
     * @param name name of primtive to be associated with passed sense object
     * @param sense sense object to be used, when sense with name is supposed to execute.
     */
    public synchronized void addSense(String name, ISense sense) {
        if (isNameUsed(name)) {
            throw new IllegalArgumentException("Primtive with name \"" + name + "\" is already present in executor.");
        }
        senses.put(name, sense);
    }

    /**
     * Add primitive, use name from annotations.
     * @param action primitive that will be added
     */
    public void addAction(IAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Add primitive, use name from annotations.
     * @param sense primitive that will be added
     */
    public void addSense(ISense sense) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private String getSenseName(ISense sense, String primitiveName) {
    	PrimitiveInfo info = sense.getClass().getAnnotation(PrimitiveInfo.class);
    	if (info == null) return primitiveName;
    	if (info.name() == null) return primitiveName;
    	return info.name();
    }
    
    private String getActionName(IAction sense, String primitiveName) {
    	PrimitiveInfo info = sense.getClass().getAnnotation(PrimitiveInfo.class);
    	if (info == null) return primitiveName;
    	if (info.name() == null) return primitiveName;
    	return info.name();
    }
    
    @Override
    public synchronized Object executeSense(String primitive, VariableContext ctx) {
        if (!isSense(primitive)) {
        	throw new IllegalArgumentException("Sense \"" + primitive + "\" is not specified in the worker.");
        }
        
        ISense current = senses.get(primitive);
        Object result = current.query(ctx);
        
        log.info(MessageFormat.format("Query: {0}({1}) = {2}", getSenseName(current, primitive), ctx, result));
        
        return result;
    }

    @Override
    public synchronized ActionResult executeAction(String actionToExecuteName, VariableContext ctx) {
        if (!isAction(actionToExecuteName)) {
            throw new IllegalArgumentException("Action \"" + actionToExecuteName + "\" is not specified in the worker.");
        }

        // FIND ACTION TO BE EXECUTED
        IAction actionToExecute = actions.get(actionToExecuteName);
        String variableContextToExecute = ctx.toString();
        // FIND PREVIOUSLY EXECUTED ACTION
        IAction currentAction = actions.get(currentActionName);

        // NO PREVIOUS ACTION?
        if (currentAction == null) {
        	// YES!
        	// => set action to be current
            currentActionName = actionToExecuteName;
            currentVariableContext = ctx.toString();
            currentAction = actionToExecute;
            
            // => initialize new action
            log.info(MessageFormat.format("Action: {0}.init({1})", getActionName(currentAction, currentActionName), ctx));
            currentAction.init(ctx);
        } else // ACTION SWITCH? Actions are switched when action name is changed or variable context is changed
        if ((currentAction != null && actionToExecute != currentAction) ||
                !variableContextToExecute.equals(currentVariableContext)) {
        	// YES, NEW ACTION SCHEDULED
        	// => finalize previous one
        	log.info(MessageFormat.format("Action: {0}.done({1})", getActionName(currentAction, currentActionName), ctx));
        	currentAction.done(ctx);
            
            // => swap actions
            currentActionName = actionToExecuteName;
            currentVariableContext = variableContextToExecute;
            currentAction = actionToExecute;
            
            // => initialize new action
            log.info(MessageFormat.format("Action: {0}.init({1})", getActionName(currentAction, currentActionName), ctx));
            currentAction.init(ctx);
        }
        
        // run current action        
        ActionResult result = currentAction.run(ctx);
        log.info(MessageFormat.format("Action: {0}.run({1}) = {2}", getActionName(currentAction, currentActionName), ctx, result));
        
        // DID ACTION FINISHED?
        if (result == ActionResult.FINISHED || result == ActionResult.RUNNING_ONCE) {
        	// YES!
        	// => finalize it
        	log.info(MessageFormat.format("Action: {0}.done({1})", getActionName(currentAction, currentActionName), ctx));
            currentAction.done(ctx);
            // => nullify that we have previously executed (<= ACTION IS FINISHED)
            currentAction = null;
            currentActionName = null;
            currentVariableContext = null;
        }
        
        return result;
    }
}
