package com.massisframework.massis.model.managers.movement.steering;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.util.geom.KVector;

public class SteeringCombinationBehavior extends SteeringBehavior {

    private final SteeringBehavior[] behaviors;

    public SteeringCombinationBehavior(LowLevelAgent v,
            SteeringBehavior... behaviors)
    {
        super(v);
        this.behaviors = behaviors;
    }

    @Override
    public KVector steer()
    {
        double maxForce = v.getMaxForce();
        KVector steeringForce = new KVector();
        int i = 0;
        while (maxForce > 0 && i < behaviors.length)
        {
            KVector force = behaviors[i].steer().mult(maxForce);
            if (Double.isNaN(force.x) || Double.isNaN(force.y))
            {
                break;
            }
            maxForce -= force.magnitude();
            steeringForce.add(force);

            i++;
        }
        return steeringForce;

    }
}
