package rpax.massis.model.managers.movement.steering;

import rpax.massis.model.agents.DefaultAgent;
import rpax.massis.util.geom.KVector;

public abstract class SteeringBehavior {

    protected DefaultAgent v;

    public SteeringBehavior(DefaultAgent v)
    {
        this.v = v;
    }

    public abstract KVector steer();
}
