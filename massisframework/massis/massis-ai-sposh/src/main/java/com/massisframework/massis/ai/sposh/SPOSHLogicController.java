package com.massisframework.massis.ai.sposh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.massisframework.massis.ai.sposh.actions.SimulationAction;
import com.massisframework.massis.ai.sposh.senses.SimulationSense;
import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.util.SimObjectProperty;

import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.PoshParser;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.engine.FireResult;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine.EvaluationResultInfo;
import cz.cuni.amis.pogamut.sposh.engine.timer.SystemClockTimer;
import cz.cuni.amis.pogamut.sposh.executor.StateWorkExecutor;

/**
 * SPOSH Logic controller in MASSIS. Based in Pogamut's Logic Controller
 *
 * @author rpax
 *
 * @param <SO>
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class SPOSHLogicController extends HighLevelController  {

    private final Logger logger;
    @SuppressWarnings("unused")
	private static final Map<String, PoshPlan> cachedPlans = new HashMap<>();
    /**
     * Posh engine that is evaluating the plan.
     */
    private final PoshEngine engine;
    /**
     * Primitive executor that is executing the primitves when engine requests
     * it, passes the variables and returns the value of executed primitive.
     */
    private final StateWorkExecutor workExecutor;
    private final SimulationContext context;

    /**
     * Main constructor. Loads a plan and attaches its logic to the agent
     *
     * @param agent the agent
     * @param planFilePath the path of the sposh plan
     */
    public SPOSHLogicController(LowLevelAgent agent,
            Map<String, String> metadata,
            String resourcesFolder)
    {
        super(agent, metadata, resourcesFolder);
        
        this.logger = Logger.getLogger(
                agent.getClass().getName() + "#" + agent.getID());
        this.logger.setLevel(Level.WARNING);
        final String planPathRelative = metadata.get(SimObjectProperty.PLANFILE.toString());
        final String planPathAbsolute = Paths.get(resourcesFolder,
                planPathRelative).toString();
        final PoshPlan poshPlan = loadPoshPlanFromFile(planPathAbsolute);
        // Creation of the context
        this.context = new SimulationContext(agent);
        
        agent.setHighLevelData(this.context);
        
        this.workExecutor = new StateWorkExecutor(this.logger);
        // Creation of the actions of the plan, done by reflection
        for (final String actionClassName : poshPlan.getActionsNames())
        {
            try
            {

                final SimulationAction action = (SimulationAction) Class
                        .forName(actionClassName)
                        .getConstructor(SimulationContext.class)
                        .newInstance(this.context);
                this.workExecutor
                        .addAction(action.getClass().getName(), action);
            } catch (final Exception ex)
            {
                this.logger.log(Level.SEVERE, null, ex);
            }
        }
        // the same its done with the senses
        for (final String actionClassName : poshPlan.getSensesNames())
        {
            try
            {
                final SimulationSense sense = (SimulationSense) Class
                        .forName(actionClassName)
                        .getConstructor(SimulationContext.class)
                        .newInstance(this.context);
                this.workExecutor.addSense(sense.getClass().getName(), sense);
            } catch (final Exception ex)
            {
                this.logger.log(Level.SEVERE, null, ex);
            }
        }
        
        // creation of the engine
        this.engine = new PoshEngine(agent.getID(), poshPlan,
                new SystemClockTimer(),
                Logger.getLogger(SPOSHLogicController.class.getName()));
        // tenemos el contexto ya.

    }

    /**
     * Logic method evaluates the posh plan every time it is called.
     */
    @Override
    public final void step()
    {

        if (getEngine().getLog() != null)
        {
            // getEngine().getLog().info("Invoking SPOSH engine.");
        }
        // LoggableWorkExecutor loggableWorkExecutor = new LoggableWorkExecutor(
        // workExecutor);
        while (true)
        {
            // EvaluationResultInfo result = getEngine().evaluatePlan(
            // loggableWorkExecutor);
            final EvaluationResultInfo result = getEngine()
                    .evaluatePlan(this.workExecutor);
            // String lastPrimitive =
            // loggableWorkExecutor.getLastExecutedPrimitive();
            if (result.type != null
                    && (result.type == FireResult.Type.CONTINUE
                    || result.type == FireResult.Type.FOLLOW
                    || result.type == FireResult.Type.FULFILLED
                    || result.type == FireResult.Type.SURFACE_CONTINUE || result.type == FireResult.Type.FAILED))
            {
                if (getEngine().getLog() != null)
                {
                    // getEngine().getLog().info("Plan evaluation continues...");
                }
                continue;
            }
            break;
        }
        if (getEngine().getLog() != null)
        {
            // getEngine().getLog().info("Plan evaluation end.");
        }

    }

    public static synchronized PoshPlan loadPoshPlanFromFile(String planFilePath)
    {
        try
        {
            return parsePlan(getPlanFromFile(planFilePath));
        } catch (IOException | ParseException ex)
        {
            Logger.getLogger(SPOSHLogicController.class.getName()).log(
                    Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        /*
         * if (!cachedPlans.containsKey(planFilePath)) {
         * 
         * try { cachedPlans.put(planFilePath, ); } catch (IOException |
         * ParseException ex) { logger.log( Level.SEVERE, null, ex); throw new
         * RuntimeException(ex); }
         * 
         * } return cachedPlans.get(planFilePath);
         */
    }

    /**
     * Parse the supplied plan.
     *
     * @param planSource plan source to be parsed
     * @return parsed plan
     * @throws ParseException if there is an syntax error in the plan.
     */
    public static PoshPlan parsePlan(String planSource) throws ParseException
    {
        final StringReader planReader = new StringReader(planSource);
        final PoshParser parser = new PoshParser(planReader);
        return parser.parsePlan();
    }

    /**
     * Get sposh engine for this logic
     *
     * @return null if engine wasn't yet created(is created in
	 *         {@link SposhLogicController#initializeController(UT2004Bot) } ) or the
     * engine.
     */
    protected final PoshEngine getEngine()
    {
        return this.engine;
    }

    /**
     * Read POSH plan from the stream and return it. Close the stream.
     *
     * @param in Input stream from which the plan is going to be read
     * @return Text of the plan, basically content of the stream
     * @throws IOException If there is some error while reading the stream
     */
    public static String getPlanFromStream(InputStream in) throws IOException
    {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));

        final StringBuilder plan = new StringBuilder();
        String line;
        try
        {
            while ((line = br.readLine()) != null)
            {
                plan.append(line);
            }
        } finally
        {
            br.close();
        }

        return plan.toString();
    }

    /**
     * Read POSH plan from the file and return it.
     *
     * @param filename Path to the file that contains the POSH plan.
     * @return Text of the plan, basically content of the file
     * @throws IOException If there is some error while reading the stream
     */
    public static String getPlanFromFile(String filename) throws IOException
    {
        final FileInputStream f = new FileInputStream(filename);
        return getPlanFromStream(f);
    }

    /**
     * Get POSh plan from resource int the same jar as the class.
     * <p>
     *
     * <pre>
     * // Plan is stored in package cz.cuni.amis.pogamut.testbot under name
     * // poshPlan.lap
     * // This can get the file from .jar or package structure
     * getPlanFromResource(&quot;cz/cuni/amis/pogamut/testbot/poshPlan.lap&quot;);
     * </pre>
     *
     * @param resourcePath Path to the plan in some package
     * @return Content of the plan.
     * @throws IOException if something goes wrong, like file is missing,
     * hardisk has blown up ect.
     */
    protected final String getPlanFromResource(String resourcePath)
            throws IOException
    {
        final ClassLoader cl = this.getClass().getClassLoader();
        return getPlanFromStream(cl.getResourceAsStream(resourcePath));
    }

    public SimulationContext getContext()
    {
        return this.context;
    }

    @Override
    public void stop()
    {
    }

    
}
