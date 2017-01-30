package com.massisframework.massis.model.building;

import java.util.HashMap;
import java.util.List;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.Level;
import com.massisframework.massis.displays.SimulationDisplay;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface Building {

	List<SimulationEntity> getFloors();

	Home getHome();

	HashMap<Level, Floor> getLevelsFloors();

	Indexable getFloorOf(Level lvl);

	Floor getFloorById(int floorId);

	CoordinateHolder getRandomRoom();

	AnimationManager getAnimationManager();

	EnvironmentManager getEnvironmentManager();

	void registerDisplays(SimulationDisplay... displays);

	String getResourcesFolder();

}