package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.engine.FireResult.Type;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.ArrayList;
import java.util.List;

/**
 * Executor for competence.
 *
 * @author Honza
 */
final class CExecutor extends AbstractExecutor implements ElementExecutor {

    private boolean executorWasEvalued = false;
    
    /**
     * Remnant from time when *POSH has goal of competence.
     */
    @Deprecated
    private final SenseListExecutor goalExecutor;
    private final List<CEExecutor> ceExecutors = new ArrayList<CEExecutor>();

    /**
     * Create new competence executor
     *
     * @param plan plan used to resolve names of primitives into S/A/AP/C
     * @param competence competence to execute
     * @param competencePath Path to the competence.
     * @param ctx variable context of competence
     * @param log logger to record actions of this executor
     */
    public CExecutor(PoshPlan plan, Competence competence, LapPath competencePath, VariableContext ctx, EngineLog log) {
        super(competencePath, ctx, log);

        assert competencePath.traversePath(plan) == competence;

        this.goalExecutor = new SenseListExecutor<Competence>(competencePath, ctx, log);

        int choiceId = 0;
        for (CompetenceElement ce : competence.getChildDataNodes()) {
            LapPath choicePath = competencePath.concat(LapType.COMPETENCE_ELEMENT, choiceId++);
            CEExecutor choiceExecutor = new CEExecutor(plan, competence, ce, choicePath, ctx, log);
            
            ceExecutors.add(choiceExecutor);
        }
    }

    /**
     * Checks whether competence goal has been met.
     *
     * @param workExecutor
     * @return
     */
    public boolean isGoalSatisfied(IWorkExecutor workExecutor) {
        if (goalExecutor == null) {
            return false;
        }
        return goalExecutor.fire(workExecutor, false).wasSuccess();
    }

    /**
     * Check if goal is not satisfied, if it is, there is no need to continue to
     * execute this competence (return {@link Type#FULFILLED}). <p> For all CE
     * in priority order: Check if CE retry limit hasn't been exceeded and if
     * the CE trigger is valid. If element is primitive, fire it. If it is AP or
     * C, return it as next element on the stack
     *
     * If none of CE can be fired, the C failed and there is no need to continue {@link Type#FAILED};
     *
     * @param workExecuter
     * @return
     */
    @Override
    public FireResult fire(IWorkExecutor workExecuter) {
        engineLog.pathReached(path);
        
        if (executorWasEvalued) {
            return new FireResult(Type.SURFACE_CONTINUE);
        }
        executorWasEvalued = true;
        
        // is goal satisfied?
        // TODO: What should I do with goal of competence? It would be best to remove it completely.
        TriggerResult goalResult = goalExecutor.fire(workExecuter, false);
        if (goalResult.wasSuccess()) {
            return new FireResult(FireResult.Type.FULFILLED);
        }

        for (CEExecutor ceExecutor : ceExecutors) {
            TriggerResult triggerResult = ceExecutor.isReady(workExecuter);
            if (triggerResult.wasSuccess()) {
                StackElement stackElement = new StackElement(CompetenceElement.class, ceExecutor.getName(), ceExecutor);
                return new FireResult(FireResult.Type.FOLLOW, stackElement);
            }
        }
        // If no element was fired, we have failed
        return new FireResult(FireResult.Type.FAILED);
    }
}
