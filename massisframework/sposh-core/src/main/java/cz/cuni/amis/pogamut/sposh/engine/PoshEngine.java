package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.engine.timer.ITimer;
import cz.cuni.amis.pogamut.sposh.engine.timer.SystemClockTimer;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.List;
import java.util.logging.Logger;


/**
 * This class is responsible for executing the valid posh plan.
 *
 * @author Honza
 */
public final class PoshEngine {

    /**
     * How did evaluation of plan ended?
     */
    public enum EvaluationResult {

        GOAL_SATISFIED,
        ELEMENT_FIRED,
        NO_ELEMENT_FIRED
    }

    /**
     * Container holding two information about result of last evaluation of the
     * plan and how was stack changed.
     */
    public static class EvaluationResultInfo {
        
        /**
         * How did evaluation of the plan ended?
         */
        public EvaluationResult result;
        /**
         * How was stack changed.
         */
        public FireResult.Type type;

        public EvaluationResultInfo(EvaluationResult result, FireResult.Type type) {
            this.result = result;
            this.type = type;
        }
    }
    private final int engineId;
    PoshPlan plan;
    ITimer timer;
    EngineLog engineLog;
    private DCExecutor dcExecutor;

    protected PoshEngine(PoshPlan plan) {
        this(plan, new SystemClockTimer());
    }

    protected PoshEngine(PoshPlan plan, ITimer timer) {
        this(0, plan, timer, Logger.getLogger(PoshEngine.class.getName()));
    }
    
    protected PoshEngine(PoshPlan plan, ITimer timer,  Logger log) {
        this(0, plan, timer, log);
    }

    /**
     * Create new engine of Yaposh plan and {@link #reset() } it.
     *
     * @param engineId Id of engine, it is used for construction of path in {@link EngineLog#pathReached(cz.cuni.amis.pogamut.sposh.elements.LapPath)
     * }, it is used as i of the plan.
     * @param plan Plan to be executed.
     * @param timer Timer used by the engine
     * @param log Logger for logging the execution of the engine
     */
    public PoshEngine(int engineId, PoshPlan plan, @Deprecated ITimer timer, Logger log) {
        this.engineId = engineId;
        this.plan = plan;
        this.timer = timer;
        this.engineLog = new EngineLog(log);
        reset();
    }

    /**
     * Reset the posh engine, all stacks and variables will be reseted. Use this
     * to return engine to former state, it had when first initialized.
     */
    public final synchronized void reset() {
        LapPath dcPath = new LapPath().concat(LapType.PLAN, engineId).concat(LapType.DRIVE_COLLECTION, 0);
        dcExecutor = new DCExecutor(plan, dcPath, timer, engineLog);
    }

    /**
     * Do one step of the plan, pick a drive from DC and execute the element at
     * the top of its stack.
     *
     * @param workExecuter Executor used to execute primitives.
     * @return Result of execution of the plan, how was stack of drive changed.
     */
    public synchronized EvaluationResultInfo evaluatePlan(IWorkExecutor workExecuter) {
        engineLog.clear();
        EvaluationResultInfo ret = dcExecutor.fire(workExecuter);
        // DO NOT REMOVE. For details see javadoc of the method.
        evaluatePlanExit();
        return ret;
    }

    /**
     * <em>DO NOT REMOVE!!!</em> This method is here as a workaround of slow
     * Netbeans breakpoint API. The API is quite fast when adding a breakpoint
     * of {@link MethodBreakpoint#TYPE_METHOD_ENTRY} type, but <em>very
     * slow</em> (about 1.2 seconds penalty) when using the
     * {@link MethodBreakpoint#TYPE_METHOD_EXIT}. As a workaround, I am adding
     * an {@link MethodBreakpoint#TYPE_METHOD_ENTRY entry type} breakpoint to
     * this method that is used directly before returning from
     * {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor)
     * }.
     */
    private void evaluatePlanExit() {
        // intentionally left empty
    }

    ElementStackTrace getStackForDE(String name) {
        return dcExecutor.getStackForDE(name);
    }

    ElementStackTrace getStackForDE(int index) {
        return dcExecutor.getStackForDE(index);
    }

    int getDECount() {
        return dcExecutor.getDECount();
    }

    String getDEName(int index) {
        return dcExecutor.getDEName(index);
    }

    /**
     * Get logger of engine
     * @return Logger passed in the constructor
     */
    public Logger getLog() {
        return engineLog.getLogger();
    }

    /**
     * Get list of paths that were evaluated during last evaluation of engine.
     */
    List<LapPath> getEvaluatedPaths() {
        return engineLog.getPaths();
    }
    
    /**
     * Get plan of this engine (serialize the parsed plane and return it). DO
     * NOT MODIFY ANYTHING IN RETURNED PLAN!
     *
     * @return
     */
    public final PoshPlan getPlan() {
        return plan;
    }

    /**
     * Convert posh tree into posh plan (textual representation) and return it.
     *
     * @return Textual representation of the posh tree.
     */
    public final String getPoshPlan() {
        return plan.toString();
    }

    /**
     * Get name of engine, retrieved from doc node or from DC name.
     * @return Name of the plan from the documentation, if exists, else name of
     * DC
     */
    public String getName() {
        String planName = plan.getName();
        if (planName.isEmpty()) {
            return plan.getDriveCollection().getName();
        }
        return planName;
    }
}
