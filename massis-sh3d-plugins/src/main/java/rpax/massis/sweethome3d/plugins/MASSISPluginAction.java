package rpax.massis.sweethome3d.plugins;

import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;

public abstract class MASSISPluginAction extends PluginAction {

    protected final Plugin plugin;

    public MASSISPluginAction(Plugin plugin)
    {
        super();
        this.plugin = plugin;
    }
}
