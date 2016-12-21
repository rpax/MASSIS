package com.massisframework.massis.model.building;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Selectable;
import com.massisframework.massis.displays.SimulationDisplay;
import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.io.Restorable;

public interface Building {

	/**
	 * Links a simulationObject with its corresponding sweethome3d furniture
	 * element
	 *
	 * @param simulationObject
	 *            the simulation object to be linked
	 * @param representation
	 *            the furniture element of sweethome3d that represents it
	 */
	void addSH3DRepresentation(ISimulationObject simulationObject,
			HomePieceOfFurniture representation);

	/**
	 * Adds a teleport element to the building
	 *
	 * @param teleport
	 *            the teleport element
	 */
	void addTeleport(Teleport teleport);

	HomePieceOfFurniture getSH3DRepresentation(Restorable obj);

	List<Floor> getFloors();

	Home getHome();

	HashMap<Level, Floor> getLevelsFloors();

	Indexable getFloorOf(Level lvl);

	Floor getFloorById(int floorId);

	/**
	 * Only used for recovering the state. <i>Do not use</i>
	 *
	 * @param simObjId
	 * @return
	 */
	ISimulationObject getSimulationObject(int simObjId);

	SimRoom getRandomRoom();

	AnimationManager getAnimationManager();

	EnvironmentManager getEnvironmentManager();

	Location getNamedLocation(String name);

	void addNamedLocation(String name, Location location);

	MovementManager getMovementManager();

	void addNamedRoom(String name, SimRoom simRoom);

	void registerDisplays(SimulationDisplay... displays);

	String getResourcesFolder();

	Map<String, String> getMetadata(Selectable f);

	Collection<HighLevelController> getScheduledControllers();

	PathFindingManager getPathManager();

	public void addToSchedule(HighLevelController hlc);
}