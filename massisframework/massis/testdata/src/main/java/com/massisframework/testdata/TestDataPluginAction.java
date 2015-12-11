package com.massisframework.testdata;

import com.eteks.sweethome3d.plugin.PluginAction;


/**
 * Shows & loads test data homes
 * 
 * @author rpax
 *
 */
public class TestDataPluginAction extends PluginAction {

	public TestDataPluginAction(TestDataPlugin plugin)
	{
		super(TestDataPluginAction.class.getPackage().getName()+".ApplicationPlugin",
				TestDataPluginAction.class.getName(),
                plugin.getPluginClassLoader(), true);
		//Sections: Help->Sample Homes
		putPropertyValue(Property.MENU, "Help");
        putPropertyValue(Property.NAME, "Load Sample Home...");
	}
	@Override
	public void execute()
	{
		
	}

}
