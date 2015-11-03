package com.massisframework.massis.model.agents;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

/**
 * Represents the High Level behavior of an agent. These should be attached to
 * the simulation engine (in this case, MASON).
 *
 * @author Rafael Pax.
 *
 */
public abstract class HighLevelController implements Steppable, Stoppable {

    /**
     * The low level agent controlled by this controller
     */
    protected LowLevelAgent agent;
    /**
     * The metadata of this agent. The values contained in this map are the ones
     * introduced in the editor. (SweetHome3D). Can be null
     */
    protected Map<String, String> metadata;
    /**
     * MASSIS' resources folder. Any file paths referenced in the
     * {@link #metadata} values should be stored here.
     */
    protected final String resourcesFolder;

    /**
     * Constructs a new High Level Behavior of a {@link LowLevelAgent}.
     *
     * @param agent The low level agent controlled by this behavior
     * @param metadata the metadata attached to the agent (can be null)
     * @param resourcesFolder MASSIS' resources folder. Any file paths
     * referenced in the metadata values should be stored here.
     */
    public HighLevelController(LowLevelAgent agent, Map<String, String> metadata,
            String resourcesFolder)
    {
        this.agent = agent;
        this.metadata = metadata;
        this.resourcesFolder = resourcesFolder;
    }

    /**
     * Helper method for creating High Level Controllers. Normally, High Level
     * controllers classes are specified in the properties introduced in the
     * SweetHome3D editor.
     *
     * @param <T> The type of the desired high-Level controller
     * @param clazz The class of the controller
     * @param agent the agent to be attached
     * @param metadata metadata of the agent
     * @param resourcesFolder the resources folder used by this controller
     * @return A new {@link HighLevelController}
     */
    public static <T extends HighLevelController> T newInstance(
            Class<T> clazz,
            LowLevelAgent agent,
            Map<String, String> metadata,
            String resourcesFolder)
    {
        try
        {
            return clazz.getConstructor(
                    LowLevelAgent.class,
                    Map.class,
                    String.class)
                    .newInstance(agent, metadata, resourcesFolder);

        } catch (Exception ex)
        {
            Logger.getLogger(HighLevelController.class.getName()).log(
                    Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    @Override
    public final void step(SimState ss)
    {
        this.step();
    }

    public abstract void step();
    private static final HighLevelController dummyController =
            new HighLevelController(null, null, null) {
        @Override
        public void step()
        {
        }

        @Override
        public void stop()
        {
        }
    };

    public static HighLevelController getDummyController()
    {
        return dummyController;
    }
}