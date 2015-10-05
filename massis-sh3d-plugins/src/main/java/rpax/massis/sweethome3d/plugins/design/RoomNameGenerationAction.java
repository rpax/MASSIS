package rpax.massis.sweethome3d.plugins.design;

import javax.swing.JOptionPane;

import rpax.massis.sweethome3d.plugins.MASSISPluginAction;

import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.plugin.Plugin;

public class RoomNameGenerationAction extends MASSISPluginAction {

    public RoomNameGenerationAction(Plugin plugin)
    {
        super(plugin);
        putPropertyValue(Property.MENU, "Name Generation");
        putPropertyValue(Property.NAME, "Generate Room names");
        setEnabled(true);
    }

    @Override
    public void execute()
    {
        String prefix = JOptionPane.showInputDialog("Room Prefix:");
        prefix = prefix == null ? "" : prefix;
        int i = 1;
        for (Room room : this.plugin.getHome().getRooms())
        {
            room.setName(prefix + "" + i);
            i++;
        }

    }
}
