/**
 *
 */
package com.massisframework.massis.model.building;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.util.geom.KPolygonUtils;
import com.massisframework.massis.util.io.JsonState;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import straightedge.geom.KPoint;
import straightedge.geom.vision.Occluder;

/**
 * Represents a room in MASSIS
 *
 * @author rpax
 *
 */
public class SimRoom extends SimulationObject implements Occluder, Steppable, Stoppable {

    private static final long serialVersionUID = 1L;
    /**
     * The connectors attached to this room. (e.g Doors/teleports)
     */
    private List<RoomConnector> connectedConnectors;
    /**
     * Connected rooms , BFS order
     */
    private List<SimRoom> roomsOrderedByDistance;
    // Cached values
    private final Collection<DefaultAgent> vehiclesInThisRoomCached = new ArrayList<>();
    private boolean vehiclesInThisRoomComputed = false;

    public SimRoom(Map<String, String> metadata, SimLocation location,
            MovementManager movementManager, AnimationManager animationManager,
            EnvironmentManager environment)
    {
        super(metadata, location, movementManager, animationManager,
                environment);
    }

    /**
     * Searches for the connectors in this floor and adds them to the list
     */
    private void computeDoorConnections()
    {
        this.connectedConnectors = new ArrayList<>();
        // Por sentido comun: las puertas que intersecta son los que conecta.
        for (RoomConnector sr : this.getRoomsConnectorsInSameFloor())
        {
            if (this.getPolygon().intersects(sr.getPolygon()))
            {
                this.connectedConnectors.add(sr);
            } else
            {

                KPoint doorCenter = sr.getPolygon().getCenter();
                KPoint roomBound = this.getPolygon().getBoundaryPointClosestTo(
                        doorCenter);
                KPoint doorBound = sr.getPolygon().getBoundaryPointClosestTo(
                        roomBound);
                if (roomBound.distance(doorBound) < 1)
                {
                    this.connectedConnectors.add(sr);
                }
            }

        }
    }

    /**
     *
     * @return the rooms ordered by distance, BFS
     */
    public List<SimRoom> getRoomsOrderedByDistance()
    {
        if (this.roomsOrderedByDistance == null)
        {
            this.roomsOrderedByDistance = new ArrayList<>();
            HashSet<SimRoom> visitedRooms = new HashSet<SimRoom>();
            Queue<SimRoom> queue = new LinkedList<SimRoom>();
            visitedRooms.add(this);
            queue.add(this);
            while (!queue.isEmpty())
            {
                SimRoom currentRoom = queue.poll();
                visitedRooms.add(currentRoom);
                this.roomsOrderedByDistance.add(currentRoom);
                for (RoomConnector sd : currentRoom
                        .getConnectedRoomConnectors())
                {
                    for (SimRoom sr : sd.getConnectedRooms())
                    {
                        if (!visitedRooms.contains(sr))
                        {
                            visitedRooms.add(sr);
                            queue.add(sr);
                        }
                    }
                }
            }

        }
        return Collections.unmodifiableList(this.roomsOrderedByDistance);
    }

    /**
     *
     * @return the connectors of this room. Using that room connectors one agent
     * can move from one room to another
     */
    public List<RoomConnector> getConnectedRoomConnectors()
    {

        if (this.connectedConnectors == null)
        {
            computeDoorConnections();
        }
        return Collections.unmodifiableList(connectedConnectors);
    }

    public KPoint getBoundaryPointClosestTo(KPoint p)
    {
        return this.getPolygon().getBoundaryPointClosestTo(p);
    }

    public KPoint[] getBoundaryPointsClosestTo(KPoint p, int npoints)
    {
        return KPolygonUtils.getBoundaryPointsClosestTo(this.getPolygon(), p.x,
                p.y, npoints);
    }

    public double getDistanceOfBoundaryPointClosestTo(KPoint p)
    {

        return this.getPolygon().getBoundaryPointClosestTo(p).distance(p);

    }

    /**
     * @deprecated use {@link #getPeopleIn()} instead
     * @return the people in this room.
     *
     */
    @Deprecated
    public Iterator<DefaultAgent> getPeopleInIterator()
    {
        return new FilterIterator<DefaultAgent>(this.getLocation().getFloor()
                .getPeople().iterator(), new PeopleInThisRoomPredicate());
    }

    /**
     *
     * @return the people in this room (Agents)
     */
    public Collection<DefaultAgent> getPeopleIn()
    {
        cacheVehiclesInThisRoom();
        return this.vehiclesInThisRoomCached;
    }

    @Override
    public void step(SimState s)
    {
        this.clearFlags();
    }

    private void clearFlags()
    {
        this.vehiclesInThisRoomComputed = false;
    }

    /**
     * TODO rpax why the quadtree is not being used here?? Caches the people in
     * this room
     */
    private void cacheVehiclesInThisRoom()
    {
        if (!this.vehiclesInThisRoomComputed)
        {
            this.vehiclesInThisRoomCached.clear();
            Iterator<DefaultAgent> it = this.getPeopleInIterator();
            while (it.hasNext())
            {
                this.vehiclesInThisRoomCached.add(it.next());
            }
            this.vehiclesInThisRoomComputed = true;
        }
    }

    @Override
    public void stop()
    {
    }

    private class PeopleInThisRoomPredicate implements Predicate<DefaultAgent> {

        @Override
        public boolean evaluate(DefaultAgent person)
        {
            return (SimRoom.this == person.getRoom());
        }
    }

    @Override
    public JsonState<Building> getState()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    public Location getRandomLoc()
    {
        Random rnd = ThreadLocalRandom.current();
        Rectangle2D.Double bounds = this.getPolygon().getBounds2D();
        KPoint p = new KPoint();
        do
        {
            p.x = bounds.getX() + rnd.nextInt((int) bounds.getWidth());
            p.y = bounds.getY() + rnd.nextInt((int) bounds.getHeight());
            p = this.getLocation().getFloor()
                    .getNearestPointOutsideOfObstacles(p);
        } while (!this.getPolygon().contains(p));
        return new Location(p.x, p.y, this.getLocation().getFloor());

    }
}
