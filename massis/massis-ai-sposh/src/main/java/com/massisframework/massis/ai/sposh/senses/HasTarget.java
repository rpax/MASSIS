/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.ai.sposh.senses;

import com.massisframework.massis.ai.sposh.SimulationContext;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Returns if the agent has a target assigned
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Has Target", description = "Return if the agent has a target assigned")
public class HasTarget extends SimulationSense<Boolean> {

    public HasTarget(SimulationContext ctx) {
        super(ctx);
    }

   
    public Boolean query() {
        return this.ctx.getTarget()!=null;
    }
}
