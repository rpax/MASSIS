package com.massisframework.massis.model.managers.movement;

import java.util.List;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Teleport;

import straightedge.geom.KPoint;

/**
 * Holds a path.
 *
 * @author rpax
 *
 */
public class Path {

    private List<KPoint> points;
    private final Teleport target;

    public Path(List<KPoint> points, Teleport teleport)
    {
        super();
        this.points = points;
        this.target = teleport;
    }

    public Path(List<KPoint> points)
    {
        this(points, null);
    }

    public boolean isInTargetTeleport(DefaultAgent v)
    {

        return this.target != null
                && this.target.getPolygon().intersects(v.getPolygon());
    }

    public List<KPoint> getPoints()
    {
        return points;
    }

    public void setPoints(List<KPoint> points)
    {
        this.points = points;
    }

    public Teleport getTargetTeleport()
    {
        return target;
    }

    public boolean isEmpty()
    {
        return this.points.isEmpty();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Path [lastPoint=");
        builder.append(points.get(points.size() - 1));
        builder.append(", targetTeleport=");
        builder.append(target);
        builder.append("]");
        return builder.toString();
    }
}
