/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.sweethome3d.plugins.building;

import rpax.massis.sweethome3d.plugins.metadata.*;
import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;

/**
 *
 * @author Rafael Pax
 */
public class BuildingLoaderPlugin extends Plugin {

    @Override
    public PluginAction[] getActions()
    {
        return new PluginAction[]
        {
            new BuildingLoadAction(this),
            new BuildingSaveAction(this)
        };
    }
}
