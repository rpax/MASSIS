package com.massisframework.massis.model.systems.furniture;

import com.eteks.sweethome3d.model.HomeDoorOrWindow;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.massisframework.massis.model.components.Metadata;
import com.massisframework.massis.model.components.impl.DoorComponentImpl;
import com.massisframework.massis.model.components.impl.WindowComponentImpl;
import com.massisframework.massis.model.systems.sh3d.SweetHome3DFurniture;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.util.SimObjectProperty;

public class FurnitureSystem implements SimulationSystem {

	private SimulationEntityData ed;
	private SimulationEntitySet furniture;
	private Injector injector;

	@Inject
	public FurnitureSystem(SimulationEntityData ed, Injector injector)
	{
		this.injector = injector;
		this.ed = ed;
	}

	@Override
	public void initialize()
	{
		this.furniture = ed.createEntitySet(SweetHome3DFurniture.class);
		this.furniture.applyChanges();
		// Iterate over walls, windows...etc
		for (SimulationEntity e : furniture)
		{
			this.addFurniture(e);
		}
	}

	private void addFurniture(SimulationEntity e)
	{
		HomePieceOfFurniture f = e.get(SweetHome3DFurniture.class)
				.getFurniture();
		if (f instanceof HomeDoorOrWindow)
		{
			if (isWindow(f))
			{
				e.add(new WindowComponentImpl());
			} else
			{
				e.add(new DoorComponentImpl());
			}
		} else
		{
			AgentComponentImpl agent = new AgentComponentImpl();
			String className = e.get(Metadata.class)
					.get(SimObjectProperty.CLASSNAME.toString());
			if (className != null)
			{
				HighLevelAgent hl;
				try
				{
					hl = (HighLevelAgent) injector
							.getInstance(Class.forName(className));
					agent.setHighLevelAgent(hl);
				} catch (ClassNotFoundException e1)
				{
					// TODO catch properly
					e1.printStackTrace();
				}

			}
		}

	}

	private boolean isWindow(HomePieceOfFurniture f)
	{
		return f.getName() != null
				&& f.getName().toUpperCase().contains(
						SimObjectProperty.WINDOW.toString());
	}

	@Override
	public void update(float deltaTime)
	{
		if (this.furniture.applyChanges())
		{
			this.furniture.getAddedEntities().forEach(this::addFurniture);
		}
	}

}
