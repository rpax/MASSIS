package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.components.NameComponent;

public class NameComponentImpl implements NameComponent {

	private String name;

	public NameComponentImpl(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

}
