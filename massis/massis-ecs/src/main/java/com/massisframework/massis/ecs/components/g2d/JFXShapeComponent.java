package com.massisframework.massis.ecs.components.g2d;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

import javafx.scene.shape.Shape;

public class JFXShapeComponent extends Component {
	Shape node;
	@EntityId public int parent=-1;

	public Shape getShape()
	{
		return node;
	}

	public void setShape(Shape node)
	{
		this.node = node;
	}

	public int getParent()
	{
		return parent;
	}

	public void setParent(int parent)
	{
		this.parent = parent;
	}
}
