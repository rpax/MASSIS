package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Adopt;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;

/**
 * Predecesor of all executors, basically provides only logging infrastructure.
 * @author Honza
 */
abstract class AbstractExecutor {
    /**
     * Path to the element this executor is representing.
     */
    protected final LapPath path;
    /**
     * Variable context in the element the executor is representing.
     */
    protected final VariableContext ctx;
    /**
     * Logger of execution of executor.
     */
    protected final EngineLog engineLog;
    
    /**
     * Create new executor.
     * @param path Path to the element this executor is processing.
     * @param ctx Variable context in the body of an element represented by the executor.
     * @param engineLog Log used to record info about execution of the plan by the engine.
     */
    protected AbstractExecutor(LapPath path, VariableContext ctx, EngineLog engineLog) {
        this.path = path;
        this.ctx = ctx;
        this.engineLog = engineLog;
    }

    public final LapPath getPath() {
        return path;
    }
    
    private StackElement<ADExecutor> getElementAD(PoshPlan plan, PrimitiveCall adaptCall, LapPath referencePath) {
    	String adoptName = adaptCall.getName();
        Adopt adopt = plan.getAD(adoptName);
        if (adopt == null) {
            return null;
        }
        int adoptId = plan.getAdoptId(adopt);
        
        assert plan.getAdopts().get(adoptId) == adopt;
        
        LapPath adoptPath = referencePath.concat(LapType.ADOPT, adoptId);
        VariableContext adoptContext = new VariableContext(ctx, adaptCall.getParameters(), adopt.getParameters());
        ADExecutor adoptExecutor = new ADExecutor(plan, adopt, adoptPath, adoptContext, engineLog);
        
        return new StackElement<ADExecutor>(Adopt.class, adoptName, adoptExecutor);
    }
    
    private StackElement<APExecutor> getElementAP(PoshPlan plan, PrimitiveCall actionPatternCall, LapPath referencePath) {
    	String actionPatternName = actionPatternCall.getName();
        ActionPattern actionPattern = plan.getAP(actionPatternName);
        if (actionPattern == null) {
            return null;
        }
        int actionPatternId = plan.getActionPatternId(actionPattern);
        
        assert plan.getActionPatterns().get(actionPatternId) == actionPattern;
        
        LapPath actionPatternPath = referencePath.concat(LapType.ACTION_PATTERN, actionPatternId);
        VariableContext actionPatternContext = new VariableContext(ctx, actionPatternCall.getParameters(), actionPattern.getParameters());
        APExecutor actionPatternExecutor = new APExecutor(plan, actionPattern, FireResult.Type.SURFACE_CONTINUE, actionPatternPath, actionPatternContext, engineLog);

        return new StackElement<APExecutor>(ActionPattern.class, actionPatternName, actionPatternExecutor);
    }
    
    /**
     * Get competence executor for @competenceCall if exists. If such competence
     * doesn't exists, return null.
     *
     * @param plan Plan in which we are looking for competence with name
     * retrieved from @competenceCall.
     * @param competenceCall Reference to the competence in the plan.
     * @param referencePath Path ending with reference (i.e. {@link LapType#ACTION).
     * @return new wrapped executor if competence of @competenceCall exists,
     * otherwise null.
     */
    private StackElement<CExecutor> getElementC(PoshPlan plan, PrimitiveCall competenceCall, LapPath referencePath) {
        String competenceName = competenceCall.getName();
        Competence competence = plan.getC(competenceName);
        if (competence == null) {
            return null;
        }
        int competenceId = plan.getCompetenceId(competence);

        assert plan.getCompetences().get(competenceId) == competence;

        LapPath competencePath = referencePath.concat(LapType.COMPETENCE, competenceId);
        VariableContext competenceContext = new VariableContext(ctx, competenceCall.getParameters(), competence.getParameters());
        CExecutor competenceExecutor = new CExecutor(plan, competence, competencePath, competenceContext, engineLog);

        return new StackElement<CExecutor>(Competence.class, competenceName, competenceExecutor);
    }
    
    /**
     * Create executor for a primitive action.
     *
     * @param referencePath Path referencing the action. Note that {@link LapPath}
     * doesn't extend into primitives, thus the @referencePath will end with
     * link of type {@link LapType#ACTION}.
     * @return
     */
    private StackElement<ActionExecutor> getElementAction(
    		PoshPlan plan, 
            PrimitiveCall actionCall, 
            LapPath referencePath,
		    Runnable finishedResultCallback, 
            Runnable runningResultCallback, 
            Runnable runningOnceResultCallback, 
            Runnable failedResultCallback)
    {
		String actionName = actionCall.getName();
        ActionExecutor actionExecutor = new ActionExecutor(actionCall,
                finishedResultCallback, runningResultCallback, runningOnceResultCallback, failedResultCallback,
                referencePath, new VariableContext(ctx, actionCall.getParameters()), engineLog);

		return new StackElement<ActionExecutor>(TriggeredAction.class, actionName, actionExecutor);
	}
    
    protected StackElement getElement(PoshPlan plan, PrimitiveCall actionCall, LapPath referencePath) {
    	return getElement(plan, actionCall, referencePath,
                null, null, null, null);
    }
    
    /**
     * Create executor for element referenced by the @actionCall and encapsulate
     * it in the {@link StackElement}. The @actionCall can reference to AP, AD,
     * C or simple primitive action. This method will find correct element that
     * is being referenced and creates an executor for it.
     *
     * <b>IMPORTANT:</b> The action*Result and *Callbacks are used only for
     * action. They are ignored if @actionCall references something else (i.e.
     * not an primitive action).
     *
     * @param plan Plan in which we use to look for element referenced by @actionCall.
     * @param actionCall Reference to some unknown element in the @plan.
     * @param referencePath Path up to the reference. The path will end with {@link LapType#ACTION}
     * (i.e. reference), e.g. <tt>/P:0/DC:0/DE:4/A:0</tt>, the last link is A:0,
     * the reference of fourth drive.
     * @param finishedResultCallback
     * @param runningResultCallback
     * @param runningOnceCallback
     * @param failedResultCallback
     * @return 
     */
    protected StackElement getElement(PoshPlan plan, PrimitiveCall actionCall, LapPath referencePath,
			  						  Runnable finishedResultCallback, 
                                      Runnable runningResultCallback, 
                                      Runnable runningOnceCallback, 
                                      Runnable failedResultCallback
    ) {
    	StackElement stackElement = getElementC(plan, actionCall, referencePath);
    	
		if (stackElement == null) {
			stackElement = getElementAP(plan, actionCall, referencePath);
			if (stackElement == null) {
				stackElement = getElementAD(plan, actionCall, referencePath);
				if (stackElement == null) {
					stackElement = getElementAction(plan, actionCall, referencePath,
													finishedResultCallback,
													runningResultCallback,
													runningOnceCallback,
													failedResultCallback
				    );
				}
			}
		}
		
		return stackElement;    	
    }
}
