package rpax.massis.sweethome3d.plugins.design;

import javax.swing.JOptionPane;

import rpax.massis.sweethome3d.plugins.MASSISPluginAction;

import com.eteks.sweethome3d.model.DoorOrWindow;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import rpax.massis.sweethome3d.plugins.MASSISPlugin;

public class DoorNameGenerationAction extends MASSISPluginAction {

    public DoorNameGenerationAction(MASSISPlugin plugin)
    {
        super(plugin);


        putPropertyValue(Property.MENU, "Name Generation");
        putPropertyValue(Property.NAME, "Generate Door/Window names");
        setEnabled(true);
    }

    @Override
    public void execute()
    {
        String prefix = JOptionPane.showInputDialog("Door/Window Prefix:");
        prefix = prefix == null ? "" : prefix;
        int i = 1;
        for (HomePieceOfFurniture hpof : this.plugin.getHome()
                .getFurniture())
        {
            if (hpof instanceof DoorOrWindow)
            {
                hpof.setName(prefix + "" + i);
            }
            i++;
        }

    }
}
