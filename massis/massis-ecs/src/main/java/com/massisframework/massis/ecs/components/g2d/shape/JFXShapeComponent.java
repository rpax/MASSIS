package com.massisframework.massis.ecs.components.g2d.shape;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.massisframework.massis.javafx.canvas2d.tabbedpane.JFXSceneGraph;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public class JFXShapeComponent extends Component {
	Shape node;
	@EntityId
	public int parent = -1;

	public Shape getShape()
	{
		return node;
	}

	public final void setStroke(Paint value)
	{
		node.setStroke(value);
	}

	public void setGroup(String groupName)
	{
		JFXSceneGraph.setGroup(node, groupName);
	}

	public String getGroup()
	{
		return JFXSceneGraph.getGroup(node);
	}

	public void setFill(Paint paint)
	{
		this.node.setFill(paint);
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
