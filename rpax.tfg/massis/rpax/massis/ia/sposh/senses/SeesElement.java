package rpax.massis.ia.sposh.senses;

import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import rpax.massis.ia.sposh.MentalStateKeys;
import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.Agent;
import rpax.massis.model.agents.SPOSHAgent;
/**
 * True if sees another with the attribute and value provided
 * @author rpax
 *
 * @param <V>
 */
@PrimitiveInfo(
   name = "Sees Element",
   description = 
   "True if sees another with the attribute and value provided")
public class SeesElement<V extends SPOSHAgent> extends SimulationSense<V, Boolean>
        implements MentalStateKeys {
    
    public SeesElement(SimulationContext<V> ctx) {
        super(ctx);
    }
    
    
    public Boolean query(@Param("$attr") String attr,@Param("$value") Integer val) {
        for (Agent v : this.getAgent().getAgentsInVisionRadio()) {
            if (v.getProperty(attr)==val) {
                return true;
            }
        }
        return false;
    }
}
