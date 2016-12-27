package com.massisframework.massis.ecs.components;

import com.artemis.Component;

public class ModifiableComponent extends Component {

	private boolean changed;

	public boolean isChanged()
	{
		return changed;
	}
	protected void fireChanged()
	{
		this.setChanged(true);
	}
	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}
}
