package rpax.massis.sweethome3d.plugins.design;

import java.awt.Shape;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import rpax.massis.util.building.MASSISHomeMetadataManager;

import com.eteks.sweethome3d.model.Elevatable;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import rpax.massis.util.building.VisualOps;

public final class DesignTools {

    private static final int LEFT_SIDE = 0;
    private static final int RIGHT_SIDE = 1;
    /**
     * Valor arbitrario de pared invisible, lo suficientemente bajo como para
     * que no se vea y con una precision que haga que sea practicamente
     * imposible de reproducir "a mano".
     */
    private static final float INVISIBLE_WALL_HEIGHT = Float.parseFloat("0."
            + "Pilu".hashCode());

    public static void makeOuterWallsInvisible(Home home)
    {
        VisualOps.makeOuterWallsInvisible(home);
    }

    public static void makeOuterWallsInvisible(Home home, DesignerLog log)
    {
        log.appendLine("Starting...");
        VisualOps.makeOuterWallsInvisible(home);
        log.appendLine("Done");
    }

    public static void createIDs(Home home, DesignerLog log)
    {
        System.out.println("Nothing");
    }

    public static void showHomeIDData(Home home, DesignerLog log)
    {
        MASSISHomeMetadataManager manager = MASSISHomeMetadataManager
                .getHomeMetaData(home);
        log.clear();
        log.appendLine("SIZE: " + manager.size());
        log.appendLine("--------------------------");
        ArrayList<Selectable> ret = new ArrayList<Selectable>();
        ret.addAll(home.getWalls());
        ret.addAll(home.getFurniture());
        ret.addAll(home.getRooms());
        for (Selectable selectable : ret)
        {
            Map<String, String> metadata = manager.getMetadata(selectable);
            if (selectable instanceof HomePieceOfFurniture)
            {
                log.appendLine(selectable + ": " + metadata + " ("
                        + ((HomePieceOfFurniture) selectable).getName());
            } else
            {
                log.appendLine(selectable + ": " + metadata + "");
            }
        }

    }

    public static class LevelWallsMap {

        HashMap<Level, ArrayList<Wall>> map = new HashMap<Level, ArrayList<Wall>>();

        public void add(Wall wall)
        {
            Level lvl = wall.getLevel();
            ArrayList<Wall> wallsLvl = map.get(lvl);
            if (wallsLvl == null)
            {
                wallsLvl = new ArrayList<Wall>();
            }
            wallsLvl.add(wall);
            map.put(lvl, wallsLvl);
        }

        public ArrayList<Wall> getWalls(Level lvl)
        {
            ArrayList<Wall> walls = this.map.get(lvl);
            if (walls == null)
            {
                walls = new ArrayList<Wall>();
                this.map.put(lvl, walls);
            }
            return walls;
        }

        public Set<Level> keySet()
        {
            return this.map.keySet();
        }

        public Set<Entry<Level, ArrayList<Wall>>> entrySet()
        {
            return this.map.entrySet();
        }

        public Collection<ArrayList<Wall>> values()
        {
            return this.map.values();
        }
    }

    public static Shape getShape(Elevatable obj)
    {
        return callMethod(obj, "getShape", Shape.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> T callMethod(Object object, String methodName,
            Class<T> returnType)
    {
        Method method;

        try
        {
            method = object.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(object);
        } catch (NoSuchMethodException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
    private static DesignerLog emptyDesignerLog;

    public static DesignerLog emptyDesignerLog()
    {
        if (emptyDesignerLog == null)
        {
            emptyDesignerLog = new DesignerLog() {
                public DesignerLog clear()
                {
                    return emptyDesignerLog;
                }

                public DesignerLog appendLine(String str)
                {
                    return emptyDesignerLog;
                }

                public DesignerLog append(String str)
                {
                    return emptyDesignerLog;
                }
            };

        }
        return emptyDesignerLog;
    }
}
