package rpax.massis.model.managers.movement;

import java.util.ArrayList;
import java.util.List;

import rpax.massis.model.agents.Agent;
import rpax.massis.util.geom.ContainmentPolygon;
import rpax.massis.util.geom.KLine;
import rpax.massis.util.geom.KVector;
import straightedge.geom.KPoint;

/**
 * Utility class with some steering methods. Some of them are based on the
 * examples provided in http://natureofcode.com/
 * 
 * @author rpax
 * 
 */
public class Steering {

	public static KVector seek(Agent p, KPoint target) {

		// List<KPoint> path = p.getFloor().findPath(p.getLocation(), target);
		// if (path != null && path.size() > 1)
		// target = new KVector(path.get(1));

		KVector desired = KVector.sub(target, p.getXY());

		// If the magnitude of desired equals 0, skip out of here
		// (We could optimize this to check if x and y are 0 to avoid mag()
		// square root
		if (desired.magnitude() == 0)
			return new KVector(0, 0);

		// Normalize desired and scale to maximum speed
		desired.normalize();
		desired.mult(p.getMaxSpeed());
		// Steering = Desired minus Velocity
		KVector steer = KVector.sub(desired, p.getVelocity());
		steer.limit(p.getMaxForce()); // Limit to maximum steering force

		return steer;
	}

	public static KVector arrive(Agent p, KVector target,
			double aproximationRadio) {
		KVector desired = KVector.sub(target, p.getXY()); // A vector
		// pointing
		// from the location
		// to the target
		double d = desired.magnitude();

		if (d < aproximationRadio)
		{
			double m = KVector.map(d, 0, 100, 0, p.getMaxSpeed());
			desired.limit(m);
		}
		else
		{
			desired.limit(p.getMaxSpeed());
		}

		// Steering = Desired minus Velocity
		KVector steer = KVector.sub(desired, p.getVelocity());
		steer.limit(p.getMaxForce()); // Limit to maximum steering force
		return steer;
	}

	public static KVector wander(Agent p, double wanderRadio,
			double wanderDistance, double wanderTheta) {

		KVector circleloc = p.getVelocity().copy(); // Start with velocity
		circleloc.normalize(); // Normalize to get heading
		circleloc.mult(wanderDistance); // Multiply by distance
		circleloc.add(p.getXY()); // Make it relative to boid's
									// location

		double h = p.getVelocity().heading2D(); // We need to know the heading
												// to offset
		// wandertheta

		KVector circleOffSet = new KVector(wanderRadio
				* Math.cos(wanderTheta + h), wanderRadio
				* Math.sin(wanderTheta + h));
		KVector target = KVector.add(circleloc, circleOffSet);
		return seek(p, target);

	}

	public static KVector futureLocation(KPoint position, KVector vel,
			double steps) {
		return new KVector(position).add(vel.copy().mult(steps));
	}

	// =======================================================================
	// Containment Constants
	private static double[] thetas = { -Math.PI / 2, -Math.PI / 8, 0,
			Math.PI / 8, Math.PI / 2, Math.PI };
	private static double[] checkL = { 1, 1, 1, 1, 1, 1 };
	private static double[] r = { 5, 5, 10, 5, 5, 10 };

	// =======================================================================
	public static KVector stayWithInWalls(Agent person,
			List<ContainmentPolygon> containmentPolygons) {

		// shortcuts
		if (person.getRoom()
				.getDistanceOfBoundaryPointClosestTo(person.getXY()) > person
				.getMaxSpeed())
		{
			return new KVector(0, 0);
		}
		else if (person.isInDoorArea())
		{
			return new KVector(0, 0);
		}
		//
		boolean projected = false;
		KVector[] forward = new KVector[thetas.length], ray = new KVector[thetas.length], projection = new KVector[thetas.length], acc = new KVector[thetas.length], lateral = new KVector[thetas.length];
		ContainmentPolygon[] check = new ContainmentPolygon[thetas.length];
		for (int n = 0; n < thetas.length; n++)
		{
			forward[n] = new KVector();
			ray[n] = new KVector();
			projection[n] = new KVector();
			acc[n] = new KVector();
			lateral[n] = new KVector();

		}
		for (int n = 0; n < thetas.length; n++)
		{
			acc[n].mult(0);
			// KVector acc = new KVector();
			KVector vel = person.getVelocity().copy();
			KVector loc = new KVector(person.getXY());

			double checkLength = checkL[n] * vel.magnitude();
			// KVector forward, ray, projection, force, lateral;
			KVector force;

			// forward[n] = getAngle(vel.copy(), n);
			KVector vDirected = new KVector();
			vDirected.x = vel.x * Math.cos(thetas[n]) - vel.y
					* Math.sin(thetas[n]);
			vDirected.y = vel.x * Math.sin(thetas[n]) + vel.y
					* Math.cos(thetas[n]);
			forward[n] = vDirected;
			//
			forward[n].normalize();
			ray[n] = forward[n].copy();
			ray[n].mult(checkLength);
			lateral[n] = forward[n].copy();
			lateral[n].set(lateral[n].y, -lateral[n].x);
			lateral[n].mult(r[n]);

			ArrayList<KVector> p = new ArrayList<>();
			p.add(new KVector(lateral[n].x, lateral[n].y));
			p.add(new KVector(-lateral[n].x, -lateral[n].y));
			// p.add(new KVector(0, 0));
			p.add(new KVector(ray[n].x - lateral[n].x, ray[n].y - lateral[n].y));
			p.add(new KVector(ray[n].x + lateral[n].x, ray[n].y + lateral[n].y));
			// p.add(new KVector(ray.x, ray.y));

			check[n] = new ContainmentPolygon(loc.x, loc.y, p);

			for (ContainmentPolygon ob : containmentPolygons)
			{
				projection[n] = overlap(check[n], ob);
				if ((projection[n].x != 0) || (projection[n].y != 0))
				{ // overlapping so steer away
					projected = true;
					projection[n].normalize();
					force = lateral[n].copy();
					// force.normalize();
					force.mult(projection[n].dot(force));
					// force.limit(person.getMaxForce());
					acc[n].add(force);
				}
			}

			// System.err.println("ADDING FORCE [" + n + "] : " + acc[n]);

		}
		if (projected == false)
		{
			return new KVector(0, 0);
		}
		KVector f = new KVector();
		for (int i = 0; i < thetas.length; i++)
		{
			f.add(acc[i]);
		}
		return f;
	}

	/**
	 * <p>
	 * Much credit due to: <a
	 * href="http://www.openprocessing.org/user/8371">Jacob Haip's code</a>
	 * </p>
	 * <p>
	 * overlap() takes in two polygons and using the separating axis theorum
	 * returns the smallest vector that will projected ob1 from colliding ob2 if
	 * there is no collision, a null vector is returned Based off N Tutorial A -
	 * Collision Detection and Response
	 * http://www.metanetsoftware.com/technique/tutorialA.html And also Advanced
	 * Character Physics by Thomas Jakobsen
	 * </p>
	 */
	private static KVector overlap(ContainmentPolygon ob1,
			ContainmentPolygon ob2) { // ob1 is one that is projected
		// (moves)

		KVector nor, pt, projection;
		double low1;
		double high1;
		double low2;
		double high2;
		double dt;
		projection = new KVector(Float.MAX_VALUE, 0);

		for (int q = 0; q < ob1.points.size(); q++)
		{
			if (q == (ob1.points.size() - 1))
			{
				nor = KVector.sub(ob1.points.get(0), ob1.points.get(q));
			}
			else
			{
				nor = KVector.sub(ob1.points.get(q), ob1.points.get(q + 1));
			}
			nor.set(-nor.y, nor.x); // rotate 90 degrees
			nor.normalize();

			// set the values so any value will work
			low1 = Float.MAX_VALUE;
			high1 = -Float.MAX_VALUE;
			for (int i = 0; i < ob1.points.size(); i++)
			{
				pt = KVector.add(ob1.points.get(i), new KVector(ob1.x, ob1.y));
				dt = pt.dot(nor);
				if (dt < low1)
				{
					low1 = dt;
				}
				if (dt > high1)
				{
					high1 = dt;
				}
			}
			low2 = Float.MAX_VALUE;
			high2 = -Float.MAX_VALUE;
			for (int i = 0; i < ob2.points.size(); i++)
			{
				pt = KVector.add(ob2.points.get(i), new KVector(ob2.x, ob2.y));
				dt = pt.dot(nor);
				if (dt < low2)
				{
					low2 = dt;
				}
				if (dt > high2)
				{
					high2 = dt;
				}
			}
			// find projection using min overlap of low1-high1 and low2-high2
			// ob1 is the one that is projected (moves)
			double mid1, mid2;
			mid1 = 0.5f * (low1 + high1);
			mid2 = 0.5f * (low2 + high2);
			if (mid1 < mid2)
			{
				if (high1 < low2)
				{ // no overlap
					return (new KVector(0, 0)); // return a null vector
				}
				else
				{ // test to see if projection is smallest
					if ((high1 - low2) < projection.magnitude())
					{ // new smallest projection found
						projection = nor.copy();
						projection.normalize();
						projection.mult(-(high1 - low2));
					}
				}
			}
			else
			{
				if (low1 > high2)
				{ // no overlap
					return (new KVector(0, 0)); // return a null vector
				}
				else
				{
					if ((high2 - low1) < projection.magnitude())
					{ // new smallest projection found
						projection = nor.copy();
						projection.normalize();
						projection.mult((high2 - low1));
					}
				}
			}
		}

		// do same for
		// ob2/////////////////////////////////////////////////////////////////////////////
		for (int q = 0; q < ob2.points.size(); q++)
		{
			// println(ob1.points.size());
			if (q == (ob2.points.size() - 1))
			{
				nor = KVector.sub(ob2.points.get(0), ob2.points.get(q));
			}
			else
			{
				nor = KVector.sub(ob2.points.get(q), ob2.points.get(q + 1));
			}
			nor.set(-nor.y, nor.x); // rotate 90 degrees
			nor.normalize();
			nor.mult(100);
			nor.normalize();
			// set the values so any value will work
			low1 = Float.MAX_VALUE;
			high1 = -Float.MAX_VALUE;
			for (int i = 0; i < ob1.points.size(); i++)
			{
				pt = KVector.add(ob1.points.get(i), new KVector(ob1.x, ob1.y));
				dt = pt.dot(nor);
				if (dt < low1)
				{
					low1 = dt;
				}
				if (dt > high1)
				{
					high1 = dt;
				}
			}
			low2 = Float.MAX_VALUE;
			high2 = -Float.MAX_VALUE;
			for (int i = 0; i < ob2.points.size(); i++)
			{
				pt = KVector.add(ob2.points.get(i), new KVector(ob2.x, ob2.y));
				dt = pt.dot(nor);
				if (dt < low2)
				{
					low2 = dt;
				}
				if (dt > high2)
				{
					high2 = dt;
				}
			}
			// find projection using min overlap of low1-high1 and low2-high2
			// ob1 is the one that is projected (moves)
			double mid1, mid2;
			mid1 = 0.5f * (low1 + high1);
			mid2 = 0.5f * (low2 + high2);
			if (mid1 < mid2)
			{
				if (high1 < low2)
				{ // no overlap
					return (new KVector(0, 0)); // return a null vector
				}
				else
				{ // test to see if projection is smallest
					if ((high1 - low2) < projection.magnitude())
					{ // new smallest projection found
						projection = nor.copy();
						projection.normalize();
						projection.mult(-(high1 - low2));
					}
				}
			}
			else
			{
				if (low1 > high2)
				{ // no overlap
					return (new KVector(0, 0)); // return a null vector
				}
				else
				{
					if ((high2 - low1) < projection.magnitude())
					{ // new smallest projection found
						projection = nor.copy();
						projection.normalize();
						projection.mult((high2 - low1));
					}
				}
			}
		}

		return projection;
	}

	public static KVector evade(Agent fleer, Agent predator) {
		if (fleer.isObjectPerceived(predator))
			return flee(fleer,
					KVector.add(predator.getXY(), predator.getVelocity()));
		return new KVector(0, 0);
	}

	public static KVector followIfVisible(Agent follower, Agent leader) {
		if (follower.isObjectPerceived(leader))
			return flee(follower,
					KVector.add(leader.getXY(), leader.getVelocity()));
		return new KVector(0, 0);
	}

	public static KVector flee(Agent fleer, KVector undesired) {
		return KVector.mult(-1, seek(fleer, undesired));
	}

	public static KVector follow(Agent fleer, Agent leader) {
		return seek(fleer, KVector.add(leader.getXY(), leader.getVelocity()));
	}

	public static KVector align(Agent p, double distance) {
		KVector v = new KVector();
		int neighborCount = 0;
		for (Agent agent : p.getVehiclesInRoom())
		{
			if (agent != p && p.isObjectPerceived(agent))
			{
				if (p.getXY().distance(agent.getXY()) < distance)
				{
					v.x += agent.getVelocity().x;
					v.y += agent.getVelocity().y;
					neighborCount++;
				}

			}

		}
		if (neighborCount > 0)
		{
			v.normalize();
			v.mult(p.getMaxSpeed());
			// Steering = Desired minus Velocity
			KVector steer = KVector.sub(v, p.getVelocity());
			steer.limit(p.getMaxForce()); // Limit to maximum steering force
			return v;
		}
		return new KVector(0, 0);
	}

	// ////////////////////////////////////////////////

	public KVector createFeeler(Agent v, double angle, double length) {
		KVector vel = v.getVelocity();
		KVector feeler = new KVector();
		feeler.x = vel.x * Math.cos(angle) - vel.y * Math.sin(angle);
		feeler.y = vel.x * Math.sin(angle) + vel.y * Math.cos(angle);
		feeler.normalize().mult(length);
		return feeler;
	}

	public static List<KLine> getCollisionLines(Agent agent, double STEPS_AHEAD) {
		// draw1(g,v,STEPS_AHEAD);

		// 1. Normal a la velocidad, L
		KVector loc = new KVector(agent.getXY());
		double radius = agent.getPolygon().getRadius();
		KVector v = agent.getVelocity().copy();
		KVector normalL = KVector.normal(loc, KVector.add(v, loc)).mult(
				radius * 1.1);
		KVector normalR = KVector.mult(-1, normalL);
		KVector L1 = KVector.add(loc, normalL);
		KVector R1 = KVector.add(loc, normalR);

		KVector locF = KVector.add(loc, KVector.mult(STEPS_AHEAD, v));

		KVector L2 = KVector.add(locF, normalL);
		KVector R2 = KVector.add(locF, normalR);

		/**
		 * <pre>
		 * L2  locF   R2
		 * ^ \   ^     ^
		 * |  \  |     |
		 * |   \ |     |
		 * |    \|     |
		 * |     | D1  |
		 * |     |\    |
		 * |     | \   |
		 * |     |  \  |
		 * +     +   \ +
		 * L1<---X--->R1
		 * 
		 * </pre>
		 */
		ArrayList<KLine> lines = new ArrayList<>();
		lines.add(new KLine(L1, L2));
		lines.add(new KLine(R1, R2));
		lines.add(new KLine(R1, L2));
		lines.add(new KLine(L2, R2));
		return lines;
	}

}