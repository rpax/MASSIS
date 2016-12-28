package com.massisframework.massis.ecs.components;

import com.artemis.Component;

public class MovableInfo extends Component {

	/**
	 * @treatAsPrivate
	 */
	public boolean movable;

	public boolean isMovable()
	{
		return movable;
	}
}
