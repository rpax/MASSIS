package com.massisframework.massis.sim.engine.base;

import com.eteks.sweethome3d.model.Home;
import com.google.inject.Binder;
import com.google.inject.Module;

public class SweetHome3DModule implements Module {

	private Home home;
	public SweetHome3DModule(Home home)
	{
		this.home=home;
	}
	@Override
	public void configure(Binder binder)
	{
		binder.bind(Home.class).toInstance(home);
	}

}
