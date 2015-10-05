package rpax.massis.model.managers.movement.steering;

import static odk.lang.FastMath.pow;
import static odk.lang.FastMath.sqrt;
import rpax.massis.model.agents.DefaultAgent;
import rpax.massis.util.geom.KVector;

/**
 * Implements a custom collision Avoidance steering behavior
 *
 * @author rpax
 *
 */
public class CollisionAvoidance extends SteeringBehavior {

    private final int STEPS_AHEAD;

    public CollisionAvoidance(DefaultAgent v, double d)
    {
        super(v);
        this.STEPS_AHEAD = (int) d;
    }

    @Override
    public KVector steer()
    {
        // if (v.isInDoorArea()) return new KVector();
        boolean separate = false;
        double minT = Double.MAX_VALUE;
        KVector separationVector = new KVector();
        KVector repulsionVector = new KVector();
        final double agent_vel_magnitude = v.getVelocity().magnitude();
        for (DefaultAgent other : getActiveAgentsInRange(this.v,STEPS_AHEAD * 2
                * agent_vel_magnitude))
        {
           
            double distance = v.getXY().distance(other.getXY());
            double d1 = (v.getPolygon().getRadius() + other.getPolygon()
                    .getRadius()) * 1.5;

            if (d1 > distance)
            {
                return new Separation(v, STEPS_AHEAD).steer();
            }
            double t = getTimeToCollision(v, other, d1);
            if (t > 0 && t < minT)
            {

                minT = t;
                KVector futureV = futureLocation(v, t);
                KVector futureO = futureLocation(other, t);
                repulsionVector = KVector.sub(futureV, futureO).normalize().div(
                        t * 0.1);

            }

            if (d1 > distance)
            {
                separate = true;
                separationVector.add(KVector.sub(v.getXY(), other.getXY())
                        .normalize().div(distance));

            }
        }
        if (separate)
        {
            separationVector.mult(v.getMaxForce());
            return separationVector;
        }
        return repulsionVector;
    }

    public static KVector futureLocation(DefaultAgent a, double t)
    {
        return new KVector(a.getXY()).add(a.getVelocity().copy().mult(t));
    }

    private static double getTimeToCollision(DefaultAgent a, DefaultAgent b,
            double d1)
    {

        double xa1 = a.getX();
        double ya1 = a.getY();
        double vxa = a.getVelocity().getX();
        double vya = a.getVelocity().getY();
        //
        double xb1 = b.getX();
        double yb1 = b.getY();
        double vxb = b.getVelocity().getX();
        double vyb = b.getVelocity().getY();
        //
        double t1 = ((-vxa) * xa1 + vxb * xa1 + vxa * xb1 - vxb * xb1 - vya
                * ya1 + vyb * ya1 + vya * yb1 - vyb * yb1
                //
                - //
                (1 / 2)
                * sqrt(4
                * pow((vxa * (xa1 - xb1) + vxb * (-xa1 + xb1) + (vya - vyb)
                * (ya1 - yb1)), 2)
                - 4
                * (pow(vxa, 2) - 2 * vxa * vxb + pow(vxb, 2) + pow(
                (vya - vyb), 2))
                * (-pow(d1, 2) + pow(xa1, 2) - 2 * xa1 * xb1
                + pow(xb1, 2) + pow(ya1, 2) - 2 * ya1 * yb1 + pow(
                yb1, 2))))
                / (pow(vxa, 2) - 2 * vxa * vxb + pow(vxb, 2) + pow((vya - vyb),
                2));
        double t2 = ((-vxa) * xa1 + vxb * xa1 + vxa * xb1 - vxb * xb1 - vya
                * ya1 + vyb * ya1 + vya * yb1 - vyb * yb1
                //
                + //
                (1 / 2)
                * sqrt(4
                * pow((vxa * (xa1 - xb1) + vxb * (-xa1 + xb1) + (vya - vyb)
                * (ya1 - yb1)), 2)
                - 4
                * (pow(vxa, 2) - 2 * vxa * vxb + pow(vxb, 2) + pow(
                (vya - vyb), 2))
                * (-pow(d1, 2) + pow(xa1, 2) - 2 * xa1 * xb1
                + pow(xb1, 2) + pow(ya1, 2) - 2 * ya1 * yb1 + pow(
                yb1, 2))))
                / (pow(vxa, 2) - 2 * vxa * vxb + pow(vxb, 2) + pow((vya - vyb),
                2));

        if (t1 < 0)
        {
            return t2;
        } else if (t2 < 0)
        {
            return t1;
        } else
        {
            return Math.min(t1, t2);
        }

    }
}
