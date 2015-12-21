package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;

/**
 * Executor for CE. 
 * @author Honza
 */
class CEExecutor extends AbstractExecutor implements ElementExecutor {
    private final PoshPlan plan;
    private final Competence competence;
    private final CompetenceElement choice;
    private PrimitiveCall actionCall;
    private String name;
    private SenseListExecutor<CompetenceElement> trigger;
    private int retries = 0;
    private int maxRetries;
    
    /**
     * Create competence executor.
     * @param plan plan that will be used to resolve primitives
     * @param choice competence element that is going to be executed
     * @param choicePath Path to the choice.
     * @param ctx variable context of competence element
     * @param log logger to record actions of this executor
     */
    CEExecutor(PoshPlan plan, Competence competence, CompetenceElement choice, LapPath choicePath, VariableContext ctx, EngineLog log) {
        super(choicePath, ctx, log);

        assert choicePath.traversePath(plan) == choice;
        assert choicePath.subpath(0, choicePath.length() - 1).traversePath(plan) == competence;
        
        this.plan = plan;
        this.competence = competence;
        this.choice = choice;
        this.name = choice.getName();
        this.trigger = new SenseListExecutor<CompetenceElement>(choice.getTrigger(), choicePath, ctx, log);
        this.maxRetries = choice.getRetries();
        this.actionCall = choice.getAction().getActionCall();
    }

    private LapPath createChoiceActionPath() {
        return path.concat(LapType.ACTION, 0);
    }
    
    /**
     * FIXME: I should probably make single method and join methods in CEExecutor, APExecutor and DEExecutor
     * @param plan
     * @param actionCall
     * @return
     */
    private StackElement createActionExecutor(PrimitiveCall actionCall) {
        LapPath choiceActionPath = createChoiceActionPath();
    	return getElement(plan, actionCall, choiceActionPath); 
    }

    /**
     * Can this executor be executed? Are all preconditions (triggers and 
     * retries) OK?
     * @param workExecuter
     * @return
     */
    TriggerResult isReady(IWorkExecutor workExecuter) {
        engineLog.fine("isReady? " + retries + "/" + maxRetries);
        TriggerResult result;
        if (maxRetries == CompetenceElement.INFINITE_RETRIES || retries < maxRetries) {
            result = trigger.fire(workExecuter, true);
        } else {
            result = new TriggerResult(false);
        }
        return result;
    }

    /**
     * How should this behave:
     *  - if called from above, return new FOLLOW element for the action
     *  - if the action was already finished, return
     * @param workExecuter
     * @return
     */
    @Override
    public FireResult fire(IWorkExecutor workExecuter) {
        engineLog.pathReached(path);
        if (actionCalled) {
            actionCalled = false;
            return new FireResult(FireResult.Type.SURFACE_CONTINUE);
        }

        retries++;
        actionCalled = true;
        return new FireResult(FireResult.Type.FOLLOW, createActionExecutor(actionCall));
    }

    private boolean actionCalled = false;

    /**
     * @return Get name of this CE
     */
    String getName() {
        return name;
    }
}
