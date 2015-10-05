package rpax.massis.model.managers.movement.steering;

import java.util.Iterator;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;
import rpax.massis.model.agents.DefaultAgent;
import rpax.massis.util.geom.KVector;

public abstract class SteeringBehavior {

    protected DefaultAgent v;

    public SteeringBehavior(DefaultAgent v)
    {
        this.v = v;
    }

    public abstract KVector steer();

    protected static Iterable<DefaultAgent> getActiveAgentsInRange(
            final DefaultAgent v, final double range)
    {
        return new Iterable<DefaultAgent>() {
            @Override
            public Iterator<DefaultAgent> iterator()
            {
                return new FilterIterator<>(
                        v.getAgentsInRange(range).iterator(),
                        new Predicate<DefaultAgent>() {
                    @Override
                    public boolean evaluate(DefaultAgent other)
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
