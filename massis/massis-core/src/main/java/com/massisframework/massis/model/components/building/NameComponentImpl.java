package com.massisframework.massis.model.components.building;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.NameComponent;

public class NameComponentImpl implements NameComponent {

	private String name;
	@Inject
	private NameComponentImpl()
	{
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
