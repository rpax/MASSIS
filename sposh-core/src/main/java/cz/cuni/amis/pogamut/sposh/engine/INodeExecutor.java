package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.engine.timer.ITimer;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.util.*;

/**
 *
 * @author Honza
 */
interface INodeExecutor {

    /**
     * When this method is called, there is no executor on the stack lower than
     * this one.
     *
     * @param workExecutor
     * @return
     */
    NodeResult evaluate(IWorkExecutor workExecutor);

    NodeResult childEvaluated(NodeState result);

    LapType getType();
}

enum NodeState {

    /**
     * The node has been executed and has no need to be executed again
     */
    DONE,
    /**
     * Node has done something and is ready (if selected) to continue its
     * processing. Basically I would like to continue, but if you can't, I can
     * live with it.
     *
     * Example: primitive action will return this on RUNNING and its parent
     * could be decorator with timeout. If timeout is not reached, FOLLOW child
     * again, if timeout reached, return DONE.
     */
    CONTINUE,
    /**
     * Process returned child in next cycle.
     */
    FOLLOW,
    /**
     * Node has failed.
     */
    FAILED
}

class NodeResult {

    final NodeState state;
    final INodeExecutor executor;

    public NodeResult(NodeState state, INodeExecutor executor) {
        this.state = state;
        this.executor = executor;
    }
}

class DCNodeExecutor extends NodeExecutor<DriveCollection> {

    private final ITimer timer;
    private final SenseListExecutor<DriveCollection> goalExecutor;
    private List<DriveNodeExecutor> driveExecutors = new ArrayList<DriveNodeExecutor>();

    DCNodeExecutor(PoshPlan plan, LapPath path, VariableContext ctx, EngineLog engineLog, ITimer timer) {
        super(plan, path, ctx, engineLog);

        DriveCollection dc = plan.getDriveCollection();
        assert path.traversePath(plan) == dc;

        this.timer = timer;
        this.goalExecutor = new SenseListExecutor<DriveCollection>(dc.getGoal(), this.path, ctx, engineLog);

        int driveId = 0;
        for (DriveElement drive : dc.getDrives()) {
            // TODO: Once confirmed that same, replace driveId
            assert drive.getId() == driveId;
            LapPath drivePath = path.concat(LapType.DRIVE_ELEMENT, driveId++);
            driveExecutors.add(new DriveNodeExecutor(plan, drivePath, ctx, engineLog, timer));
        }
    }
    // TODO: Remove and do some other way
    private DriveNodeExecutor lastTriggeredDrive = null;

    public synchronized PoshEngine.EvaluationResultInfo fire(IWorkExecutor workExecuter) {
        TriggerResult triggerResult = goalExecutor.fire(workExecuter, false);
        if (triggerResult.wasSuccess()) {
            return new PoshEngine.EvaluationResultInfo(PoshEngine.EvaluationResult.GOAL_SATISFIED, FireResult.Type.FULFILLED);
        }

        for (DriveNodeExecutor driveExecutor : driveExecutors) {
            if (driveExecutor.isElegible(workExecuter, timer.getTime())) {
                if (lastTriggeredDrive != null && lastTriggeredDrive != driveExecutor) {
                    // DRIVE SWITCH
                    // Some drive that has more priority than "lastTriggeredDrive" has to fire
                    // => clean up the stack of deExecutor
                    lastTriggeredDrive.interrupt();
                }
                lastTriggeredDrive = driveExecutor;


                NodeResult result;
                if (driveExecutor.stack.isEmpty()) {
                    // Inserts node represented by drive action on the stack
                    result = driveExecutor.evaluate(workExecuter);
                } else {
                    INodeExecutor stackTop = driveExecutor.stack.peek();
                    result = stackTop.evaluate(workExecuter);
                }

                switch (result.state) {
                    case CONTINUE:
                        driveExecutor.stack.pop();
                        break;
                    case DONE:
                        driveExecutor.stack.pop();
                        break;
                    case FAILED:
                        driveExecutor.stack.pop();
                        break;
                    case FOLLOW:
                        driveExecutor.stack.push(result.executor);
                        break;
                    default:
                        throw new FubarException("Wrong type " + result.state);
                }
                
                
                // TODO: Remove later, this translation is to keep somehow compatible with tests
                FireResult.Type resultType;// = driveExecutor.fire(workExecuter, timer);
                switch (result.state) {
                    case CONTINUE:
                        throw new UnsupportedOperationException("TODO: implement");
                    case DONE:
                        throw new UnsupportedOperationException("TODO: implement");
                    case FAILED:
                        throw new UnsupportedOperationException("TODO: implement");
                    case FOLLOW:
                        throw new UnsupportedOperationException("TODO: implement");
                    default:
                        throw new FubarException("Wrong type " + result.state);
                }

 //               return new PoshEngine.EvaluationResultInfo(PoshEngine.EvaluationResult.ELEMENT_FIRED, resultType);
            }
        }
        return new PoshEngine.EvaluationResultInfo(PoshEngine.EvaluationResult.NO_ELEMENT_FIRED, FireResult.Type.FAILED);
    }
}

abstract class NodeExecutor<NODE extends PoshElement> extends AbstractExecutor {

    protected final PoshPlan plan;
    protected final NODE node;

    NodeExecutor(PoshPlan plan, LapPath path, VariableContext ctx, EngineLog engineLog) {
        super(path, ctx, engineLog);
        this.plan = plan;
        this.node = path.<NODE>traversePath(plan);
    }
//    @Override
//    public final LapType getType() {
//        return path.getLink(path.length() - 1).getType();
//    }
}

class DriveNodeExecutor extends NodeExecutor<DriveElement> implements INodeExecutor {

    final Deque<INodeExecutor> stack = new LinkedList<INodeExecutor>();
    private final ITimer timer;
    private final SenseListExecutor<DriveElement> triggerExecutor;
    private long lastFired = Integer.MAX_VALUE;

    public DriveNodeExecutor(PoshPlan plan, LapPath path, VariableContext ctx, EngineLog engineLog, ITimer timer) {
        super(plan, path, ctx, engineLog);

        this.timer = timer;
        this.triggerExecutor = new SenseListExecutor<DriveElement>(path, ctx, engineLog);
    }

    INodeExecutor createExecutor(PrimitiveCall actionCall) {
        // TODO: Implement
        throw new UnsupportedOperationException("TODO: Implement");
    }

    boolean isElegible(IWorkExecutor workExecutor, long timestamp) {
        long passed = timestamp - lastFired;
        Freq freq = node.getFreq();
        if (Freq.compare(freq.tick(), passed) > 0) {
            engineLog.fine("Max.firing frequency for drive " + node.getName() + " exceeded, has to be at least " + freq.tick() + "ms, but was only " + passed);
            return false;
        }
        TriggerResult result = triggerExecutor.fire(workExecutor, true);
        return result.wasSuccess();
    }

    private PrimitiveCall getActionCall() {
        return node.getAction().getActionCall();
    }

    @Override
    public NodeResult evaluate(IWorkExecutor workExecutor) {
        engineLog.pathReached(path);
        engineLog.finest("Stack of drive " + node.getName() + " is empty, adding initial element: " + getActionCall().toString());
        lastFired = timer.getTime();

        return new NodeResult(NodeState.FOLLOW, createExecutor(getActionCall()));
    }

    @Override
    public NodeResult childEvaluated(NodeState result) {
        engineLog.pathReached(path);
        lastFired = timer.getTime();

        switch (result) {
            case CONTINUE:
                return new NodeResult(NodeState.FOLLOW, createExecutor(getActionCall()));
            case DONE:
                return new NodeResult(NodeState.DONE, this);
            case FAILED:
                return new NodeResult(NodeState.FAILED, this);
            default:
                throw new IllegalArgumentException("Unsupported result: " + result);
        }
    }

    @Override
    public LapType getType() {
        return LapType.DRIVE_ELEMENT;
    }

    /**
     * Continuous execution of the drive executor has been interrupted by
     * another drive executor.
     */
    void interrupt() {
        // TODO: In original #driveInterrupted, it cleared up to first occurance of ADExecutor
        stack.clear();
    }
}

class ActionNodeExecutor extends NodeExecutor<TriggeredAction> implements INodeExecutor {

    public ActionNodeExecutor(PoshPlan plan, LapPath actionPath, VariableContext ctx, EngineLog engineLog) {
        super(plan, actionPath, ctx, engineLog);
    }

    /**
     * Evaluate node
     *
     * @param workExecutor
     * @return Result of node evaluation
     */
    @Override
    public NodeResult evaluate(IWorkExecutor workExecutor) {
        engineLog.pathReached(path);

        ActionResult result = workExecutor.executeAction(node.getActionCall().getName(), ctx);
        switch (result) {
            case FAILED:
                return new NodeResult(NodeState.FAILED, this);
            case FINISHED:
                return new NodeResult(NodeState.DONE, this);
            case RUNNING:
                return new NodeResult(NodeState.CONTINUE, this);
            case RUNNING_ONCE:
                return new NodeResult(NodeState.DONE, this);
            default:
                throw new IllegalStateException("Unexpected ActionResult: " + result);
        }
    }

    /**
     * The executor has asked to follow node before and the node has finished.
     */
    @Override
    public NodeResult childEvaluated(NodeState result) {
        throw new FubarException("Action doesn't have children.");
    }

    @Override
    public final LapType getType() {
        return LapType.ACTION;
    }
}