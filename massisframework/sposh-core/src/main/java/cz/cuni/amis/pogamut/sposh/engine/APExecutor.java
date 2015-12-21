package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;

/**
 * Executor for AP. If successfull, return specified result, otherwise FAIL.
 *
 * @author Honza
 */
@SuppressWarnings("unchecked")
class APExecutor extends AbstractExecutor implements ElementExecutor {

    /**
     * Plan this executor is using to resolve actions and so on.
     */
    private final PoshPlan plan;
    /**
     * The pattern this executor is executing.
     */
    private final ActionPattern actionPattern;
    /**
     * Index of action that will be executed in the next call of {@link #fire(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor)
     * }. At the start, it is 0 (because first action should be exeucted). Once
     * it is equal to size of {@link ActionPattern#getActions()}, we have
     * finished this {@link APExecutor}.
     */
    private int index = 0;
    /**
     * Once this {@link APExecutor} finished all its actions (i.e. all its
     * actions have properly been fired and surfaced back here), return this.
     *
     * TODO: Is this even needed? SURFACE fo all should be sufficient.
     */
    private FireResult.Type result;

    /**
     * Create a new AP executor
     *
     * @param plan plan, we will use it to look up what are actions in ap
     * @param ap ap that will be executed.
     * @param result what to return in case of successful AP execution (SURFACE
     * or FULFILLED)
     * @param ctx
     * @param log logger to record actions of this executor
     */
    APExecutor(PoshPlan plan, ActionPattern ap, FireResult.Type result, LapPath apPath, VariableContext ctx, EngineLog log) {
        super(apPath, ctx, log);
        
        assert apPath.traversePath(plan) == ap;

        this.plan = plan;
        this.actionPattern = ap;
        this.index = 0;
        this.result = result;
    }

    @Override
    public FireResult fire(IWorkExecutor workExecuter) {
        engineLog.pathReached(path);
        if (index == actionPattern.getActions().size()) {
            return new FireResult(result);
        }
        // index is not incremented here, it should be done only whenever the action finishes its execution,
        // see createActionExecutor()
        TriggeredAction action = actionPattern.getActions().get(index);
        StackElement stackElement = createActionExecutor(action.getActionCall());
        return new FireResult(FireResult.Type.FOLLOW, stackElement);
    }

    /**
     * Create an executor for the passed action according to what it really is
     * (C/AP/P).
     *
     * @param plan
     * @param action
     * @return
     */
    private StackElement createActionExecutor(PrimitiveCall actionCall) {
        LapPath actionPath = path.concat(LapType.ACTION, index);
        StackElement element = getElement(plan, actionCall, actionPath, 
                new Runnable() { // FINISHED CALLBACK

                    @Override
                    public void run() {
                        ++index;
                    }
                },
                null,
                new Runnable() { // RUNNING ONCE CALLBACK

                    @Override
                    public void run() {
                        ++index;
                    }
                },
                null);
        if (element.getExecutor() instanceof APExecutor) {
            // APExecutor won't trigger SUCCESS-CALLBACK ... must manually move index of next-to-be-executed action
            ++index;
        }
        return element;
    }
}
