package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Interface that is used by posh engine to execute primitives.
 * There may be multiple ways to execute primitives, original posh is just
 * calling methods, we may want primitives executed in phases (like INIT, RUN*, FINISH).
 * 
 * @author Honza
 */
public interface IWorkExecutor {
	
	/**
     * Execute sense and get the result.
     * @param senseName name of primitive
     * @param ctx variable context for sense containing possible parameters
     * @return result of executed primitive
     */
	Object executeSense(String senseName, VariableContext ctx);
	
	/**
     * Execute action and get the result.
     * @param actionName name of primitive
     * @param ctx variable context for action containing possible parameters
     * @return result of executed primitive
     */
	ActionResult executeAction(String actionName, VariableContext ctx);

}
