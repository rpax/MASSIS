package rpax.massis.sim;

import rpax.massis.model.building.Building;
import rpax.massis.model.building.Building.BuildingProgressMonitor;
import sim.engine.MakesSimState;
import sim.engine.SimState;

import com.eteks.sweethome3d.model.RecorderException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpax.massis.util.building.BuildingData;
import rpax.massis.util.io.storage.DefaultMassisStorage;
import rpax.massis.util.io.storage.MassisStorage;

public abstract class AbstractSimulation extends SimState {

    private boolean finishCalled = false;
    protected final String resourcesPath;
    protected BuildingProgressMonitor buildingProgress;
    protected MassisStorage storage;
    protected String logFileLocation;
    protected Building building;

    public AbstractSimulation(long seed, String buildingFilePath,
            String resourcesPath, String logFileLocation,
            BuildingProgressMonitor buildingProgress)
    {
        super(seed);
        try
        {
            this.storage=DefaultMassisStorage.getStorage(new File(buildingFilePath));
        } catch (IOException ex)
        {
            Logger.getLogger(AbstractSimulation.class.getName()).log(Level.SEVERE,
                    null, ex);
            System.exit(1);
        }
        this.resourcesPath = resourcesPath;
        this.logFileLocation = logFileLocation;
        this.buildingProgress = buildingProgress;
    }

    public AbstractSimulation(long seed, String buildingFilePath,
            String resourcesPath, String logFileLocation)
    {
        this(seed, buildingFilePath, resourcesPath, logFileLocation, null);
    }
    private static final long serialVersionUID = 575438688820685250L;

    public static void runSimulation(
            final Class<? extends AbstractSimulation> c, String[] args)
    {

        if (!keyExists("-building", args))
        {
            System.err
                    .println(
                    "Building filepath argument not provided. Exiting now");
            System.exit(-1);
        }

        final String buildingFilePath = argumentForKey("-building", args);
        final String saveLocation = argumentForKey("-logfile", args);
        final String resourcesPath = argumentForKey("-resources", args);
        doLoop(new MakesSimState() {
            @Override
            public SimState newInstance(long seed, String[] args)
            {
                try
                {
                    return (c.getDeclaredConstructor(
                            /**
                             * Seed
                             */
                            Long.TYPE,
                            /**
                             * buildingFilePath
                             */
                            String.class,
                            /**
                             * resourcesPath
                             */
                            String.class,
                            /**
                             * saveLocation
                             */
                            String.class).newInstance(seed, buildingFilePath,
                            resourcesPath, saveLocation));
                } catch (Exception e)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(
                            "Exception occurred while trying to construct the simulation ");
                    sb.append(c);
                    sb.append("\n");
                    sb.append("Available constructors: \n");
                    for (Constructor constructor : c.getDeclaredConstructors())
                    {
                        sb.append(Arrays.toString(
                                constructor.getParameterTypes()));
                        sb.append("\n");
                    }

                    throw new RuntimeException(sb.toString(), e);


                }
            }

            @Override
            @SuppressWarnings("rawtypes")
            public Class simulationClass()
            {
                return c;
            }
        }, args);

        System.exit(0);
    }

    @Override
    public void start()
    {
        try
        {
            this.building = this.createBuilding();
        } catch (RecorderException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.start();

    }

    protected Building createBuilding() throws RecorderException
    {
        try
        {
            BuildingData bd = new BuildingData(
                    storage.loadHome(),
                    storage.loadMetadata());
            if (buildingProgress != null)
            {
                return new Building(bd, this.resourcesPath,
                        this.buildingProgress);
            } else
            {
                return new Building(bd, this.resourcesPath);
            }
        } catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(AbstractSimulation.class.getName()).log(
                    Level.SEVERE,
                    null, ex);
            throw new RuntimeException(ex);
        }
    }

    static boolean keyExists(String key, String[] args)
    {
        for (int x = 0; x < args.length; x++)
        {
            if (args[x].equalsIgnoreCase(key))
            {
                return true;
            }
        }
        return false;
    }

    static String argumentForKey(String key, String[] args)
    {
        for (int x = 0; x < args.length - 1; x++)
        // if a key has an argument, it can't be the last string
        {
            if (args[x].equalsIgnoreCase(key))
            {
                return args[x + 1];
            }
        }
        return null;
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.finish();
    }

    @Override
    public void finish()
    {
        if (!this.finishCalled)
        {
            this.finishCalled = true;
            this.endSimulation();

        }
    }

    protected abstract void endSimulation();
}
