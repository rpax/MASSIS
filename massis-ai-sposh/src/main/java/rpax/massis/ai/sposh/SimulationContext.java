package rpax.massis.ai.sposh;

import java.util.HashMap;

import com.massisframework.massis.model.location.Location;
import cz.cuni.amis.pogamut.sposh.context.Context;
import com.massisframework.massis.model.agents.LowLevelAgent;

/**
 * Agent context in MASSIS Engine
 *
 * @author rpax
 *
 * @param <SO>
 */
public class SimulationContext extends Context<LowLevelAgent> {

    protected HashMap<String, Object> mentalState = new HashMap<>();
    private Location target;

    public SimulationContext(LowLevelAgent bot)
    {
        super("Simulation Context", bot);

    }

    public void setTarget(Location so)
    {
        this.target = so;
    }

    public Location getTarget()
    {
        return this.target;
    }

    public HashMap<String, Object> getMentalState()
    {
        return this.mentalState;
    }
}
