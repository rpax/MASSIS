package com.massisframework.massis.model.managers.movement.steering;

import java.util.List;

import com.massisframework.massis.model.building.WayPoint;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.SteeringComponent;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.KPoint;

public class FollowPath extends SteeringBehavior {

	private final double radius;
	private final double frames_ahead;

	public FollowPath(SimulationEntity entity, double radius,
			double frames_ahead)
	{
		super(entity);
		this.radius = radius;
		this.frames_ahead = frames_ahead;
	}

	@Override
	public KVector steer()
	{
		PathComponent pc = v.getComponent(PathComponent.class);
		Position2D pos2d = v.getComponent(Position2D.class);
		SteeringComponent steeringComponent = v
				.getComponent(SteeringComponent.class);
		if (pc == null)
			return KVector.ZERO;
		List<WayPoint> p = pc.getPath().getPoints();
		KVector vel = v.getComponent(Velocity.class).getValue();
		KVector loc = new KVector(pos2d.getX(), pos2d.getY());
		KVector predict = vel.copy();
		predict.normalize();
		predict.mult(frames_ahead);
		KVector predictLoc = KVector.add(loc, predict);

		KVector target = null;
		KVector dir = null;
		double record = 100 * 1000 * 100;

		KVector normal = null;
		for (int i = 0; i < p.size() - 1; i++)
		{

			KPoint a = p.get(i).getXY();
			KPoint b = p.get(Math.min((i + 1), p.size() - 1)).getXY();
			normal = KVector.getNormalPoint(predictLoc, a, b);

			double da = KVector.distance(normal, a);
			double db = KVector.distance(normal, b);
			KVector line = KVector.sub(b, a);
			if (da + db > line.magnitude() + 1)
			{
				normal = new KVector(b);
				a = p.get(Math.min((i + 1), p.size() - 1)).getXY();
				b = p.get(Math.min((i + 2), p.size() - 1)).getXY();
				line = KVector.sub(b, a);

			}

			double d = KVector.distance(predictLoc, normal);
			if (d < record)
			{
				record = d;
				target = normal;
				dir = line;
				dir.normalize();
				dir.mult(frames_ahead);
			}
		}

		KVector steer = new KVector();
		KVector pos = new KVector(pos2d.getX(), pos2d.getY());
		if (target == null)
		{
			return new KVector();
		}
		if (record > radius || vel.magnitude() < 0.1)
		{
			target.add(dir);

			KVector desired = KVector.sub(target, loc);
			double d = desired.magnitude();
			if (d > 0)
			{
				desired.normalize();

				desired.mult(steeringComponent.getMaxSpeed());
				steer = desired;

			} else
			{
				steer = KVector.sub(target, pos);

				steer.normalize();
			}
			return steer.normalize().mult(steeringComponent.getMaxForce());
		} else
		{
			steer = KVector.sub(target, pos);
			steer.normalize();
			return steer.normalize().mult(steeringComponent.getMaxForce());
		}
	}
}
