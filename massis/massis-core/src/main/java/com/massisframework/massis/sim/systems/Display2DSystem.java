package com.massisframework.massis.sim.systems;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.displays.buildingmap.BuildingMap;
import com.massisframework.massis.displays.floormap.layers.ConnectionsLayer;
import com.massisframework.massis.displays.floormap.layers.CrowdDensityLayer;
import com.massisframework.massis.displays.floormap.layers.DoorLayer;
import com.massisframework.massis.displays.floormap.layers.DrawableFloor;
import com.massisframework.massis.displays.floormap.layers.PathFinderLayer;
import com.massisframework.massis.displays.floormap.layers.PathLayer;
import com.massisframework.massis.displays.floormap.layers.PeopleIDLayer;
import com.massisframework.massis.displays.floormap.layers.PeopleLayer;
import com.massisframework.massis.displays.floormap.layers.QTLayer;
import com.massisframework.massis.displays.floormap.layers.RadioLayer;
import com.massisframework.massis.displays.floormap.layers.RoomsLabelLayer;
import com.massisframework.massis.displays.floormap.layers.RoomsLayer;
import com.massisframework.massis.displays.floormap.layers.VisibleAgentsLines;
import com.massisframework.massis.displays.floormap.layers.VisionRadioLayer;
import com.massisframework.massis.displays.floormap.layers.WallLayer;
import com.massisframework.massis.sim.engine.SimulationEngine;
import com.massisframework.massis.sim.engine.SimulationSystem;

public class Display2DSystem implements SimulationSystem {

	private SimulationEngine engine;
	private DrawableLayer<DrawableFloor>[] layers;
	private BuildingMap buildingMap;
	private int updateCount;

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(SimulationEngine simEngine)
	{
		this.engine = simEngine;
		this.layers = new DrawableLayer[] {
				new RoomsLayer(false),
				new RoomsLabelLayer(false),
				new VisionRadioLayer(false),
				new CrowdDensityLayer(true),
				new WallLayer(true),
				new DoorLayer(true),
				new ConnectionsLayer(false),
				new PathLayer(false),
				new PeopleLayer(true),
				new RadioLayer(false),
				new PathFinderLayer(false),
				new PeopleIDLayer(false),
				new VisibleAgentsLines(false),
				new QTLayer(false)
		};
		this.updateCount=0;
	}

	@Override
	public void update(float deltaTime)
	{
		this.updateCount++;
		if (updateCount==100){
			this.buildingMap = new BuildingMap(engine, this.layers);
			buildingMap.setVisible(true);
		}
	}

	@Override
	public void removedFromEngine(SimulationEngine simEngine)
	{

	}

}
