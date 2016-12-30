package com.massisframework.massis.ecs.components.g2d;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.utils.IntBag;

import javafx.scene.Node;

public class JFXNodeComponent extends Component {

	Node node;
	@EntityId
	IntBag children;

}
