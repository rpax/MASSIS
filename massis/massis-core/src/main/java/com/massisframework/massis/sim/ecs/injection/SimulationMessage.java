package com.massisframework.massis.sim.ecs.injection;

public class SimulationMessage {

	private Object content;

	public SimulationMessage(Object content)
	{
		super();
		this.content = content;
	}

	public Object getContent()
	{
		return content;
	}

	public void setContent(Object content)
	{
		this.content = content;
	}
}
