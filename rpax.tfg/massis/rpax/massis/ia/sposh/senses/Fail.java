/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.senses;

import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;

/**
 * Primitive sense that always fails (returns false) and does nothing else.
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Fail", description = "Return false")
public class Fail<SO extends SPOSHAgent> extends SimulationSense<SO, Boolean> {

    public Fail(SimulationContext<SO> ctx) {
        super(ctx);
    }

   
    public Boolean query() {
        return false;
    }
}
