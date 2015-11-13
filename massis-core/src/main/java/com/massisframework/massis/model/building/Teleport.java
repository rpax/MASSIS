package com.massisframework.massis.model.building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.massis.util.io.JsonState;

/**
 * Represents a Teleport in the building.
 * <p>
 * Teleports are special elements that provide the functionality of changing
 * instantly the location of an element (or at least, it is intended to use them
 * in that way).<br>
 * </br> Special elements in the building that, as the name suggests, they
 * teleport the agent from one location to another, and they are unidirectional.
 * A correctly configured teleport consists on two elements, representing the
 * origin area and the destination area.
 * </p>
 *
 * @author rpax
 *
 */
public class Teleport extends SimulationObject implements RoomConnector {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final byte START = 0;
    public static final byte END = 1;
    //
    private final byte type;
    private final String name;
    private Teleport connection;
    private List<SimRoom> target;
    private final HashMap<Floor, Integer> floorDistances;

    public Teleport(Map<String, String> metadata, SimLocation location,
            MovementManager movementManager, AnimationManager animationManager,
            EnvironmentManager environment)
    {
        super(metadata, location, movementManager, animationManager,
                environment);
        this.type = metadata.get(SimObjectProperty.TYPE.toString()).equalsIgnoreCase(SimObjectProperty.START.toString()) ? START
                : END;
        this.name = metadata.get(SimObjectProperty.TELEPORT.toString());
        this.floorDistances = new HashMap<>();
    }

    public void doTeleport(SimulationObject simulationObject)
    {
        if (this.getType() == START)
        {
            if (this.connection == null)
            {
                throw new UnsupportedOperationException(
                        "Target was not assigned");
            } else
            {
                simulationObject.moveTo(this.connection.getLocation());
            }
        }
    }

    public boolean isInTeleport(Location loc)
    {
        final boolean isIn= (this.getLocation().isInSameFloor(loc) && this.getPolygon()
                .contains(loc.getX(), loc.getY()));
        
        return isIn;
    }

    public String getName()
    {
        return this.name;
    }

    public Teleport getConnection()
    {
        return connection;
    }

    public void setConnection(Teleport connection)
    {
        this.connection = connection;
    }

    public byte getType()
    {
        return type;
    }

    @Override
    public List<SimRoom> getConnectedRooms()
    {
        if (this.type == END)
        {
            return Collections.emptyList();
        } else if (target == null)
        {
            for (SimRoom sr : this.getConnection().getLocation().getFloor()
                    .getRooms())
            {
                if (this.getConnection().getPolygon()
                        .intersects(sr.getPolygon()))
                {
                    this.target = Collections.unmodifiableList(Arrays
                            .asList(sr));
                    break;
                }
            }
        }
        return target;
    }

    protected void setDistanceToFloor(Floor f, int distance)
    {
        this.floorDistances.put(f, distance);
    }

    public int getDistanceToFloor(Floor f)
    {
        if (!this.floorDistances.containsKey(f))
        {
            this.floorDistances.put(f, Integer.MAX_VALUE);
        }
        return this.floorDistances.get(f);
    }

    /**
     * Computes the distances of the teleports provided as a graph
     *
     * @param teleports
     */
    public static void computeTeleportDistances(List<Teleport> teleports)
    {
        ArrayList<TeleportNode> graph = new ArrayList<>();
        HashMap<Teleport, TeleportNode> tmap = new HashMap<>();

        // 1. construccion grafo
        for (Teleport t : teleports)
        {
            TeleportNode node = new TeleportNode(t);
            tmap.put(t, node);
            graph.add(node);
        }
        // 2. Vecinos
        for (Teleport t : teleports)
        {
            for (Teleport neigh : t.getLocation().getFloor().getTeleports())
            {
                if (neigh != t)
                {
                    if (tmap.get(neigh) != null)
                    {
                        tmap.get(t).addNeighbour(tmap.get(neigh), 1);
                    }

                }
            }
            if (t.getType() == START)
            {
                if (tmap.get(t.getConnection()) != null)
                {
                    tmap.get(t).addNeighbour(tmap.get(t.getConnection()), 10);
                }

            }

        }

        for (int i = 0; i < graph.size(); i++)
        {
            // Si no es un punto de partida no interesa
            if (graph.get(i).getTeleport().getType() != START)
            {
                continue;
            }
            // se resetean todos los nodos
            for (TeleportNode node : graph)
            {
                node.distance = Integer.MAX_VALUE;
            }

            TeleportNode source = graph.get(i);
            source.distance = 0;
            // Caminos minimos
            PriorityQueue<TeleportNode> heap = new PriorityQueue<>();
            heap.add(source);
            while (!heap.isEmpty())
            {
                TeleportNode u = heap.poll();

                for (TeleportVertex v : u.neighbours)
                {
                    if (u.distance + v.distance < v.n.distance)
                    {
                        v.n.distance = u.distance + v.distance;
                        heap.add(v.n);
                    }
                }

            }
            for (TeleportNode node : graph)
            {
                Floor targetFloor = node.getTeleport().getLocation().getFloor();
                int oldDist = source.teleport.getDistanceToFloor(targetFloor);
                int newDist = node.distance;
                int minDist = Math.min(oldDist, newDist);
                source.teleport.setDistanceToFloor(targetFloor, minDist);
            }

        }

    }

    private static class TeleportNode implements Comparable<TeleportNode> {

        private final Teleport teleport;
        private final ArrayList<TeleportVertex> neighbours;
        private int distance;

        public TeleportNode(Teleport teleport)
        {
            this.teleport = teleport;
            this.neighbours = new ArrayList<>();
        }

        @Override
        public int compareTo(TeleportNode o)
        {
            return Integer.compare(this.distance, o.distance);
        }

        public Teleport getTeleport()
        {
            return teleport;
        }

        public void addNeighbour(TeleportNode n, int d)
        {

            this.neighbours.add(new TeleportVertex(n, d));
        }
    }

    private static class TeleportVertex {

        TeleportNode n;
        int distance;

        public TeleportVertex(TeleportNode n, int distance)
        {
            this.n = n;
            this.distance = distance;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Teleport [type=");
        builder.append(type == START ? "START" : "END");
        builder.append(", name=");
        builder.append(name);
        builder.append(", location=");
        builder.append(this.getLocation());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public JsonState getState()
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
