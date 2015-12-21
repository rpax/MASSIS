package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.Freq;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.engine.timer.ITimer;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.ArrayList;
import java.util.List;

/**
 * Executor of DE.
 * @author Honza
 */
class DEExecutor extends AbstractExecutor {
    private final PoshPlan plan;
    private final DriveElement drive;
    private final SenseListExecutor<DriveElement> trigger;
    private final Freq freq;
    private long lastFired = Integer.MAX_VALUE;
    private ElementStackTrace stackTrace = new ElementStackTrace();
    
    DEExecutor(PoshPlan plan, DriveElement de, LapPath drivePath, VariableContext ctx, EngineLog log) {
        super(drivePath, ctx, log);

        assert drivePath.traversePath(plan) == de;

        this.plan = plan;
        this.drive = de;
        this.freq = de.getFreq();
        this.trigger = new SenseListExecutor<DriveElement>(de.getTrigger(), drivePath, ctx, log);
    }

    private LapPath createDriveActionPath() {
        return path.concat(LapType.ACTION, 0);        
    }
    
    private StackElement createInitialStackElement(PoshPlan plan, PrimitiveCall actionCall) {
        LapPath driveActionPath = createDriveActionPath();
    	return getElement(plan, actionCall, driveActionPath); 
    }

    /**
     * Is time since last fire less than specified frequency and
     * are triggers fulfilled?
     * 
     * @param timestamp current time
     * @return true if conditions for evaluation are fulfilled
     */
    public synchronized boolean isReady(long timestamp, IWorkExecutor workExecuter) {
        long passed = timestamp - lastFired;
        // Has to be at least "freq.tick()" ms, was "passed" ms
        if (Freq.compare(freq.tick(), passed) > 0) {
            engineLog.fine("Max.firing frequency for drive " + drive.getName() + " exceeded, has to be at least " + freq.tick() + "ms, but was only " + passed);
            return false;
        }
        TriggerResult triggerResult = trigger.fire(workExecuter, true);
        return triggerResult.wasSuccess();
    }
    
    /**
     *
     * @param workExecuter
     * @param timer
     * @return true if element was fired, false otherwise
     */
    public synchronized FireResult.Type fire(IWorkExecutor workExecuter, ITimer timer) {
        engineLog.pathReached(path);
    	// TODO: optimalize by pre-caching indexes of adapts/competences on the stack 
        // CHECK FOR COMPETENCES/ADAPTS GOALS/EXIT-CONDITIONS FIRST!!!
    	List<CExecutor> competences = new ArrayList<CExecutor>();
    	for (int i = stackTrace.size()-1; i >= 0; --i) {
    		StackElement element = stackTrace.get(i);
    		if (element.getExecutor() instanceof CExecutor) {
    			competences.add((CExecutor) element.getExecutor());
    			continue;
    		}
    		if (element.getExecutor() instanceof ADExecutor) {
    			// IS EXIT-CONDITION FULLFILLED?
    			if (((ADExecutor)element.getExecutor()).isExit(workExecuter)) {
    				// EARLY SUCCEED!!!
    				// => cut down the stack and continue
    				stackTrace.cutDownToIncluding(element.getExecutor());
    				competences.clear();
    			} else {
    				// EXIT-CONDITION NOT FULFILLED
    				// => break and execute the drive
    				break;
    			}
    			continue;
    		}
    	}
    	// Check for competences goal from the parent to child 
    	// Notice that "competences" are containing competences in different order then in the stack list
    	for (int i = competences.size()-1; i >= 0; --i) {
    		CExecutor competence = competences.get(i);
    		if (competence.isGoalSatisfied(workExecuter)) {
    			// competence goal has been satisfied
    			// remove its sub-stack from the stack
    			stackTrace.cutDownToIncluding(competence);
    			break;
    		}
    	}
    	
    	// first, if stack is empty, add initial element
        if (stackTrace.isEmpty()) {
            PrimitiveCall actionCall = drive.getAction().getActionCall();
            engineLog.finest("Stack of drive " + drive.getName() + " is empty, adding initial element: " + actionCall.toString());
            stackTrace.add(createInitialStackElement(plan, actionCall));
            return FireResult.Type.CONTINUE;
        }

        // fire the element at the top of the stack
        FireResult result = stackTrace.peek().getExecutor().fire(workExecuter);
        lastFired = timer.getTime();

        engineLog.fine("The fired element returned: " + result.getType());
        switch (result.getType()) {
            case FULFILLED:
                // I wonder is this state even has meaning. If element was fulfilled,
                // shouldn't I just move up one level?
                // For now, handle it same way as original, according
                // to original sposh, handle it same way as failed case,
                // just element to DE action.
                stackTrace.removeAllElements();
                break;
            case FAILED:
                // The element has failed, reset call stack, we will try again with a clean plate
                stackTrace.removeAllElements();
                break;
            case FOLLOW:
                // Now, our executing element has decided, that some other element
                // should have preference. Add new element to the stack so it is
                // executed next time.
                stackTrace.push(result.getNextElement());
                break;
            case CONTINUE:
                // We are supposed to continue to execute the currently executed element
                // In other words, do nothing
                break;                
            case SURFACE:
            case SURFACE_CONTINUE:
                // Return from this element. It is possible that the surfacing 
                // element was the default one, so if empty stack, add default
                stackTrace.pop();
                break;
            default:
                throw new IllegalStateException("State \"" + result.getType() + "\" not expected. Serious error.");
        }
        return result.getType();
    }

    ElementStackTrace getStackTrace() {
        return stackTrace;
    }

    /**
     * Get name of this DEExecutor (same as the de element)
     * @return
     */
    public String getName() {
        return drive.getName();
    }
    
    /**
     * Called by {@link DCExecutor} whenever other drive is switched-in interrupting this drive that was previously executed. 
     */
    public void driveInterrupted() {
    	// Pop stack until ADExecutor is hit or stack emptied
    	while (stackTrace.size() > 0 && !(stackTrace.peek().getExecutor() instanceof ADExecutor)) {
    		stackTrace.pop();
    	}
    }
}
