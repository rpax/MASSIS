package rpax.massis.sweethome3d.plugins.design;

import javax.swing.JFrame;
import rpax.massis.sweethome3d.plugins.MASSISPlugin;

import rpax.massis.sweethome3d.plugins.MASSISPluginAction;

public class DesignerToolsAction extends MASSISPluginAction {

    private final DesignerToolsFrame frame;

    public DesignerToolsAction(MASSISPlugin plugin)
    {
        super(plugin);
        putPropertyValue(Property.MENU, "Tools");
        putPropertyValue(Property.NAME, "Designer Tools");
        //MASSISHomeMetadataManager.getHomeMetaData(this.home).init();
        this.frame = new DesignerToolsFrame(this.plugin.getHome());
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setEnabled(true);

    }

    @Override
    public void execute()
    {
        System.gc();

        this.frame.setVisible(true);
    }
}