package com.massisframework.massis.ecs.components.g2d;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class LayerComponent extends Component {

	@EntityId
	int layer;
}
