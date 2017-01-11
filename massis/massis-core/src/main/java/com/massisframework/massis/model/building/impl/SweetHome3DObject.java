package com.massisframework.massis.model.building.impl;

import com.eteks.sweethome3d.model.HomeObject;

public class SweetHome3DObject {

	private HomeObject homeObject;

	public HomeObject getHomeObject()
	{
		return homeObject;
	}

	public SweetHome3DObject(HomeObject homeObject)
	{
		this.homeObject = homeObject;
	}

}
