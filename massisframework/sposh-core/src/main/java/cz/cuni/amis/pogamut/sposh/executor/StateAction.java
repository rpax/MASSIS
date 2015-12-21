package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;

/**
 * Basic state action primitive. It contains context and name. Wizzard for new
 * state actions generates class that is extension of this class.
 * @author Honza
 * @param <CONTEXT> Context of this action, used to manipulate the environment.
 */
public abstract class StateAction<CONTEXT extends Context> implements IAction {
    /** Name of the action */
    private String name;
    /** Context for the action */
    protected final CONTEXT ctx;

    /**
     * Create new state action.
     * @param name name of the action
     * @param ctx action context, used as shared memory or environment interactor.
     */
    protected StateAction(String name, CONTEXT ctx) {
        this.name = name;
        this.ctx = ctx;
    }

    /**
     * Create new state action. Name of the action will be name of the class.
     * @param ctx action context, used as shared memory or environment interactor.
     */
    protected StateAction(CONTEXT ctx) {
        this.name = this.getClass().getName();
        this.ctx = ctx;
    }
    
    /**
     * Get name of the action.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get shared context of all primitives.
     * @return the ctx
     */
    public CONTEXT getCtx() {
        return ctx;
    }

}
