package rpax.massis.sh3d.plugins;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.plugin.PluginAction;

public abstract class MASSISPluginAction extends PluginAction {
	protected Home home;

	public MASSISPluginAction(Home home,
			Class<? extends MASSISPluginAction> actionClass) {
		super("rpax.massis.sh3d.plugins.ApplicationPlugin", actionClass
				.getName(), actionClass.getClassLoader());
		
		this.home=home;
	}

}
