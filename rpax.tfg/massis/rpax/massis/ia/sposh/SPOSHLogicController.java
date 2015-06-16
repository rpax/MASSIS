package rpax.massis.ia.sposh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import rpax.massis.ia.AIController;
import rpax.massis.ia.sposh.actions.SimulationAction;
import rpax.massis.ia.sposh.senses.SimulationSense;
import rpax.massis.model.agents.Agent;
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
@SuppressWarnings("rawtypes")
public class SPOSHLogicController<SO extends Agent> extends AIController<SO> {

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
	/**
	 * Timer for posh engine, for things like timeouts and so on.
	 */
	private final SimulationContext<SO> context;

	/**
	 * Main constructor. Loads a plan and attaches its logic to the agent
	 * 
	 * @param agent
	 *            the agent
	 * @param planFilePath
	 *            the path of the sposh plan
	 */
	public SPOSHLogicController(SO agent, String planFilePath) {
		super(agent);
		logger = Logger.getLogger(agent.getClass().getName() + "#"
				+ agent.getID());
		logger.setLevel(Level.WARNING);
		PoshPlan poshPlan = loadPoshPlanFromFile(planFilePath);
		// Creation of the context
		context = new SimulationContext<>(agent);
		this.workExecutor = new StateWorkExecutor(logger);
		// Creation of the actions of the plan, done by reflection
		for (String actionClassName : poshPlan.getActionsNames())
		{
			try
			{

				SimulationAction action = (SimulationAction) Class
						.forName(actionClassName)
						.getConstructor(SimulationContext.class)
						.newInstance(context);
				this.workExecutor
						.addAction(action.getClass().getName(), action);
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, null, ex);
			}
		}
		// the same its done with the senses
		for (String actionClassName : poshPlan.getSensesNames())
		{
			try
			{
				SimulationSense sense = (SimulationSense) Class
						.forName(actionClassName)
						.getConstructor(SimulationContext.class)
						.newInstance(context);
				this.workExecutor.addSense(sense.getClass().getName(), sense);
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, null, ex);
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
	public final void logic() {

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
			EvaluationResultInfo result = getEngine()
					.evaluatePlan(workExecutor);
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

	public static synchronized PoshPlan loadPoshPlanFromFile(String planFilePath) {
		try
		{
			return parsePlan(getPlanFromFile(planFilePath));
		}
		catch (IOException | ParseException ex)
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
	 * @param planSource
	 *            plan source to be parsed
	 * @return parsed plan
	 * @throws ParseException
	 *             if there is an syntax error in the plan.
	 */
	public static PoshPlan parsePlan(String planSource) throws ParseException {
		StringReader planReader = new StringReader(planSource);
		PoshParser parser = new PoshParser(planReader);
		return parser.parsePlan();
	}

	/**
	 * Get sposh engine for this logic
	 * 
	 * @return null if engine wasn't yet created(is created in
	 *         {@link SposhLogicController#initializeController(UT2004Bot) } ) or
	 *         the engine.
	 */
	protected final PoshEngine getEngine() {
		return engine;
	}

	/**
	 * Read POSH plan from the stream and return it. Close the stream.
	 * 
	 * @param in
	 *            Input stream from which the plan is going to be read
	 * @return Text of the plan, basically content of the stream
	 * @throws IOException
	 *             If there is some error while reading the stream
	 */
	public static String getPlanFromStream(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		StringBuilder plan = new StringBuilder();
		String line;
		try
		{
			while ((line = br.readLine()) != null)
			{
				plan.append(line);
			}
		}
		finally
		{
			br.close();
		}

		return plan.toString();
	}

	/**
	 * Read POSH plan from the file and return it.
	 * 
	 * @param filename
	 *            Path to the file that contains the POSH plan.
	 * @return Text of the plan, basically content of the file
	 * @throws IOException
	 *             If there is some error while reading the stream
	 */
	public static String getPlanFromFile(String filename) throws IOException {
		FileInputStream f = new FileInputStream(filename);
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
	 * @param resourcePath
	 *            Path to the plan in some package
	 * @return Content of the plan.
	 * @throws IOException
	 *             if something goes wrong, like file is missing, hardisk has
	 *             blown up ect.
	 */
	protected final String getPlanFromResource(String resourcePath)
			throws IOException {
		ClassLoader cl = this.getClass().getClassLoader();
		return getPlanFromStream(cl.getResourceAsStream(resourcePath));
	}

	@Override
	public SimulationContext<SO> getContext() {
		return context;
	}
}
