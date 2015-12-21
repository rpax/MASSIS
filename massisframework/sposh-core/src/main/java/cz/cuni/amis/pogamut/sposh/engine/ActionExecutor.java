package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;

/**
 * This is used to execute single primitive, like in DE or C.
 * @author Honza
 */
class ActionExecutor extends AbstractExecutor implements ElementExecutor {
    final private PrimitiveCall actionCall;
    
    final private FireResult.Type finishedResult = FireResult.Type.SURFACE_CONTINUE;
    final private FireResult.Type runningResult = FireResult.Type.SURFACE;
    final private FireResult.Type runningOnceResult = FireResult.Type.SURFACE;
    final private FireResult.Type failedResult = FireResult.Type.FAILED;
    
    final private Runnable finishedResultCallback;
    final private Runnable runningResultCallback;
    final private Runnable runningOnceResultCallback;
    final private Runnable failedResultCallback;

    /**
     * Create a primitive executor that will call a specified action
     * within context of parameters
     * 
     * @param actionCall actionCall to primitive
     * @param finishedResultCallback called in case of {@link ActionResult#FINISHED}, may be null
     * @param runningResultCallback called in case of {@link ActionResult#RUNNING}, may be null
     * @param failedResultCallback called in case of {@link ActionResult#FAILED}, may be null
     * @param actionPath Path in the plan to the action this executor is executing.
     * @param ctx Context of this primitive. Shoudl differ from parent, but isn't necessary
     * @param log logger to record actions of this executor
     */
    ActionExecutor(
            PrimitiveCall actionCall,
            Runnable finishedResultCallback,
            Runnable runningResultCallback,
            Runnable runningOnceResultCallback,
            Runnable failedResultCallback,
            LapPath actionPath,
            VariableContext ctx,
            EngineLog log) {
        super(actionPath, ctx, log);
        
        this.actionCall = actionCall;
        
        this.finishedResultCallback = finishedResultCallback;
        this.runningResultCallback = runningResultCallback;
        this.runningOnceResultCallback = runningOnceResultCallback;
        this.failedResultCallback = failedResultCallback;
    }


    /**
     * Fire the action and return FireResult(false), so don't continue
     * the execution.
     * @param workExecuter
     * @return failResult if result of primitive is empty or false, true otherwise
     */
    @Override
    public FireResult fire(IWorkExecutor workExecuter) {
        engineLog.pathReached(path);
        
        ActionResult result = workExecuter.executeAction(actionCall.getName(), ctx);
        switch(result) {
        case FAILED:
        	if (failedResultCallback != null) failedResultCallback.run();
        	return new FireResult(failedResult);
        	
        case FINISHED:
        	if (finishedResultCallback != null) finishedResultCallback.run();
        	return new FireResult(finishedResult);
        	
        case RUNNING:
        	if (runningResultCallback != null) runningResultCallback.run();
        	return new FireResult(runningResult);
        	
        case RUNNING_ONCE:
        	if (runningOnceResultCallback != null) runningOnceResultCallback.run();
        	return new FireResult(runningOnceResult);
        	
        default:
        	throw new IllegalStateException("Unexpected ActionResult: " + result);
        }        
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + actionCall.getName() + "]";
    }
}
