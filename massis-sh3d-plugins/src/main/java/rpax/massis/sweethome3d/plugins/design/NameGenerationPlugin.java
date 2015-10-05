/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.sweethome3d.plugins.design;

import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;

/**
 *
 * @author Rafael Pax
 */
public class NameGenerationPlugin extends Plugin {

    @Override
    public PluginAction[] getActions()
    {
        return new PluginAction[]
        {
            new RoomNameGenerationAction(this),
            new DoorNameGenerationAction(this)
        };
    }
}
