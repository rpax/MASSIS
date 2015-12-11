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
 * Clears a mental state of the agent
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Clear Mental State", description = "Clears a mental state of the agent", tags =
{
    "parallel"
})
public class ClearMentalState extends SimulationAction {

    public ClearMentalState(SimulationContext ctx)
    {
        super(ctx);
    }

    public ActionResult run(@Param("$mentalVariable") String mentalVariable)
    {
        this.ctx.getMentalState().remove(mentalVariable);
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
