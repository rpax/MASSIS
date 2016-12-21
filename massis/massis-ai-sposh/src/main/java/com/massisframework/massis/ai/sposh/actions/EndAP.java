/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.ai.sposh.actions;


import com.massisframework.massis.ai.sposh.SimulationContext;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Primitive action that doesn't do anything. It is used in empty plans and so on.
 * This action should be appended to the end of a parallel AP.
 * @author rpax
 */
@PrimitiveInfo(name = "EndAP", description = "This action should be appended to the end of a parallel AP.")
public class EndAP extends SimulationAction {

    public EndAP(SimulationContext ctx) {
        super(ctx);
    }

    
    public ActionResult run() {
        return ActionResult.RUNNING_ONCE;
    }

    @Override
	public void init() {}
    @Override
	public void done() {}
}
