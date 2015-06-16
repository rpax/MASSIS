package rpax.massis.ia.sposh;

import java.util.HashMap;

import rpax.massis.model.agents.Agent;
import rpax.massis.model.location.Location;
import cz.cuni.amis.pogamut.sposh.context.Context;
/**
 * Agent context in MASSIS Engine
 * @author rpax
 *
 * @param <SO>
 */
public class SimulationContext<SO extends Agent> extends Context<SO> {

    protected HashMap<String,Object> mentalState=new HashMap<>();
    private Location target;
  
    public SimulationContext(SO bot) {
        super("Simulation Context", bot);
        
    }
    public void setTarget(Location so) {
        this.target = so;
    }
    public Location getTarget() {
        return this.target;
    }
    

    

    public HashMap<String,Object> getMentalState() {
      return this.mentalState;
        
    }


    
}
