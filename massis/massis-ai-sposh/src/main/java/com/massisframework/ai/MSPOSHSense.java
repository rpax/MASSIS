/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.ai;

import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.executor.ParamsSense;

/**
 * SPOSH sense in MASSIS engine
 *
 * @author rpax
 */
public abstract class MSPOSHSense<A, RET_TYPE>
		extends ParamsSense<Context<A>, RET_TYPE> {

	public MSPOSHSense(Context<A> ctx)
	{
		super(ctx);
	}
}
