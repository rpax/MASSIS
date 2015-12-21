package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;

/**
 * Basic state sense primitive. It contains context and name. Wizzard for new
 * state sense generates class that is extension of this class.
 * @author Honza
 * @param <CONTEXT> Context of this sense, used to manipulate the environment.
 * @param <RETURN> Class of object this action returns every time it is queried.
 */
public abstract class StateSense<CONTEXT extends Context, RETURN> implements ISense<RETURN> {
    /** Name of the action */
    private String name;
    /** Context for the action */
    protected final CONTEXT ctx;

    /**
     * Create new sense with context.
     * @param name name of the sense.
     * @param ctx action context, used as shared memory or environment interactor.
     */
    protected StateSense(String name, CONTEXT ctx) {
        this.name = name;
        this.ctx = ctx;
    }

    /**
     * Create new sense with context. Name of the sense will be simple name of 
     * the class.
     * @param ctx action context, used as shared memory or environment interactor.
     */
    protected StateSense(CONTEXT ctx) {
        this.name = this.getClass().getName();
        this.ctx = ctx;
    }
    
    /**
     * The state sense context.
     * @return the ctx
     */
    public CONTEXT getCtx() {
        return ctx;
    }

    /**
     * Get name of the sense.
     * @return Name of the sense.
     */
    public final String getName() {
        return name;
    }
}
