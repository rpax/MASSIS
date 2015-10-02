package rpax.massis.sweethome3d.plugins;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.SwingPropertyChangeSupport;


import com.eteks.sweethome3d.HomeFrameController;
import com.eteks.sweethome3d.SweetHome3D;
import com.eteks.sweethome3d.model.CollectionEvent;
import static com.eteks.sweethome3d.model.CollectionEvent.Type.ADD;
import static com.eteks.sweethome3d.model.CollectionEvent.Type.DELETE;
import com.eteks.sweethome3d.model.CollectionListener;
import com.eteks.sweethome3d.model.Content;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;
import com.eteks.sweethome3d.swing.HomePane;
import com.eteks.sweethome3d.swing.IconManager;
import com.eteks.sweethome3d.swing.ResourceAction;
import com.eteks.sweethome3d.swing.SwingTools;
import com.eteks.sweethome3d.viewcontroller.HomeController;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

public class PluginLoader {

    @SafeVarargs
    public static void runSweetHome3DWithPlugins(
            final Class<? extends Plugin>... pluginClasses)
    {
        runSweetHome3DWithPlugins(new String[]
        {
        }, pluginClasses);
    }

    @SafeVarargs
    public static void runSweetHome3DWithPlugins(String[] args,
            final Class<? extends Plugin>... pluginClasses)
    {
        try
        {

            Constructor<SweetHome3D> constructor = SweetHome3D.class
                    .getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            final SweetHome3D sh3d = constructor.newInstance(new Object[0]);
            Method method = SweetHome3D.class.getDeclaredMethod("init",
                    String[].class);
            method.setAccessible(true);
            method.invoke(sh3d, (Object) args);

            // final HashMap<Home, ExternalHomeManager> mngrs = new HashMap<>();
            sh3d.addHomesListener(new CollectionListener<Home>() {
                @Override
                public void collectionChanged(final CollectionEvent<Home> ev)
                {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run()
                        {
                            final Home home = ev.getItem();

                            switch (ev.getType())
                            {
                                case ADD:
                                    // mngrs.put(home, new ExternalHomeManager(home));
                                    for (Class<? extends Plugin> pluginClass : pluginClasses)
                                    {
                                        try
                                        {
                                            HomeFrameController homeFrameController =
                                                    getHomeFrameController(sh3d,
                                                    home);

                                            final Plugin plugin = pluginClass.newInstance();
                                            Method method = Plugin.class.getDeclaredMethod(
                                                    "setHome", Home.class);
                                            method.setAccessible(true);
                                            method.invoke(plugin, home);

                                            method = Plugin.class.getDeclaredMethod(
                                                    "setHomeController",
                                                    HomeController.class);
                                            method.setAccessible(true);
                                            method.invoke(plugin,
                                                    homeFrameController.getHomeController());

                                            insertPlugin(sh3d, home, plugin);


                                        } catch (Exception ex)
                                        {
                                            // TODO Auto-generated catch block
                                            ex.printStackTrace();
                                        }
                                    }

                                    break;
                                case DELETE:
//
                                    break;
                                default:
                                    break;

                            }
                        }
                    });
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static HomeFrameController getHomeFrameController(SweetHome3D sh3d,
            Home home)
    {
        try
        {
            Map<Home, HomeFrameController> homeFrameControllers = getFieldValue(
                    sh3d, "homeFrameControllers");
            HomeFrameController controller = homeFrameControllers.get(home);
            return controller;
        } catch (NoSuchFieldException ex)
        {
            Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (SecurityException ex)
        {
            Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    private static void insertPlugin(SweetHome3D sh3d, Home home, Plugin plugin)
    {


        HomeFrameController controller = getHomeFrameController(sh3d, home);
        final JRootPane rootPane = (javax.swing.JRootPane) controller
                .getView();


        final JMenuBar menuBar = rootPane.getJMenuBar();


        for (final PluginAction pa : plugin.getActions())
        {
            Action pluginAction = new ActionAdapter(pa,
                    (HomePane) controller.getHomeController().getView());


            String pluginMenu = (String) pluginAction.getValue(
                    PluginAction.Property.MENU.name());

            if (pluginMenu != null)
            {
                boolean pluginActionAdded = false;
                for (int i = 0; i < menuBar.getMenuCount() && !pluginActionAdded; i++)
                {
                    JMenu menu = menuBar.getMenu(i);
                    if (menu.getText().equals(pluginMenu))
                    {
                        // Add menu item to existing menu
                        menu.addSeparator();
                        menu.add(new ResourceAction.MenuItemAction(
                                pluginAction));
                        pluginActionAdded = true;

                    }
                }
                if (!pluginActionAdded)
                {
                    // Create missing menu before last menu
                    JMenu menu = new JMenu(pluginMenu);
                    menu.add(new ResourceAction.MenuItemAction(pluginAction));
                    menuBar.add(menu, menuBar.getMenuCount() - 1);
                }
            }

        }
       
    }

    @SuppressWarnings("unchecked")
    private static <T> T getFieldValue(Object obj, String fieldName)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException
    {
        Field f = obj.getClass().getDeclaredField(fieldName); // NoSuchFieldException
        f.setAccessible(true);
        T iWantThis = (T) f.get(obj); // IllegalAccessException
        return iWantThis;
    }

    /**
     * A Swing action adapter to a plug-in action.
     */
    private static class ActionAdapter implements Action {

        private PluginAction pluginAction;
        private SwingPropertyChangeSupport propertyChangeSupport;
        private HomePane homePane;

        public ActionAdapter(PluginAction pluginAction, final HomePane homePane)
        {
            this.homePane = homePane;
            this.pluginAction = pluginAction;
            this.propertyChangeSupport = new SwingPropertyChangeSupport(this);
            this.pluginAction
                    .addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent ev)
                {
                    String propertyName = ev.getPropertyName();
                    Object oldValue = ev.getOldValue();
                    Object newValue = ev.getNewValue();
                    if (PluginAction.Property.ENABLED.name().equals(
                            propertyName))
                    {
                        propertyChangeSupport
                                .firePropertyChange(new PropertyChangeEvent(
                                ev.getSource(), "enabled",
                                oldValue, newValue));
                    } else
                    {
                        // In case a property value changes, fire the
                        // new value decorated in
                        // subclasses
                        // unless new value is null (most Swing
                        // listeners don't check new
                        // value is null !)
                        if (newValue != null)
                        {
                            if (PluginAction.Property.NAME.name()
                                    .equals(propertyName))
                            {
                                propertyChangeSupport
                                        .firePropertyChange(
                                        new PropertyChangeEvent(
                                        ev.getSource(),
                                        Action.NAME, oldValue,
                                        newValue));
                            } else if (PluginAction.Property.SHORT_DESCRIPTION
                                    .name().equals(propertyName))
                            {
                                propertyChangeSupport
                                        .firePropertyChange(
                                        new PropertyChangeEvent(
                                        ev.getSource(),
                                        Action.SHORT_DESCRIPTION,
                                        oldValue, newValue));
                            } else if (PluginAction.Property.MNEMONIC
                                    .name().equals(propertyName))
                            {
                                propertyChangeSupport
                                        .firePropertyChange(
                                        new PropertyChangeEvent(
                                        ev.getSource(),
                                        Action.MNEMONIC_KEY,
                                        oldValue != null ? new Integer(
                                        (Character) oldValue)
                                        : null,
                                        newValue));
                            } else if (PluginAction.Property.SMALL_ICON
                                    .name().equals(propertyName))
                            {
                                propertyChangeSupport
                                        .firePropertyChange(
                                        new PropertyChangeEvent(
                                        ev.getSource(),
                                        Action.SMALL_ICON,
                                        oldValue != null ? IconManager
                                        .getInstance()
                                        .getIcon(
                                        (Content) oldValue,
                                        DEFAULT_SMALL_ICON_HEIGHT,
                                        homePane)
                                        : null,
                                        newValue));
                            } else
                            {
                                propertyChangeSupport
                                        .firePropertyChange(
                                        new PropertyChangeEvent(
                                        ev.getSource(),
                                        propertyName, oldValue,
                                        newValue));
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent ev)
        {
            this.pluginAction.execute();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener)
        {
            this.propertyChangeSupport.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener)
        {
            this.propertyChangeSupport.removePropertyChangeListener(listener);
        }
        private static final int DEFAULT_SMALL_ICON_HEIGHT = 16;

        @Override
        public Object getValue(String key)
        {
            if (NAME.equals(key))
            {
                return this.pluginAction
                        .getPropertyValue(PluginAction.Property.NAME);
            } else if (SHORT_DESCRIPTION.equals(key))
            {
                return this.pluginAction
                        .getPropertyValue(
                        PluginAction.Property.SHORT_DESCRIPTION);
            } else if (SMALL_ICON.equals(key))
            {
                Content smallIcon = (Content) this.pluginAction
                        .getPropertyValue(PluginAction.Property.SMALL_ICON);
                return smallIcon != null ? IconManager.getInstance().getIcon(
                        smallIcon, DEFAULT_SMALL_ICON_HEIGHT, homePane) : null;
            } else if (MNEMONIC_KEY.equals(key))
            {
                Character mnemonic = (Character) this.pluginAction
                        .getPropertyValue(PluginAction.Property.MNEMONIC);
                return mnemonic != null ? new Integer(mnemonic) : null;
            } else if (PluginAction.Property.TOOL_BAR.name().equals(key))
            {
                return this.pluginAction
                        .getPropertyValue(PluginAction.Property.TOOL_BAR);
            } else if (PluginAction.Property.MENU.name().equals(key))
            {
                return this.pluginAction
                        .getPropertyValue(PluginAction.Property.MENU);
            } else
            {
                return null;
            }
        }

        public void putValue(String key, Object value)
        {
            if (NAME.equals(key))
            {
                this.pluginAction.putPropertyValue(PluginAction.Property.NAME,
                        value);
            } else if (SHORT_DESCRIPTION.equals(key))
            {
                this.pluginAction.putPropertyValue(
                        PluginAction.Property.SHORT_DESCRIPTION, value);
            } else if (SMALL_ICON.equals(key))
            {
                // Ignore icon change
            } else if (MNEMONIC_KEY.equals(key))
            {
                this.pluginAction.putPropertyValue(
                        PluginAction.Property.MNEMONIC, new Character(
                        (char) ((Integer) value).intValue()));
            } else if (PluginAction.Property.TOOL_BAR.name().equals(key))
            {
                this.pluginAction.putPropertyValue(
                        PluginAction.Property.TOOL_BAR, value);
            } else if (PluginAction.Property.MENU.name().equals(key))
            {
                this.pluginAction.putPropertyValue(PluginAction.Property.MENU,
                        value);
            }
        }

        public boolean isEnabled()
        {
            return this.pluginAction.isEnabled();
        }

        public void setEnabled(boolean enabled)
        {
            this.pluginAction.setEnabled(enabled);
        }
    }
}
