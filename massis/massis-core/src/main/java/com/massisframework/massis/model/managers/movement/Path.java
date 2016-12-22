//package com.massisframework.massis.model.managers.movement;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.massisframework.massis.model.building.WayPoint;
//import com.massisframework.massis.model.components.TeleportComponent;
//import com.massisframework.massis.model.managers.pathfinding.PathFollower;
//
//import straightedge.geom.KPoint;
//
///**
// * Holds a path.
// *
// * @author rpax
// *
// */
//public class Path {
//
//	private List<WayPoint> wayPoints;
//	private final TeleportComponent target;
//
//	public Path(List<KPoint> points, TeleportComponent teleport) {
//		super();
//		this.target = teleport;
//		this.wayPoints = new ArrayList<>(points.size());
//		for (final KPoint kPoint : points) {
//			this.wayPoints.add(new WayPoint() {
//
//				@Override
//				public boolean executeWayPointAction(PathFollower pf) {
//					/*
//					 * Location has not changed in this execution
//					 */
//					return false;
//				}
//				@Override
//				public double getX() {
//					return kPoint.x;
//				}
//				@Override
//				public double getY() {
//					return kPoint.y;
//				}
//				@Override
//				public KPoint getXY() {
//					return kPoint;
//				}
//				@Override
//				public boolean canExecuteWayPointAction(PathFollower pf) {
//					//There is no action to execute
//					return false;
//				}
//			});
//		}
//		// this.target = teleport;
//		if (this.target != null)
//		{
//			this.wayPoints.add(this.target);
//		}
//	}
//
//	public Path(List<KPoint> points) {
//		this(points, null);
//	}
//
//	
//
//	public List<WayPoint> getPoints() {
//		return this.wayPoints;
//	}
//
//	public void setPoints(List<WayPoint> points) {
//		this.wayPoints = points;
//	}
//
//	public TeleportComponent getTargetTeleport() {
//		return this.target;
//	}
//
//	public boolean isEmpty() {
//		return this.wayPoints.isEmpty();
//	}
//
//	@Override
//	public String toString() {
//		final StringBuilder builder = new StringBuilder();
//		builder.append("Path [lastPoint=");
//		builder.append(this.wayPoints.get(this.wayPoints.size() - 1));
//		builder.append(", targetTeleport=");
//		builder.append(this.target);
//		builder.append("]");
//		return builder.toString();
//	}
//
//}
