package com.massisframework.massis.model.managers.movement.steering;

import java.util.Iterator;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.components.EntitiesInRange;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.util.geom.KVector;

public abstract class SteeringBehavior {

    protected SimulationEntity v;

    public SteeringBehavior(SimulationEntity v)
    {
        this.v = v;
    }

    public abstract KVector steer();

    protected static Iterable<SimulationEntity> getActiveAgentsInRange(
            final SimulationEntity v, final double range)
    {
        return new Iterable<SimulationEntity>() {
            @Override
            public Iterator<SimulationEntity> iterator()
            {
                return new FilterIterator<>(
                        v.getComponent(EntitiesInRange.class).get().iterator(),
                        new Predicate<SimulationEntity>() {
                    @Override
                    public boolean evaluate(SimulationEntity other)
                    {
                    	return other.getComponent(Velocity.class)!=null;
                    	//&& other.getComponent(CurrentRoom.class)==v.getComponent(CurrentRoomId.class)
								// return !(
								// !other.isDynamic()
								// || !other.isObstacle()
								// || other == v
								// || other.getRoom() != v.getRoom());
                    }
                });

            }
        };
    }
}
