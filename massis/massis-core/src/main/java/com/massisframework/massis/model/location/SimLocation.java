package com.massisframework.massis.model.location;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eteks.sweethome3d.model.Selectable;
import com.massisframework.massis.model.building.IBuilding;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.IBuilding;
import com.massisframework.massis.model.building.SimulationObject;
import com.massisframework.massis.util.SH3DUtils;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.io.Restorable;

import odk.lang.FastMath;
import straightedge.geom.KPolygon;

/**
 * Location used by simulation objects
 *
 * @author rpax
 *
 */
public class SimLocation extends Location implements Restorable {

    /**
     * The polygon of the simulation object
     */
    private final KPolygon polygon;
    /**
     * The current angle
     */
    private double angle;
    /**
     * The attached simulation object
     */
    private SimulationObject attached;

    /**
     * @param polygon the polygon representing the element
     * @param floor the floor
     */
    private SimLocation(KPolygon polygon, Floor floor)
    {
        super(polygon.center, floor);
        this.polygon = polygon;
        this.angle = 0;

    }

    /**
     * Creates a SimLocation from a SH3D element
     *
     * @param hpof the SH3D representation
     * @param floor the floor of the ekement
     */
    public SimLocation(Selectable hpof, Floor floor)
    {
        this(SH3DUtils.createKPolygonFromSH3DObj(hpof), floor);
    }

    public void attach(SimulationObject simAgent)
    {
        this.attached = simAgent;
    }

    @Override
    public void translateTo(double x, double y)
    {
        this.polygon.translateTo(x, y);
    }

    @Override
    public void translateTo(Location other)
    {


        final double oldX = this.getX();
        final double oldY = this.getY();
        final double toX = other.getX();
        final double toY = other.getY();
        this.polygon.translateTo(toX, toY);

        this.angle = (FastMath.atan2(toY - oldY, toX - oldX));
        while (this.angle < 0)
        {
            this.angle += Math.PI * 2;
        }
        if (this.floor != other.floor)
        {
            Logger.getLogger(SimLocation.class.getName()).log(
                    Level.INFO,
                    "Changed from {0} to {1}", new Object[]
            
            {
                this.floor.getName()
                ,other.floor.getName()
            }
            );
            this.floor.remove(attached);
        }
        other.floor.addPerson(attached);
        this.floor = other.floor;

    }

    public double getAngle()
    {
        return angle;
    }

    public KPolygon getPolygon()
    {
        return this.polygon;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }

    @Override
    public JsonState<IBuilding> getState()
    {
        return new SimLocationState(this);
    }

    private static class SimLocationState implements JsonState<IBuilding> {

        float angle;
        int floorId;
        int attachedSimulationObjectId = -1;
        float centerX, centerY;

        public SimLocationState(SimLocation l)
        {
            this.angle = (float) l.angle;
            this.floorId = l.floor.getID();
            this.centerX = (float) l.center.x;
            this.centerY = (float) l.center.y;
            if (l.attached != null)
            {
                this.attachedSimulationObjectId = l.attached.getID();
            } else
            {
                this.attachedSimulationObjectId = -1;
            }
        }

        @Override
        public SimLocation restore(IBuilding building)
        {
            /*
             * Se recupera el floor, por un lado
             */
            Floor other = building.getFloorById(floorId);
            /*
             * Por otro, se recupera el SimulationObject correspondiente al
             * identificador attached.
             */
            SimulationObject attached = building
                    .getSimulationObject(attachedSimulationObjectId);
            /*
             * Se obtiene la localizacion de attached y se configura
             */
            SimLocation location = attached.getLocation();
            location.polygon.translateTo(this.centerX, this.centerY);
            location.angle = this.angle;

            if (location.floor != other)
            {

                location.floor.remove(attached);
                other.addPerson(attached);
                location.floor = other;
            }

            return location;
        }
    }

    protected void setAngle(double angle)
    {
        this.angle = angle;
    }
}
