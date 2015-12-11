package com.massisframework.testdata;

import com.eteks.sweethome3d.plugin.PluginAction;


/**
 * Shows & loads test data homes
 * 
 * @author rpax
 *
 */
public class LoadSampleHomeAction extends PluginAction {

	public LoadSampleHomeAction(TestDataPlugin plugin)
	{
		super(LoadSampleHomeAction.class.getPackage().getName()+".ApplicationPlugin",
				LoadSampleHomeAction.class.getName(),
                plugin.getPluginClassLoader(), true);
		//Sections: Help->Sample Homes
		putPropertyValue(Property.MENU, "Help");
        putPropertyValue(Property.NAME, "Load Sample Home...");
	}
	
	@Override
	public void execute()
	{
		//1. Load sample homes names
		
		//2. show in some kind of GUI.
		//3. load home
	}

}
