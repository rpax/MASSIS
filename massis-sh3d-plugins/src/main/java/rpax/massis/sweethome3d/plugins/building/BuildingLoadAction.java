/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.sweethome3d.plugins.building;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.plugin.PluginAction;
import com.eteks.sweethome3d.viewcontroller.HomeController;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import rpax.massis.sweethome3d.plugins.MASSISPlugin;
import rpax.massis.sweethome3d.plugins.MASSISPluginAction;
import rpax.massis.util.building.BuildingData;
import rpax.massis.util.building.MASSISHomeMetadataManager;
import rpax.massis.util.io.storage.DefaultMassisStorage;
import rpax.massis.util.io.storage.MassisStorage;

/**
 *
 * @author Rafael Pax
 */
public class BuildingLoadAction extends MASSISPluginAction {

    public BuildingLoadAction(MASSISPlugin plugin)
    {
        super(plugin);
        putPropertyValue(Property.MENU, "File");
        putPropertyValue(Property.NAME, "Load MASSIS building");
        this.setEnabled(true);
    }

    @Override
    public void execute()
    {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {


            try
            {
                MassisStorage storage = DefaultMassisStorage.getStorage(
                       fc.getSelectedFile());
                //Extract building data
                BuildingData bd = new BuildingData(storage.loadHome(),
                        storage.loadMetadata());
                Home home = bd.getHome();
                //force metadata creation
                MASSISHomeMetadataManager.getHomeMetaData(home);

                Method method = HomeController.class.getDeclaredMethod(
                        "addHomeToApplication",
                        Home.class);
                method.setAccessible(true);
                method.invoke(this.plugin.getHomeController(), home);
            } catch (IOException |
                    NoSuchMethodException |
                    SecurityException |
                    IllegalArgumentException |
                    InvocationTargetException |
                    IllegalAccessException |
                    ClassNotFoundException ex)
            {
                Logger.getLogger(BuildingLoadAction.class.getName()).log(
                        Level.SEVERE,
                        null, ex);
            }



        } else
        {
            System.out.println("Open command cancelled by user.");
        }

    }
}
