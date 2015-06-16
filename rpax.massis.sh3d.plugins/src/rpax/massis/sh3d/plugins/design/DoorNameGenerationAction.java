package rpax.massis.sh3d.plugins.design;

import javax.swing.JOptionPane;

import rpax.massis.sh3d.plugins.MASSISPluginAction;

import com.eteks.sweethome3d.model.DoorOrWindow;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;

public class DoorNameGenerationAction extends MASSISPluginAction {
	

	public DoorNameGenerationAction(Home home) {
		super(home, DoorNameGenerationAction.class);
		 

		putPropertyValue(Property.MENU, "Name Generation");
		putPropertyValue(Property.NAME, "Generate Door/Window names");
		setEnabled(true);
	}

	@Override
	public void execute() {
		String prefix = JOptionPane.showInputDialog("Door/Window Prefix:");
		prefix = prefix == null ? "" : prefix;
		int i = 1;
		for (HomePieceOfFurniture hpof : home
				.getFurniture())
		{
			if (hpof instanceof DoorOrWindow)
				hpof.setName(prefix + "" + i);
			i++;
		}

	}
}
