package rpax.massis.sh3d.plugins.design;

import javax.swing.JFrame;

import rpax.massis.sh3d.plugins.MASSISPluginAction;
import rpax.massis.sh3d.plugins.metadata.MASSISHomeMetadataManager;

import com.eteks.sweethome3d.model.Home;

public class DesignerToolsAction extends MASSISPluginAction {
	
	private final DesignerToolsFrame frame;

	public DesignerToolsAction(Home home) {
		super(home, DesignerToolsAction.class);
		 

		putPropertyValue(Property.MENU, "Tools");
		putPropertyValue(Property.NAME, "Designer Tools");
		MASSISHomeMetadataManager.getHomeMetaData(this.home).init();
		this.frame = new DesignerToolsFrame(this.home);
		this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setEnabled(true);

	}

	@Override
	public void execute() {
		System.gc();
		
		this.frame.setVisible(true);
	}
}