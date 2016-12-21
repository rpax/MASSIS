package com.massisframework.massis.model.managers.movement.steering;

import java.util.Iterator;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.util.geom.KVector;

public abstract class SteeringBehavior {

    protected LowLevelAgent v;

    public SteeringBehavior(LowLevelAgent v)
    {
        this.v = v;
    }

    public abstract KVector steer();

    protected static Iterable<LowLevelAgent> getActiveAgentsInRange(
            final LowLevelAgent v, final double range)
    {
        return new Iterable<LowLevelAgent>() {
            @Override
            public Iterator<LowLevelAgent> iterator()
            {
                return new FilterIterator<>(
                        v.getAgentsInRange(range).iterator(),
                        new Predicate<LowLevelAgent>() {
                    @Override
                    public boolean evaluate(LowLevelAgent other)
                    {
                        return !(
                                   !other.isDynamic()
                                || !other.isObstacle()
                                || other == v
                                || other.getRoom() != v.getRoom());
                    }
                });

            }
        };
    }
}
