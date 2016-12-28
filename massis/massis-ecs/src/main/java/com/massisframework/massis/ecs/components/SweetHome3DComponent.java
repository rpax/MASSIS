package com.massisframework.massis.ecs.components;

import com.artemis.Component;
import com.eteks.sweethome3d.model.HomeObject;

public class SweetHome3DComponent extends Component {

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
		return this;
	}

}
