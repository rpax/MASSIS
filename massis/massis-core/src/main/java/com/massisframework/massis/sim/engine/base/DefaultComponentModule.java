package com.massisframework.massis.sim.engine.base;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.NameComponent;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.components.building.DoorComponent;
import com.massisframework.massis.model.components.building.HeadingComponent;
import com.massisframework.massis.model.components.building.MetadataComponent;
import com.massisframework.massis.model.components.building.MovementCapabilities;
import com.massisframework.massis.model.components.building.NameComponentImpl;
import com.massisframework.massis.model.components.building.ObstacleComponent;
import com.massisframework.massis.model.components.building.PhysicsComponent;
import com.massisframework.massis.model.components.building.WallComponent;
import com.massisframework.massis.model.components.building.impl.DoorComponentImpl;
import com.massisframework.massis.model.components.building.impl.FloorImpl;
import com.massisframework.massis.model.components.building.impl.HashMetadataComponent;
import com.massisframework.massis.model.components.building.impl.HeadingImpl;
import com.massisframework.massis.model.components.building.impl.MovementCapabilititesImpl;
import com.massisframework.massis.model.components.building.impl.ObstacleComponentImpl;
import com.massisframework.massis.model.components.building.impl.RoomComponentImpl;
import com.massisframework.massis.model.components.building.impl.SimplePhysicsComponent;
import com.massisframework.massis.model.components.building.impl.WallComponentImpl;
import com.massisframework.massis.model.location.LocationImpl;

public class DefaultComponentModule implements Module {

	@Override
	public void configure(Binder binder)
	{
		binder.bind(PhysicsComponent.class).to(SimplePhysicsComponent.class);
		binder.bind(MovementCapabilities.class).to(MovementCapabilititesImpl.class);
		binder.bind(Location.class).to(LocationImpl.class);
		binder.bind(WallComponent.class).to(WallComponentImpl.class);
		binder.bind(NameComponent.class).to(NameComponentImpl.class);
//		binder.bind(TeleportComponent.class).to(TeleportComponentImpl.class);
		binder.bind(Floor.class).to(FloorImpl.class);
		binder.bind(MetadataComponent.class).to(HashMetadataComponent.class);
		binder.bind(DoorComponent.class).to(DoorComponentImpl.class);
		binder.bind(RoomComponent.class).to(RoomComponentImpl.class);
		binder.bind(ObstacleComponent.class).to(ObstacleComponentImpl.class);
		binder.bind(HeadingComponent.class).to(HeadingImpl.class);
	}

}
