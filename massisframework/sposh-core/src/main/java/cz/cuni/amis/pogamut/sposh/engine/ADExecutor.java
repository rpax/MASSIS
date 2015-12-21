package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.Adopt;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.elements.Trigger;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.engine.FireResult.Type;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;

class ADExecutor extends AbstractExecutor implements ElementExecutor {

    private Adopt adopt;
    private PoshPlan plan;
    private SenseListExecutor<Adopt> exitConditionExecutor;

    protected ADExecutor(PoshPlan plan, Adopt ad, LapPath adoptPath, VariableContext ctx, EngineLog engineLog) {
        super(adoptPath, ctx, engineLog);
        
        assert adoptPath.traversePath(plan) == ad;
        
        this.plan = plan;
        this.adopt = ad;
        this.exitConditionExecutor = new SenseListExecutor<Adopt>(ad.getExitCondition(), adoptPath, ctx, engineLog);
    }

    public boolean isExit(IWorkExecutor workExecutor) {
        return exitConditionExecutor.fire(workExecutor, true).wasSuccess();
    }

    public Trigger<Adopt> getExitCondition() {
        return adopt.getExitCondition();
    }

    public TriggeredAction getAdoptedElement() {
        return adopt.getAdoptedElement();
    }

    @Override
    public FireResult fire(IWorkExecutor workExecuter) {
        engineLog.pathReached(path);
        if (isExit(workExecuter)) {
            // we've met exit-condition
            return new FireResult(Type.SURFACE);
        }
        return new FireResult(Type.FOLLOW, createActionExecutor());
    }

    private LapPath createAdoptActionPath() {
        return path.concat(LapType.ACTION, 0);
    }
    
    private StackElement createActionExecutor() {
        PrimitiveCall actionCall = getAdoptedElement().getActionCall();
        LapPath adoptActionPath = createAdoptActionPath();
        return getElement(plan, actionCall, adoptActionPath);
    }
}
