package com.massisframework.massis.ecs.components;

public class NameComponent extends ModifiableComponent {

	private String name="";

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
		this.fireChanged();
	}
}
