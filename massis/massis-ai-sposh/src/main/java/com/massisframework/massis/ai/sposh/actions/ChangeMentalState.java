/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.ai.sposh.actions;

import com.massisframework.massis.ai.sposh.SimulationContext;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Changes the Changes an internal attribute of the agent
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Change Mental State", description = "Changes an internal attribute of the agent", tags = {
		"parallel" })
public class ChangeMentalState extends SimulationAction {

	public ChangeMentalState(SimulationContext ctx)
	{
		super(ctx);
	}

	public ActionResult run(@Param("$mentalVariable") String mentalVariable,
			@Param("$value") Integer value)
	{
		this.ctx.getMentalState().put(mentalVariable, value);
		return ActionResult.FINISHED;
	}

	@Override
	public void init()
	{
	}

	@Override
	public void done()
	{
	}
}
