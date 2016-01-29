package cz.cuni.amis.pogamut.sposh.executor;

/**
 * Extends {@link IWorkExecutor} adding methods {@link ILogicWorkExecutor#logicBeforePlan()} and {@link ILogicWorkdExecutor#logicAfterPlan()} that can be used for slipping code that should be executed
 * every logic-evaluation iteration.
 * @author Jimmy
 * @deprecated This is a wrong place for these methods, {@link SposhLogicController}
 *             has templated {@link IWorkExecutor} and methods logicBeforePlan
 *             and logicBeforePlan. This is no longer automatically executed by LogicController anymore. You must do it yourself.
 */
@Deprecated
public interface ILogicWorkExecutor extends IWorkExecutor {

	/**
	 * Method that is triggered every time the plan for executor is evaluated. It is triggered right before the plan evaluation.
	 */
        @Deprecated
	public void logicBeforePlan();

	/**
	 * Method that is triggered every time the plan for executor is evaluated. It is triggered right after the plan evaluation.
	 */
	@Deprecated
        public void logicAfterPlan();

}
