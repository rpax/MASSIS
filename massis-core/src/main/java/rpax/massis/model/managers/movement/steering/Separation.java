package rpax.massis.model.managers.movement.steering;

import java.util.List;

import rpax.massis.model.agents.DefaultAgent;
import rpax.massis.model.managers.movement.Steering;
import static rpax.massis.model.managers.movement.steering.SteeringBehavior.getActiveAgentsInRange;
import rpax.massis.util.geom.KLine;
import rpax.massis.util.geom.KVector;
import straightedge.geom.AABB;
import straightedge.geom.KPoint;

@SuppressWarnings("unused")
public class Separation extends SteeringBehavior {

    private final int STEPS_AHEAD;

    public Separation(DefaultAgent v, double d)
    {
        super(v);
        this.STEPS_AHEAD = (int) d;

    }

    @Override
    public KVector steer()
    {
        return separateNew(v, STEPS_AHEAD);
    }

    private static KVector separateNew(DefaultAgent v, double STEPS_AHEAD)
    {
        KVector force = new KVector();
        KVector brakingforce = new KVector();
        int inminentCollisionCount = 0;
        List<KLine> agentCollisionLines = Steering.getCollisionLines(v,
                STEPS_AHEAD);
        final double agent_vel_magnitude = v.getVelocity().magnitude();
        double agentMinDistToIntersection = Double.MAX_VALUE;
        KPoint nearestIntersection = null;
        double collisionTimeDiff = v.getPolygon().getRadius()
                / agent_vel_magnitude;
        KVector vCenterAtIntersection = null;
        KVector otherCenterAtIntersection = null;
        for (DefaultAgent other : getActiveAgentsInRange(v,
                STEPS_AHEAD * agent_vel_magnitude))
        {
            double agent_other_dist = other.getXY().distance(v.getXY());
            boolean isCollisionInminent = agent_other_dist < v.getPolygon()
                    .getRadius() + other.getPolygon().getRadius();
            if (isCollisionInminent)
            {
                brakingforce.add(
                        KVector.sub(v.getXY(), other.getXY())
                        // KVector.normal(new KVector(), other.getVelocity()).mult(-1)
                        .normalize()
                        .mult(1 / (agent_other_dist * agent_other_dist)));
                inminentCollisionCount++;
            }
            if (inminentCollisionCount == 0)
            {
                List<KLine> otherCollisionLines = Steering.getCollisionLines(
                        other,
                        STEPS_AHEAD);
                final double other_vel_magnitude = other.getVelocity()
                        .magnitude();
                for (KLine agentLine : agentCollisionLines)
                {
                    for (KLine otherLine : otherCollisionLines)
                    {
                        KPoint intersection = agentLine
                                .getIntersectionPoint(otherLine);
                        if (intersection != null
                                && v.getRoom().getPolygon()
                                .contains(intersection))
                        {

                            // sacamos el tiempo para llegar a la interseccion
                            double agent_distanceToIntersection = v.getXY()
                                    .distance(intersection);
                            double other_distanceToIntersection = other.getXY()
                                    .distance(intersection);
                            double agentNumberOfStepsToCollision = agent_distanceToIntersection
                                    / agent_vel_magnitude;
                            double otherNumberOfStepsToCollision = other_distanceToIntersection
                                    / other_vel_magnitude;

                            double timeDiff = Math
                                    .abs(agentNumberOfStepsToCollision
                                    - otherNumberOfStepsToCollision);
                            if (timeDiff <= 2)
                            {

                                double dist = agent_distanceToIntersection;
                                if (dist < agentMinDistToIntersection)
                                {
                                    agentMinDistToIntersection = dist;
                                    nearestIntersection = intersection;
                                    vCenterAtIntersection = KVector.mult(
                                            agentNumberOfStepsToCollision,
                                            v.getVelocity()).add(v.getXY());
                                    otherCenterAtIntersection = KVector.mult(
                                            other_distanceToIntersection,
                                            other.getVelocity()).add(
                                            other.getXY());
                                }
                            }
                            // fillCircle(g, intersection, 10);
                        }
                    }
                }

            }
        }
        if (inminentCollisionCount > 0)
        {
            force = brakingforce/*.normalize().sub(KVector.normalize(v.getVelocity()))*/.normalize();
            force.div(inminentCollisionCount);

        } else if (nearestIntersection != null)
        {

            force = KVector.sub(vCenterAtIntersection, otherCenterAtIntersection).normalize();//.mult(10000);
            force.mult(1 / (agentMinDistToIntersection));

        }

        return force;
    }
//

    private static KVector separateOld(DefaultAgent v, double STEPS_AHEAD)
    {

        int minStep = Integer.MAX_VALUE;
        KPoint otherCenterAtIntersection = null;
        KPoint vCenterAtIntersection = null;
        DefaultAgent nearestOther = null;
        KVector force = new KVector();

        for (DefaultAgent other : v.getAgentsInRange(
                v.getMaxSpeed() * (STEPS_AHEAD + 1)))
        {
            if (v == other)
            {
                continue;
            }
            if (v.getRoom() != other.getRoom())
            {
                continue;
            }
//			if (v.isInDoorArea())
//				continue;
            AABB otherAABB = other.getPolygon().getAABB();
            AABB vAABB = v.getPolygon().getAABB();
            KVector vPosition = new KVector(v.getXY());
            KVector otherPosition = new KVector(other.getXY());
            for (int step = 0; step < STEPS_AHEAD; step++)
            {
                if (step > minStep)
                {
                    // fuera. Ya hemos encontrado una colision antes
                    break;
                }
                otherPosition.add(other.getVelocity());
                otherAABB.setCenter(otherPosition);
                vPosition.add(v.getVelocity());
                vAABB.setCenter(vPosition);

                if (otherAABB.intersects(vAABB))
                {
                    if (!v.getRoom().getPolygon()
                            .contains(otherAABB.getCenter())
                            || !v.getRoom().getPolygon()
                            .contains(vAABB.getCenter()))
                    {
                        // se comprueba que ambos siguen en el cuarto.
                        // si no, fuera.
                        break;

                    }
                    if (minStep > step)
                    {
                        minStep = step;
                        otherCenterAtIntersection = otherAABB.getCenter();
                        vCenterAtIntersection = vAABB.getCenter();
                        nearestOther = other;
                    }
                    break;
                }

            }

        }
        if (otherCenterAtIntersection != null && vCenterAtIntersection != null)
        {

            force = KVector.sub(vCenterAtIntersection, otherCenterAtIntersection).normalize();
            double numberOfStepsToCollision = v.getMaxSpeed() / KPoint.distance(
                    v.getXY(), vCenterAtIntersection);
            force.mult(
                    0.3 / (numberOfStepsToCollision * numberOfStepsToCollision));


        }

        return force;
    }
}
