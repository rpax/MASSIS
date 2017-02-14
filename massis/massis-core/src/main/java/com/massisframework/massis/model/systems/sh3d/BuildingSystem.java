package com.massisframework.massis.model.systems.sh3d;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.Level;
import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class BuildingSystem implements SimulationSystem {

	private Home home;

	@Inject
	public BuildingSystem(Home home)
	{
		this.home = home;
	}

	@Inject
	private SimulationEntityData ed;

	@Override
	public void initialize()
	{

		if (home.getLevels().isEmpty())
		{
			this.createLevel(null);
		}
		home.getLevels().forEach(this::createLevel);
	}

	private void createLevel(Level lvl)
	{
		SimulationEntity floorEntity = this.ed.createEntity();
		floorEntity.add(new SweetHome3DLevel()).setLevel(lvl);

	}
	

	@Override
	public void update(float deltaTime)
	{

	}

	public Home getHome()
	{
		return home;
	}

}
