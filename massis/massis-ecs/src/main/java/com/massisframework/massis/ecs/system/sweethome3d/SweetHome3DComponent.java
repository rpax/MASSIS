package com.massisframework.massis.ecs.system.sweethome3d;

import com.eteks.sweethome3d.model.HomeObject;
import com.massisframework.massis.ecs.components.ModifiableComponent;

public class SweetHome3DComponent extends ModifiableComponent {

	private HomeObject ho;

	public SweetHome3DComponent()
	{

	}

	public HomeObject get()
	{
		return ho;
	}

	public SweetHome3DComponent set(HomeObject ho)
	{
		this.ho = ho;
		this.fireChanged();
		return this;
	}

}
