/**
 *
 */
package rpax.massis.model.building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
import rpax.massis.util.io.JsonState;
import straightedge.geom.KPoint;

/**
 * Represents a Door in MASSIS
 *
 * @author rpax
 *
 */
public class SimDoor extends SimulationObject implements RoomConnector {

    private static final long serialVersionUID = 1L;
    /**
     * The rooms connected by this Door
     */
    private List<SimRoom> connectedRooms;
    /**
     * If it is open or not. by default is true.
     */
    private boolean open = true;

    public SimDoor(Map<String, String> metadata, SimLocation location,
            MovementManager movementManager, AnimationManager animationManager,
            EnvironmentManager environment)
    {
        super(metadata, location, movementManager, animationManager,
                environment);
    }

    /**
     * Iterates over all rooms in this floor and, if they intersect, are added
     * to the connected rooms list.
     */
    private void computeRoomConnections()
    {
        this.connectedRooms = new ArrayList<>();
        // Por sentido comun: los cuartos que intersecta son los que conecta.
        for (SimRoom sr : this.getLocation().getFloor().getRooms())
        {
            if (this.getPolygon().intersects(sr.getPolygon()))
            {
                this.connectedRooms.add(sr);
            } else
            {

                KPoint doorCenter = sr.getPolygon().getCenter();
                KPoint roomBound = this.getPolygon().getBoundaryPointClosestTo(
                        doorCenter);
                KPoint doorBound = sr.getPolygon().getBoundaryPointClosestTo(
                        roomBound);
                if (roomBound.distance(doorBound) < 1)
                {
                    this.connectedRooms.add(sr);
                }
            }

        }
    }

    @Override
    public List<SimRoom> getConnectedRooms()
    {
        if (this.connectedRooms == null)
        {
            this.computeRoomConnections();
        }
        return Collections.unmodifiableList(connectedRooms);
    }

    @Override
    public String toString()
    {

        return super.toString() + " [" + getConnectedRooms().get(0) + ","
                + getConnectedRooms().get(1) + "]";
    }

    public boolean isOpened()
    {
        return this.open;
    }

    public boolean isClosed()
    {
        return !this.open;
    }

    public void setOpen(boolean open)
    {
        this.open = open;
        this.notifyChanged();
    }

    @Override
    public SimDoorState getState()
    {
        return new SimDoorState(this, super.getState());
    }

    public static class SimDoorState implements JsonState<Building> {

        private final boolean isOpen;
        private final JsonState data;

        public SimDoorState(SimDoor d, JsonState simulationObjectData)
        {
            this.data = simulationObjectData;
            this.isOpen = d.open;
        }

        @Override
        public SimDoor restore(Building building)
        {
            SimDoor d = (SimDoor) data.restore(building);
            d.open = this.isOpen;
            return d;
        }
    }
}
