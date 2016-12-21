//package com.massisframework.massis.model.building.impl;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.PriorityQueue;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import com.massisframework.massis.model.building.Building;
//import com.massisframework.massis.model.building.Floor;
//import com.massisframework.massis.model.building.SimRoom;
//import com.massisframework.massis.model.building.SimulationObject;
//import com.massisframework.massis.model.building.Teleport;
//import com.massisframework.massis.model.components.TeleportComponent;
//import com.massisframework.massis.model.components.TeleportComponent.TeleportType;
//import com.massisframework.massis.model.components.building.Coordinate2DComponent;
//import com.massisframework.massis.model.components.building.FloorContainmentComponent;
//import com.massisframework.massis.model.location.Location;
//import com.massisframework.massis.model.location.SimLocation;
//import com.massisframework.massis.model.managers.AnimationManager;
//import com.massisframework.massis.model.managers.EnvironmentManager;
//import com.massisframework.massis.model.managers.movement.MovementManager;
//import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
//import com.massisframework.massis.model.managers.pathfinding.PathFollower;
//import com.massisframework.massis.sim.SimulationEntity;
//import com.massisframework.massis.util.SimObjectProperty;
//import com.massisframework.massis.util.geom.KPolygonUtils;
//import com.massisframework.massis.util.io.JsonState;
//
///**
// * Represents a Teleport in the building.
// * <p>
// * Teleports are special elements that provide the functionality of changing
// * instantly the location of an element (or at least, it is intended to use them
// * in that way).<br>
// * </br>
// * Special elements in the building that, as the name suggests, they teleport
// * the agent from one location to another, and they are unidirectional. A
// * correctly configured teleport consists on two elements, representing the
// * origin area and the destination area.
// * </p>
// *
// * @author rpax
// *
// */
//public class TeleportImpl extends SimulationObject
//		implements Teleport {
//
//	//
//	private final byte type;
//	private final String name;
//	private SimulationEntity connection;
//	private List<SimRoom> target;
//	private final HashMap<Floor, Integer> floorDistances;
//
//	public TeleportImpl(Map<String, String> metadata, SimLocation location,
//			MovementManager movementManager, AnimationManager animationManager,
//			EnvironmentManager environment, PathFindingManager pathManager)
//	{
//		super(metadata, location, movementManager, animationManager,
//				environment, pathManager);
//		this.type = metadata.get(SimObjectProperty.TYPE.toString())
//				.equalsIgnoreCase(SimObjectProperty.START.toString()) ? START
//						: END;
//		this.name = metadata.get(SimObjectProperty.TELEPORT.toString());
//		this.floorDistances = new HashMap<>();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.massisframework.massis.model.building.ITeleport#isInTeleport(com.
//	 * massisframework.massis.model.location.Location)
//	 */
//	@Override
//	public boolean isInTeleport(Location loc)
//	{
//		final boolean isIn = (this.getLocation().isInSameFloor(loc)
//				&& this.getPolygon().contains(loc.getX(), loc.getY()));
//
//		return isIn;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.ITeleport#getName()
//	 */
//	@Override
//	public String getName()
//	{
//		return this.name;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.ITeleport#getConnection()
//	 */
//	@Override
//	public SimulationEntity getConnection()
//	{
//		return connection;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.massisframework.massis.model.building.ITeleport#setConnection(com.
//	 * massisframework.massis.model.building.Teleport)
//	 */
//	@Override
//	public void setConnection(SimulationEntity connection)
//	{
//		this.connection = connection;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.ITeleport#getType()
//	 */
//	@Override
//	public byte getType()
//	{
//		return type;
//	}
//
//	// /*
//	// * (non-Javadoc)
//	// *
//	// * @see
//	// * com.massisframework.massis.model.building.ITeleport#getConnectedRooms()
//	// */
//	// @Override
//	// public List<SimulationEntity> getConnectedRooms()
//	// {
//	// if (this.type == END)
//	// {
//	// return Collections.emptyList();
//	// } else if (target == null)
//	// {
//	// for (SimulationEntity sr : this.getConnection().getLocation()
//	// .getFloor().getRooms())
//	// {
//	// if (this.getConnection().getPolygon()
//	// .intersects(sr.getPolygon()))
//	// {
//	// this.target = Collections
//	// .unmodifiableList(Arrays.asList(sr));
//	// break;
//	// }
//	// }
//	// }
//	// return target;
//	// }
//
//	@Override
//	public void setDistanceToFloor(Floor f, int distance)
//	{
//		this.floorDistances.put(f, distance);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.massisframework.massis.model.building.ITeleport#getDistanceToFloor(
//	 * com.massisframework.massis.model.building.Floor)
//	 */
//	@Override
//	public int getDistanceToFloor(Floor f)
//	{
//		if (!this.floorDistances.containsKey(f))
//		{
//			this.floorDistances.put(f, Integer.MAX_VALUE);
//		}
//		return this.floorDistances.get(f);
//	}
//
//	/**
//	 * Computes the distances of the teleports provided as a graph
//	 *
//	 * @param teleports
//	 */
//	public static void computeTeleportDistances(
//			List<SimulationEntity> teleports)
//	{
////		ArrayList<TeleportNode> graph = new ArrayList<>();
////		Map<SimulationEntity, TeleportNode> tmap = new HashMap<>();
////
////		// 1. construccion grafo
////		for (SimulationEntity t : teleports)
////		{
////			TeleportNode node = new TeleportNode(t);
////			tmap.put(t, node);
////			graph.add(node);
////		}
////		// 2. Vecinos
////		for (SimulationEntity se : teleports)
////		{
////			TeleportComponent t=se.get(TeleportComponent.class);
////			Floor floor=se.get(FloorContainmentComponent.class).getFloor();
////			
////			for (SimulationEntity neigh : floor.getTeleports())
////			{
////				if (neigh != t)
////				{
////					if (tmap.get(neigh) != null)
////					{
////						tmap.get(t).addNeighbour(tmap.get(neigh), 1);
////					}
////
////				}
////			}
////			if (t.getTeleportType() == TeleportType.START)
////			{
////				if (tmap.get(t.getTarget()) != null)
////				{
////					tmap.get(t).addNeighbour(tmap.get(t.getTarget()), 10);
////				}
////
////			}
////
////		}
////
////		for (int i = 0; i < graph.size(); i++)
////		{
////			// Si no es un punto de partida no interesa
////			if (graph.get(i).getTeleport().get(TeleportComponent.class).getTeleportType() != TeleportType.START)
////			{
////				continue;
////			}
////			// se resetean todos los nodos
////			for (TeleportNode node : graph)
////			{
////				node.distance = Integer.MAX_VALUE;
////			}
////
////			TeleportNode source = graph.get(i);
////			source.distance = 0;
////			// Caminos minimos
////			PriorityQueue<TeleportNode> heap = new PriorityQueue<>();
////			heap.add(source);
////			while (!heap.isEmpty())
////			{
////				TeleportNode u = heap.poll();
////
////				for (TeleportVertex v : u.neighbours)
////				{
////					if (u.distance + v.distance < v.n.distance)
////					{
////						v.n.distance = u.distance + v.distance;
////						heap.add(v.n);
////					}
////				}
////
////			}
////			for (TeleportNode node : graph)
////			{
////				Floor targetFloor = node.getTeleport().get(FloorContainmentComponent.class).getFloor();
////				int oldDist = source.teleport.getDistanceToFloor(targetFloor);
////				int newDist = node.distance;
////				int minDist = Math.min(oldDist, newDist);
////				source.teleport.setDistanceToFloor(targetFloor, minDist);
////			}
////
////		}
//
//	}
//
//	private static class TeleportNode implements Comparable<TeleportNode> {
//
//		private final SimulationEntity teleport;
//		private final ArrayList<TeleportVertex> neighbours;
//		private int distance;
//
//		public TeleportNode(SimulationEntity teleport)
//		{
//			this.teleport = teleport;
//			this.neighbours = new ArrayList<>();
//		}
//
//		@Override
//		public int compareTo(TeleportNode o)
//		{
//			return Integer.compare(this.distance, o.distance);
//		}
//
//		public SimulationEntity getTeleport()
//		{
//			return teleport;
//		}
//
//		public void addNeighbour(TeleportNode n, int d)
//		{
//
//			this.neighbours.add(new TeleportVertex(n, d));
//		}
//	}
//
//	private static class TeleportVertex {
//
//		TeleportNode n;
//		int distance;
//
//		public TeleportVertex(TeleportNode n, int distance)
//		{
//			this.n = n;
//			this.distance = distance;
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.ITeleport#toString()
//	 */
//	@Override
//	public String toString()
//	{
//		StringBuilder builder = new StringBuilder();
//		builder.append("Teleport [type=");
//		builder.append(type == START ? "START" : "END");
//		builder.append(", name=");
//		builder.append(name);
//		builder.append(", location=");
//		builder.append(this.getLocation());
//		builder.append("]");
//		return builder.toString();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.ITeleport#getState()
//	 */
//	@Override
//	public JsonState<Building> getState()
//	{
//		throw new UnsupportedOperationException("Not implemented yet");
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.ITeleport#
//	 * canExecuteWayPointAction(com.massisframework.massis.model.managers.
//	 * pathfinding.PathFollower)
//	 */
//	@Override
//	public boolean canExecuteWayPointAction(PathFollower pf)
//	{
//		return this.getLocation().isInSameFloor(pf.getLocation())
//				&& KPolygonUtils.intersects(pf, this);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.massisframework.massis.model.building.ITeleport#executeWayPointAction
//	 * (com.massisframework.massis.model.managers.pathfinding.PathFollower)
//	 */
//	@Override
//	public boolean executeWayPointAction(PathFollower vehicle)
//	{
//		/*
//		 * Is in this teleport?
//		 */
//
//		vehicle.getVelocity().mult(0);
//		// final Teleport connectedTeleport = path.getTargetTeleport()
//		// .getConnection();
//		final SimulationEntity connectedTeleport = this.getConnection();
//
//		Logger.getLogger(TeleportImpl.class.getName()).log(Level.INFO,
//				"Moving vehicle to connected teleport: {0}.",
//				connectedTeleport);
//		/*
//		 * Proper teleporting
//		 */
//		Coordinate2DComponent coord = connectedTeleport
//				.get(Coordinate2DComponent.class);
//		FloorContainmentComponent fc = connectedTeleport
//				.get(FloorContainmentComponent.class);
//
//		vehicle.moveTo(coord.getX(), coord.getY(), fc.getFloor());
//		/*
//		 * Remove from cache, everything has changed
//		 */
//		this.getPathManager().removeFromCache(vehicle);
//		/*
//		 * Location has changed
//		 */
//		return true;
//
//	}
//
//}
