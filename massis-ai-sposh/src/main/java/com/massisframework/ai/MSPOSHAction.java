/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.ai;


import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.executor.ParamsAction;

/**
 * Represents an SPOSH action in MASSIS simulation engine
 *
 * @author rpax
 */
public abstract class MSPOSHAction<A> extends ParamsAction<Context<A>>
        {

    public MSPOSHAction(Context<A> ctx)
    {
        super(ctx);
    }

    /*
     * The SPOSH engine calls init() and done() with
     * reflection. Making these two methods abstract, forces the underlaying
     * classes to implement it, avoiding errors.
     */
    public abstract void init();

    public abstract void done();
}
