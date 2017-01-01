package com.massisframework.massis.ecs.components;

import com.artemis.Component;

public class AIComponent extends Component {
	/**
	 * @treatAsPrivate
	 */
	public AIExecutor executor;

	public AIExecutor getExecutor()
	{
		return executor;
	}

	public void setExecutor(AIExecutor executor)
	{
		this.executor = executor;
	}

}
