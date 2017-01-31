package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.NameComponent;

public class NameComponentImpl implements NameComponent {

	private String name;

	@Override
	public String get()
	{
		return this.name;
	}

	@Override
	public void set(String v)
	{
		this.name = v;

	}

}
