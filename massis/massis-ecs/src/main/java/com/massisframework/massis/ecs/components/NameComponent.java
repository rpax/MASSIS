package com.massisframework.massis.ecs.components;

import com.artemis.Component;

public class NameComponent extends Component {

	private String name = "";

	public NameComponent()
	{

	}

	public String get()
	{
		return name;
	}

	public void set(String name)
	{
		this.name = name;
	}
}
