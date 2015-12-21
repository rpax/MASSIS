package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine.EvaluationResult;
import cz.cuni.amis.pogamut.sposh.engine.timer.ITimer;
import cz.cuni.amis.pogamut.sposh.engine.timer.SystemClockTimer;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for evaluating the DriveCollection element.
 *
 * @author HonzaH
 */
final class DCExecutor extends AbstractExecutor {
    private SenseListExecutor<DriveCollection> goalExecutor;
    private List<DEExecutor> deExecutors = new ArrayList<DEExecutor>();
    private DEExecutor lastTriggeredDrive = null;
    private ITimer timer;

    /**
     * Create new executor of {@link DriveCollection}.
     *
     * @param plan Plan which is used for execution
     * @param dcPath Path to DC. There can be multiple plans, so id of {@link LapType#PLAN}
     * can differ. Id used is {@link PoshEngine#engineId}.
     * @param timer Timer used by the executor, commonly {@link SystemClockTimer}.
     * @param engineLog Log used to log exection of engine, also important for
     * Dash using {@link EngineLog#pathReached(cz.cuni.amis.pogamut.sposh.elements.LapPath)
     * }.
     */
    protected DCExecutor(PoshPlan plan, LapPath dcPath, ITimer timer, EngineLog engineLog) {
        super(dcPath, new VariableContext(), engineLog);

        DriveCollection dc = plan.getDriveCollection();
        
        assert path.traversePath(plan) == dc;
        
        this.timer = timer;
        this.goalExecutor = new SenseListExecutor<DriveCollection>(dc.getGoal(), this.path, ctx, engineLog);

        int driveId = 0;
        for (DriveElement drive : dc.getDrives()) {
            LapPath drivePath = path.concat(LapType.DRIVE_ELEMENT, driveId++);
            deExecutors.add(new DEExecutor(plan, drive, drivePath, ctx, engineLog));
        }
    }

    public synchronized PoshEngine.EvaluationResultInfo fire(IWorkExecutor workExecuter) {
        FireResult.Type resultType = null;
        TriggerResult triggerResult = goalExecutor.fire(workExecuter, false);
        if (triggerResult.wasSuccess()) {
            return new PoshEngine.EvaluationResultInfo(EvaluationResult.GOAL_SATISFIED, FireResult.Type.FULFILLED);
        }

        for (DEExecutor deExecutor : deExecutors) {
            if (deExecutor.isReady(timer.getTime(), workExecuter)) {
            	if (lastTriggeredDrive != null && lastTriggeredDrive != deExecutor) {
            		// DRIVE SWITCH
            		// Some drive that has more priority than "lastTriggeredDrive" has to fire
            		// => clean up the stack of deExecutor
            		lastTriggeredDrive.driveInterrupted();            		
            	}            	
            	lastTriggeredDrive = deExecutor;
                resultType = deExecutor.fire(workExecuter, timer);
                return new PoshEngine.EvaluationResultInfo(EvaluationResult.ELEMENT_FIRED, resultType);
            }
        }
        return new PoshEngine.EvaluationResultInfo(EvaluationResult.NO_ELEMENT_FIRED, FireResult.Type.FAILED);
    }
    
    int getDECount() {
    	return deExecutors.size();
    }
    
    String getDEName(int index) {
    	return deExecutors.get(index).getName();
    }

    ElementStackTrace getStackForDE(int index) {
        return deExecutors.get(index).getStackTrace();
    }

    /**
     * Get ElementStackTrace for the drive element with name "name." If there
     * are two drive elements with same name, throw IllegalStateException.
     *
     * @param name name of drive element we are looking for
     * @return stacktrace for element if element with such name exists, else
     * null.
     */
    ElementStackTrace getStackForDE(String name) {
        DEExecutor result = null;
        for (DEExecutor de : deExecutors) {
            boolean equal = de.getName() == null ? name == null : de.getName().equals(name);
            if (equal) {
                if (result == null) {
                    result = de;
                } else {
                    throw new IllegalStateException("Two drive elements with name \"" + name + "\".");
                }
            }
        }
        if (result != null) {
            return result.getStackTrace();
        }

        return null;
    }
}
