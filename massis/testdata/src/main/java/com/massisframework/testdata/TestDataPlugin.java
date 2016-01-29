package com.massisframework.testdata;

import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;

public class TestDataPlugin extends Plugin {

	@Override
	public PluginAction[] getActions()
	{
		return new PluginAction[] { new LoadSampleHomeAction(this) };
	}

}
