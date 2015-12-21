package cz.cuni.amis.pogamut.sposh.executor;

/**
 * The value returned by the primitive via {@link IWorkExecutor} that can modify the behavior of posh stack evaluation.
 * 
 * @author Jimmy
 */
public enum ActionResult {
	
	/**
	 * Indicated failure of the action.
	 * <p><p>
	 * Failing an action means to roll back intention-stack of the drive down to the last adapt (no adapt == delete the stack).
	 * <p><p>
	 * Note that {@link StateWorkExecutor} won't stop PoshEngine execution upon this result. 
	 */
	FAILED,
	
	/**
	 * Indicates that action is running within the environment and needs time to finish its execution.
	 * <p><p>
	 * PoshEngine won't delete the action from the stack and will wait for its full execution.
	 * <p><p>
	 * Note that {@link StateWorkExecutor} will execute PoshEngine cycles until it hits some RUNNING/RUNNING_ONCE action. 
	 */
	RUNNING,
	
	/**
	 * Indicates that action is running within the environment BUT it wants to be running for only ONE cycle.
	 * <p><p>
	 * PoshEngine will delete the action from the stack and terminates with SURFACE.
	 * <p><p>
	 * Note that {@link StateWorkExecutor} will execute PoshEngine cycles until it hits some RUNNING/RUNNING_ONCE action. 
	 */
	RUNNING_ONCE,
	
	/**
	 * Indicates that the action has finished and PoshEngine may search for another action to execute.
	 * <p><p>
	 * Note that {@link StateWorkExecutor} will continue running PoshEngine after FINISHING some action (until some RUNNING/RUNNING_ONCE action is executed).
	 */
	FINISHED;
	
}
