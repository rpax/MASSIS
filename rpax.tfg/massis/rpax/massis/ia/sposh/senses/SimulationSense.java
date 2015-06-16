/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.senses;

import rpax.massis.ia.sposh.AgentHolder;
import rpax.massis.ia.sposh.MentalStateKeys;
import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.ParamsSense;

/**
 * SPOSH sense in MASSIS engine
 * @author rpax
 */
public abstract class SimulationSense<SO extends SPOSHAgent, RET_TYPE>
        extends ParamsSense<SimulationContext<SO>, RET_TYPE>
        implements AgentHolder<SO>,MentalStateKeys {

    public SimulationSense(SimulationContext<SO> ctx) {
        super(ctx);
    }

    @Override
    public SO getAgent() {
        return this.ctx.getBot();
    }
}
