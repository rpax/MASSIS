/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.ai.sposh.actions;

/**
 *
 * @author rpax
 */
import com.massisframework.massis.ai.sposh.SimulationContext;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Clears any target of this agent
 * @author rpax
 */
@PrimitiveInfo(name = "Clear Target", description = "Clears any target of this agent")
public class ClearTarget extends SimulationAction {
    
    public ClearTarget(SimulationContext ctx) {
        super(ctx);
    }
    
    public ActionResult run() {
        this.ctx.setTarget(null);
        return ActionResult.FINISHED;
    }
    
    @Override
	public void init() {
    }

    @Override
	public void done() {
    }
}
