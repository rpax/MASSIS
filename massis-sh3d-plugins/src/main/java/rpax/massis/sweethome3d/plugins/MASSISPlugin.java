package rpax.massis.sweethome3d.plugins;

import com.eteks.sweethome3d.plugin.PluginAction;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MASSISPlugin extends com.eteks.sweethome3d.plugin.Plugin {

    private static final Class[] actionClasses;
    private PluginAction[] actions;

    static
    {
        ResourceBundle bundle = ResourceBundle.getBundle(
                MASSISPlugin.class.getName());
        String[] classNames = bundle.getString("MASSISPlugin.actions").split(
                "\\s*(,|\\s)\\s*");
        actionClasses = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++)
        {
            try
            {
                actionClasses[i] = Class.forName(classNames[i]);
            } catch (ClassNotFoundException ex)
            {
                Logger.getLogger(MASSISPlugin.class.getName()).log(Level.SEVERE,
                        null, ex);
                System.exit(1);
            }

        }

    }

    public MASSISPlugin()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.eteks.sweethome3d.plugin.Plugin#getActions()
     */
    @Override
    public PluginAction[] getActions()
    {
        if (actions == null)
        {
            actions = new PluginAction[actionClasses.length];
            for (int i = 0; i < actionClasses.length; i++)
            {
                try
                {
                    actions[i] = (PluginAction) actionClasses[i].getConstructor(
                            MASSISPlugin.class)
                            .newInstance(this);
                } catch (NoSuchMethodException |
                        SecurityException |
                        InstantiationException |
                        IllegalAccessException |
                        IllegalArgumentException |
                        InvocationTargetException ex)
                {
                    Logger.getLogger(MASSISPlugin.class.getName()).log(
                            Level.SEVERE,
                            null, ex);
                }

            }
//            return new PluginAction[]
//            {
//                new MetadataPluginAction(this),
//                new BuildingLoadAction(this),
//                new BuildingSaveAction(this),
//                new RoomNameGenerationAction(this),
//                new DoorNameGenerationAction(this),
//                new DesignerToolsAction(this)
//            }

        }
        return actions;
    }
}
