package rpax.massis.sh3d.plugins;

import rpax.massis.sh3d.plugins.design.DoorNameGenerationAction;
import rpax.massis.sh3d.plugins.design.RoomNameGenerationAction;
import rpax.massis.sh3d.plugins.design.duplicator.DuplicatorAction;
import rpax.massis.sh3d.plugins.metadata.MetadataPluginAction;

public class MASSISPlugin extends com.eteks.sweethome3d.plugin.Plugin {

	public MASSISPlugin() {
		super();
	}
 
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eteks.sweethome3d.plugin.Plugin#getActions()
	 */
	@Override
	public com.eteks.sweethome3d.plugin.PluginAction[] getActions() {
		
		return new com.eteks.sweethome3d.plugin.PluginAction[] {
				new MetadataPluginAction(this.getHome()),
				new RoomNameGenerationAction(this.getHome()),
				new DoorNameGenerationAction(this.getHome()),
				new rpax.massis.sh3d.plugins.design.DesignerToolsAction(this.getHome()),
				new DuplicatorAction(this.getHome(),this.getHomeController())
				};
	}
	

}
