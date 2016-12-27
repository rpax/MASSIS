package com.massisframework.massis.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class ReferencedFloor extends Component {
	public @EntityId int floorEntityId;

	public int getFloorEntityId()
	{
		return floorEntityId;
	}

	public void setFloorEntityId(int floorEntityId)
	{
		this.floorEntityId = floorEntityId;
	}
}
