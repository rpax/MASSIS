package rpax.massis.sh3d.plugins.design;

import javax.swing.JOptionPane;

import rpax.massis.sh3d.plugins.MASSISPluginAction;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.Room;

public class RoomNameGenerationAction extends MASSISPluginAction {
	
 
	public RoomNameGenerationAction(Home home) {
		super(home,RoomNameGenerationAction.class);
		putPropertyValue(Property.MENU, "Name Generation");
		putPropertyValue(Property.NAME, "Generate Room names");
		setEnabled(true);
	}

	@Override
	public void execute() {
		String prefix = JOptionPane.showInputDialog("Room Prefix:");
		prefix = prefix == null ? "" : prefix;
		int i=1;
		for (Room room : home.getRooms())
		{
			room.setName(prefix+""+i);
			i++;
		}

	}
}
