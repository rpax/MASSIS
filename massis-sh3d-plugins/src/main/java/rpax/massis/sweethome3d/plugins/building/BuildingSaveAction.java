/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.sweethome3d.plugins.building;

import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.plugin.PluginAction;
import com.eteks.sweethome3d.viewcontroller.HomeView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import rpax.massis.sweethome3d.plugins.MASSISPlugin;
import rpax.massis.sweethome3d.plugins.MASSISPluginAction;
import rpax.massis.util.building.BuildingData;
import rpax.massis.util.io.storage.DefaultMassisStorage;
import rpax.massis.util.io.storage.MassisStorage;

/**
 *
 * @author Rafael Pax
 */
public class BuildingSaveAction extends MASSISPluginAction {

    public BuildingSaveAction(MASSISPlugin plugin)
    {
        super(plugin);
        putPropertyValue(Property.MENU, "File");
        putPropertyValue(Property.NAME, "Save As MASSIS building");



        /*
         * Disable normal load/save actions
         */
        this.plugin
                .getHomeController()
                .getView()
                .setEnabled(HomeView.ActionType.SAVE, false);
        this.plugin.getHomeController().getView().setEnabled(
                HomeView.ActionType.SAVE_AS, false);
        /*
         * Add window closing listener
         */
        JFrame homeFrame = getHomeFrame();
        for (WindowListener wl : homeFrame.getWindowListeners())
        {

            if (com.eteks.sweethome3d.HomeFramePane.class.equals(
                    wl.getClass().getEnclosingClass()))
            {
                homeFrame.removeWindowListener(wl);
            }

        }

        homeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                onWindowClosing(e);
            }
        });

        this.setEnabled(true);


    }

    @Override
    public void execute()
    {

        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {

            File file = fc.getSelectedFile();
            try
            {
                MassisStorage storage = DefaultMassisStorage.getStorage(
                        file);
                storage.saveHome(this.plugin.getHome());
                storage.saveMetadata(BuildingData.getBuildingData(
                        this.plugin.getHome()).getMetadataManager().getBuildingMetadata());
                storage.deleteLogFiles();
            } catch (IOException ex)
            {
                Logger.getLogger(BuildingSaveAction.class.getName()).log(
                        Level.SEVERE,
                        null, ex);
            } catch (RecorderException ex)
            {
                Logger.getLogger(BuildingSaveAction.class.getName()).log(
                        Level.SEVERE,
                        null, ex);
            }
            this.plugin.getHome().setModified(false);
        }

    }

    private JFrame getHomeFrame()
    {
        return (JFrame) SwingUtilities.getRoot(
                (JComponent) this.plugin.getHomeController().getView());

    }

    private void onWindowClosing(WindowEvent e)
    {
        //1. Check if home has been modified
        if (this.plugin.getHome().isModified())
        {
            HomeView.SaveAnswer save = this.plugin.getHomeController().getView().confirmSave(
                    "Building");
            switch (save)
            {
                case SAVE:
                    execute();
                    this.plugin.getHomeController().close();
                    break;
                case CANCEL:
                    //nothing
                    break;
                case DO_NOT_SAVE:
                    this.plugin.getHomeController().close();
                    break;
                default:
                    throw new AssertionError(save.name());
            }
        } else
        {
            this.plugin.getHomeController().close();
        }
    }
}
