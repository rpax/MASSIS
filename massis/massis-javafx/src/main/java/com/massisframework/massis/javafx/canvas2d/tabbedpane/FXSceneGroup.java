package com.massisframework.massis.javafx.canvas2d.tabbedpane;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FXSceneGroup {

	private final StringProperty layerName;
	private SimpleBooleanProperty enabled;

	public FXSceneGroup(String layerName, boolean enabled)
	{
		this.layerName = new SimpleStringProperty(layerName);
		this.enabled = new SimpleBooleanProperty(enabled);
	}

	public StringProperty layerNameProperty()
	{
		return layerName;
	}

	public SimpleBooleanProperty enabledProperty()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled.set(enabled);
	}
}
