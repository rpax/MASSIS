package com.massisframework.massis.model.agents;


import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.StringUtils;

import com.massisframework.massis.model.building.Building;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.RoomConnector;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.building.SimulationObject;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.movement.steering.CollisionAvoidance;
import com.massisframework.massis.model.managers.movement.steering.Containment;
import com.massisframework.massis.model.managers.movement.steering.FollowPath;
import com.massisframework.massis.model.managers.movement.steering.SteeringBehavior;
import com.massisframework.massis.model.managers.movement.steering.SteeringCombinationBehavior;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.massis.util.geom.KPolygonUtils;
import com.massisframework.massis.util.geom.KVector;
import com.massisframework.massis.util.io.JsonState;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

/**
 * Represents an agent in the simulation environment
 *
 * @author rpax
 *
 */
//Path path = Paths.get(this.resourcesFolder,metadata.get(PLAN_FILE_PATH)).toString();
public class DefaultAgent extends SimulationObject implements
        LowLevelAgent {

    private static final long serialVersionUID = 1L;
    /**
     * Current velocity
     */
    protected KVector velocity = new KVector(Math.random(), Math.random());
    /**
     * La aceleracion
     */
    protected KVector acceleration = new KVector(0, 0);
    /**
     * Number of points of the vision polygon
     */
    private final int visionRadioPolygonNumPoints = DEFAULT_VISION_POLY_POINTS;
    /**
     * Radio of the vision polygon
     */
    private double visionRadio = DEFAULT_VISION_RADIO;
    protected double maxforce = DEFAULT_MAX_FORCE;
    protected double maxspeed = DEFAULT_MAX_SPEED;
    // ==================================
    // Cached values & Flags - transient
    // current/last Known room
    private SimRoom lastKnowRoom = null;
    private boolean lastKnownRoomUpdated = false;
    private boolean peopleInVisionRadioUpdated = false;
    private List<DefaultAgent> peopleInVisionArea = null;
    //
    private boolean visionFinderUpdated = false;
    private Shape visionPolygon;
    private double lastAngleOfVision;
    private KPolygon boundaryPolygon;
    private HighLevelController highLevelController;
    public static final double VISION_RADIO_SCALE;
    private final SteeringBehavior steeringBehavior;
    private final boolean isObstacle;
    private final boolean isDynamic;
    private Object highLevelData;
    private static final int DEFAULT_VISION_POLY_POINTS;
    private static final double DEFAULT_VISION_RADIO,
            DEFAULT_MAX_FORCE,
            DEFAULT_MAX_SPEED;

    static
    {
        ResourceBundle bundle = ResourceBundle.getBundle(
                DefaultAgent.class.getName());
        DEFAULT_VISION_RADIO = Double.parseDouble(
                bundle.getString("visionRadio"));
        VISION_RADIO_SCALE = Double.parseDouble(bundle.getString(
                "visionRadioScale"));
        DEFAULT_MAX_FORCE = Double.parseDouble(bundle.getString("maxforce"));
        DEFAULT_MAX_SPEED = Double.parseDouble(bundle.getString("maxspeed"));
        DEFAULT_VISION_POLY_POINTS = Integer.parseInt(bundle.getString(
                "visionRadioPolygonNumPoints"));

    }

    public DefaultAgent(
            Map<String, String> metadata,
            SimLocation location,
            MovementManager movementManager,
            AnimationManager animationManager,
            EnvironmentManager environment)
    {
        super(metadata, location, movementManager, animationManager,
                environment);
        this.peopleInVisionArea = new ArrayList<>();
        this.createVisionRadioPolygon();
        /*
         * Compute cached values
         */
        this.clearCache();
        this.steeringBehavior = createSteeringBehavior();
        this.isObstacle = getBooleanFromMetadata(
                SimObjectProperty.IS_OBSTACLE.toString(),
                metadata, true);

        this.isDynamic = getBooleanFromMetadata(
                SimObjectProperty.IS_DYNAMIC.toString(),
                metadata, false);

    }

//    @Override
//    public final void step()
//    {
//        this.highLevelController.logic();
//    }
    /**
     * Creates the vision polygon
     */
    protected final void createVisionRadioPolygon()
    {
        boundaryPolygon = KPolygon.createRegularPolygon(
                visionRadioPolygonNumPoints, visionRadio);
        boundaryPolygon.translateTo(this.getXY());

    }

    /**
     * Checks if the vision polygon must be recomputed, an if it so, recomputes
     * it
     */
    private void computeVisionFinderCalc()
    {
        if (this.visionFinderUpdated)
        {
            return;
        }
        /*
         * rotates the current angle 
         */
        boundaryPolygon.rotate(this.getAngle() - lastAngleOfVision);
        /*
         * Set last angle of vision
         */
        this.lastAngleOfVision = this.getAngle();
        /*
         * Move the polygon accordingly
         */
        boundaryPolygon.translateTo(this.getXY());
        /*
         * Intersect the polygon with the room boundaries
         */
        visionPolygon = KPolygonUtils.intersection(boundaryPolygon, this
                .getRoom().getPolygon());
        /*
         * If the operation was done successfully
         */
        if (visionPolygon == null)
        {
            visionPolygon = this.getPolygon();
        }
        /*
         * set the flag to 1
         */
        this.visionFinderUpdated = true;
    }

    @Override
    public void moveTo(Location other)
    {
        super.moveTo(other);
        this.clearCache();
    }

    /**
     * Clear cached values
     */
    public final void clearCache()
    {
        this.lastKnownRoomUpdated = false;
        this.visionFinderUpdated = false;
        this.peopleInVisionRadioUpdated = false;
    }

    /**
     *
     * @return the {@link Shape} of the vision polygon
     */
    @Override
    public Shape getVisionRadioShape()
    {
        computeVisionFinderCalc();
        return this.visionPolygon;

    }

    /**
     *
     * @param p the point to check
     * @return if the point is contained in the vision area
     */
    @Override
    public boolean isPointContainedInVisionArea(KPoint p)
    {
        if (this.getLocation().distance2D(p) > this.visionRadio)
        {
            return false;
        }
        return this.getVisionRadioShape().contains(p.x, p.y);
    }

    /**
     * Computation of the visible agents
     */
    private void computePeopleInVisionArea()
    {

        if (!this.peopleInVisionRadioUpdated)
        {
            this.peopleInVisionRadioUpdated = true;
            ArrayList<DefaultAgent> peopleInVisionArea_tmp = new ArrayList<>();

            for (DefaultAgent agent : this.getAgentsInRange(
                    this.getVisionRadio()))
            {
                {
                    peopleInVisionArea_tmp.add(agent);
                }
            }
            this.peopleInVisionArea = peopleInVisionArea_tmp;

        }
    }

    @Override
    public boolean isObjectPerceived(LowLevelAgent other)
    {
        return other.getLocation().distance2D(this.getLocation()) < this.visionRadio
                && other.getRoom() == this.getRoom();
    }

    @Override
    public Iterable<DefaultAgent> getAgentsInVisionRadio()
    {
        this.computePeopleInVisionArea();
        return this.peopleInVisionArea;
    }

    @Override
    public Iterable<DefaultAgent> getAgentsInRange(double range)
    {
        return this.getEnvironment().getAgentsInRange(this, range);
    }

    @Override
    public double getVisionRadio()
    {
        return this.visionRadio;
    }

    @Override
    public boolean isInDoorArea()
    {
        for (RoomConnector sd : this.getRoom().getConnectedRoomConnectors())
        {
            if (sd.getPolygon().getAABB()
                    .intersects(this.getPolygon().getAABB()))
            {
                return true;
            }
        }
        return false;
    }
    /*
     * Speed up methods
     */

    public void setLastKnowRoom(SimRoom lastKnowRoom)
    {
        this.lastKnowRoom = lastKnowRoom;
        this.lastKnownRoomUpdated = true;
    }

    private void computeLastKnownRoom()
    {
        if (this.lastKnownRoomUpdated)
        {
            return;
        }
        if (lastKnowRoom != null)
        {
            findRoomByLastKnownRoom();
        } else
        {
            for (SimRoom sr : this.getLocation().getFloor().getRooms())
            {
                if (sr.getPolygon().getRadius() > KPoint.distance(
                        sr.getPolygon().center, this.getPolygon().center))
                {
                    if (sr.getPolygon().contains(this.getPolygon().center))
                    {
                        lastKnowRoom = sr;
                        break;
                    }
                }
            }
        }
        this.lastKnownRoomUpdated = true;
    }

    @Override
    public SimRoom getRoom()
    {
        this.computeLastKnownRoom();
        return this.lastKnowRoom;
    }

    @Override
    public Collection<DefaultAgent> getAgentsInRoom()
    {
        this.computeLastKnownRoom();
        return this.lastKnowRoom.getPeopleIn();
    }

    private SimRoom findRoomByLastKnownRoom()
    {
        final double x = this.getX();
        final double y = this.getY();
        final SimRoom lastKnown = this.lastKnowRoom;
        for (SimRoom sr : lastKnown.getRoomsOrderedByDistance())
        {
            if (sr.getPolygon().contains(x, y))
            {
                lastKnowRoom = sr;
                return lastKnowRoom;
            }
        }
        return this.lastKnowRoom;
    }

    public int getVisionRadioPolygonNumPoints()
    {
        return visionRadioPolygonNumPoints;
    }

    public double getMaxSpeed()
    {
        return this.maxspeed;
    }

    @Override
    public KVector getVelocity()
    {
        return this.velocity;
    }

    @Override
    public double getMaxForce()
    {
        return this.maxforce;
    }

    @Override
    public KVector getAcceleration()
    {
        return acceleration;
    }

    @Override
    public JsonState<Building> getState()
    {

        return new VehicleState(this, super.getState());
    }

    @Override
    public boolean isObstacle()
    {
        return this.isObstacle;
    }

    @Override
    public boolean isDynamic()
    {
        return this.isDynamic;
    }

    private boolean getBooleanFromMetadata(String key,
            Map<String, String> metadata, boolean defaultValue)
    {
        final String strVal = StringUtils.defaultIfEmpty(metadata.get(key),
                String.valueOf(defaultValue));
        return Boolean.parseBoolean(strVal);
    }

    @Override
    public double getBodyRadius()
    {
        return this.getPolygon().getRadius();
    }

    public boolean approachToNamedLocation(String name)
    {
        Location namedLocation =
                this
                .getEnvironment()
                .getNamedLocation(name);
        Objects.requireNonNull(namedLocation);
        return this.approachTo(namedLocation);
    }

    @Override
    public boolean isInNamedLocation(String name, int radiusWithin)
    {
        Location namedLocation =
                this
                .getEnvironment()
                .getNamedLocation(name);
        Objects.requireNonNull(namedLocation);
        return namedLocation.isInSameFloor(namedLocation)
                && namedLocation.distance2D(this.getLocation()) < radiusWithin;
    }

    @Override
    public Location approachToRandomTarget()
    {
        /*
         * Common variables
         */
        Location target = null;
        KPoint p = new KPoint();
        Random rnd = ThreadLocalRandom.current();
        do
        {
            /*
             * Auxiliary "random" room & its shape
             */
            SimRoom sr;
            KPolygon roomPoly;
            Floor roomFloor;
            do
            {
                /*
                 * Initialization
                 */
                sr = this.getEnvironment().getRandomRoom();
                roomPoly = sr.getPolygon();
                roomFloor = sr.getLocation().getFloor();

                final Rectangle2D.Double bounds = roomPoly.getBounds2D();
                final int bounds_width = (int) bounds.getWidth();
                final int bounds_height = (int) bounds.getHeight();

                /*
                 * Random point in bounds
                 */
                p.x = bounds.getX() + rnd.nextInt(bounds_width);
                p.y = bounds.getY() + rnd.nextInt(bounds_height);
                /*
                 * Try to get a point outside obstacles
                 */
                p = roomFloor.getNearestPointOutsideOfObstacles(
                        p);
                /*
                 * If the room does not contain the point,
                 * repeat with another rnd room
                 */
            } while (!roomPoly.contains(p));
            /*
             * Create the target.
             */
            target = new Location(p, roomFloor);
            /*
             * Exists the (remote) possibility that the agent is in
             * the rnd target selected.In that case, repeat the whole loop.
             */
        } while (this.approachTo(target));
        return target;
    }

    protected static class VehicleState implements JsonState<Building> {

        private final KVector velocity;
        private final double visionRadio;
        private final double maxforce;
        private final double maxspeed;
        private final JsonState<Building> data;

        public VehicleState(DefaultAgent v, JsonState<Building> data)
        {
            this.data = data;
            this.velocity = v.velocity.copy();
            this.visionRadio = v.visionRadio;
            this.maxforce = v.maxforce;
            this.maxspeed = v.maxspeed;
        }

        @Override
        public Object restore(Building building)
        {
            DefaultAgent v = (DefaultAgent) this.data.restore(building);
            v.velocity = this.velocity;
            v.visionRadio = this.visionRadio;
            v.maxforce = this.maxforce;
            v.maxspeed = this.maxspeed;
            return v;
        }
    }

    public void setVelocity(KVector v)
    {
        this.velocity.x = v.x;
        this.velocity.y = v.y;
    }

    public void setAcceleration(KVector acc)
    {
        this.acceleration.x = acc.x;
        this.acceleration.y = acc.y;
    }

    @Override
    public List<KPoint> getPath()
    {
        return this.getMovementManager().getPathOf(this);
    }

    @Override
    public boolean approachTo(Location location)
    {
        return this.getMovementManager().approachTo(this, location);
    }

    public final SteeringBehavior createSteeringBehavior()
    {
        return new SteeringCombinationBehavior(this, new Containment(this),
                new CollisionAvoidance(this, 125),
                new FollowPath(this, 25, 100));
    }

    public SteeringBehavior getSteeringBehavior()
    {
        return steeringBehavior;
    }

    public Object getHighLevelData()
    {
        return highLevelData;
    }

    public void setHighLevelData(Object highLevelData)
    {
        this.highLevelData = highLevelData;
    }
}
