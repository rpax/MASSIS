package com.massisframework.massis.ecs.components.g2d.shape;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class LayerAttachment extends Component {
	@EntityId
	int layer = -1;

	public int getLayer()
	{
		return layer;
	}

	public void setLayer(int layer)
	{
		this.layer = layer;
	}
}
