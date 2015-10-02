package rpax.massis.sweethome3d.plugins;

import com.eteks.sweethome3d.plugin.PluginAction;

public abstract class MASSISPluginAction extends PluginAction {

    protected final MASSISPlugin plugin;

    public MASSISPluginAction(MASSISPlugin plugin)
    {
        super();
        this.plugin = plugin;
    }
}
