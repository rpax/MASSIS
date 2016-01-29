package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Trigger;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class that immutably stores the result of evaluation of trigger. Trigger is
 * basically a list of sense calls (i.e. {@link SenseListExecutor}).
 */
class TriggerResult implements Iterable<SenseResult> {

    private final List<SenseResult> senses;
    private final boolean success;

    /**
     * Create new result of trigger
     *
     * @param senses list of results, what did each sense ended up as
     * @param success was the trigger as a whole successful
     */
    public TriggerResult(List<SenseResult> senses, boolean success) {
        this.success = success;
        this.senses = Collections.unmodifiableList(new ArrayList<SenseResult>(senses));
    }

    /**
     * Create new result of trigger, without specifying any senses.
     *
     * @param success was the trigger successful
     */
    public TriggerResult(boolean success) {
        this(Collections.<SenseResult>emptyList(), success);
    }

    public boolean wasSuccess() {
        return success;
    }

    /**
     * Get iterator to the all senses of this trigger. Unmodifiable.
     *
     * @return iterator to the start of sens's list.
     */
    @Override
    public Iterator<SenseResult> iterator() {
        return senses.iterator();
    }
}

/**
 * Executor that decicdes if goal or trigger are fulfilled. That happens only is
 * all its senses are fired.
 *
 * @author Honza
 */
final class SenseListExecutor<T extends PoshElement> extends AbstractExecutor {

    private List<SenseExecutor> sensesExecutors = new ArrayList<SenseExecutor>();

    /**
     * Create an executor for triggers.
     *
     * @param trigger source of senses, can be null, then no senses will be
     * used.
     * @param senseListPath Path to the parent of all senses. Trigger/goal
     * itself isn't node (e.g. it is a path to drive that have trigger senses).
     * @param ctx variable context that should be passed to the senses
     * @param engineLog logger to record actions of this executor
     */
    SenseListExecutor(Trigger<T> trigger, LapPath senseListPath, VariableContext ctx, EngineLog engineLog) {
        super(senseListPath, ctx, engineLog);

        int triggerSenseId = 0;
        for (Sense sense : trigger) {
            LapPath triggerSensePath = senseListPath.concat(LapType.SENSE, triggerSenseId++);
            SenseExecutor senseExecutor = new SenseExecutor(sense, triggerSensePath, ctx, engineLog);
            sensesExecutors.add(senseExecutor);
        }
    }

    /**
     * Create an executor without any senses (it will always return
     * defaultReturn).
     *
     * @param senseListPath Path to the parent of all senses. Trigger/goal
     * itself isn't node (e.g. it is a path to drive that have trigger senses).
     * @param ctx variable context that should be passed to the senses.
     * @param log log to write debug info.
     */
    SenseListExecutor(LapPath senseListPath, VariableContext ctx, EngineLog log) {
        super(senseListPath, ctx, log);
    }

    /**
     * Evaluate all senses until first one fails. If no senses were specified,
     * consider it a fail.
     *
     * @param defaultReturn what to return if no senses were specified. In
     * trigger true, in goal false
     * @return Result of the senselist. The result is true if none of senses
     * fails, false if at least one fails.
     */
    public TriggerResult fire(IWorkExecutor workExecuter, boolean defaultReturn) {
        List<SenseResult> senses = new LinkedList<SenseResult>();

        for (SenseExecutor senseExecutor : sensesExecutors) {
            defaultReturn = true;

            SenseResult res = senseExecutor.fire(workExecuter);
            senses.add(res);

            if (!res.wasSuccessful()) {
                return new TriggerResult(senses, false);
            }
        }
        return new TriggerResult(senses, defaultReturn);
    }
}
