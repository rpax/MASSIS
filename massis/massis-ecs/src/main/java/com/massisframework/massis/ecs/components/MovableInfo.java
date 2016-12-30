package com.massisframework.massis.ecs.components;

import com.artemis.Component;

public class MovableInfo extends Component {

	public MovableInfo()
	{
	}

	public MovableInfo(boolean movable)
	{
		this.movable = movable;
	}

	/**
	 * @treatAsPrivate
	 */
	public boolean movable;

	public boolean isMovable()
	{
		return movable;
	}
}
