/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ai.sposh.senses;

import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import rpax.massis.ai.sposh.SimulationContext;

/**
 * Primitive sense that always fails (returns false) and does nothing else.
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Fail", description = "Return false")
public class Fail extends SimulationSense<Boolean> {

    public Fail(SimulationContext ctx) {
        super(ctx);
    }

   
    public Boolean query() {
        return false;
    }
}
