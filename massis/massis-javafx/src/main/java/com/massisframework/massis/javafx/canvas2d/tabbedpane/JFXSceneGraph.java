package com.massisframework.massis.javafx.canvas2d.tabbedpane;

import javafx.scene.Node;

public interface JFXSceneGraph {

	public static String GROUP_KEY = "GROUP";

	public static String getGroup(Node node)
	{
		return String.valueOf(node.getProperties().get(GROUP_KEY));
	}

	public static void setGroup(Node node, String group)
	{
		node.getProperties().put(GROUP_KEY, group);
	}

	public void addChild(Node node);

	public void removeChild(Node node);
}
