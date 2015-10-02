/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ai.sposh.senses;

import rpax.massis.ai.sposh.SimulationContext;
import cz.cuni.amis.pogamut.sposh.executor.ParamsSense;

/**
 * SPOSH sense in MASSIS engine
 *
 * @author rpax
 */
public abstract class SimulationSense<RET_TYPE>
        extends ParamsSense<SimulationContext, RET_TYPE> {

    public SimulationSense(SimulationContext ctx)
    {
        super(ctx);
    }
}
