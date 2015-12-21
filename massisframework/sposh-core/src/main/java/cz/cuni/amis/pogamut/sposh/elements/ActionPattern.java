package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.MissingParameterException;
import java.awt.datatransfer.DataFlavor;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AP is a named sequence of {@link TriggeredAction actions}.
 *
 * Action patterns: or simple sequences. These are a basic kind of plan
 * aggregate which turn out to be useful in quite a lot of situations, despite
 * their lack of flexibility. They reduce the combinatorial complexity of the
 * agent when a full competence is not really necessary.
 *
 * @author HonzaH
 */
public final class ActionPattern extends PoshDummyElement<ActionPattern, PoshPlan> implements IParametrizedElement {

    /**
     * Name of this AP.
     */
    private String name;
    /**
     * Comment about this pattern
     *
     * @deprecated Because editor doesn't support it
     */
    @Deprecated
    private String comment;
    /**
     * List of all actions contained in this AP.
     */
    private final List<TriggeredAction> actions = new ArrayList<TriggeredAction>();
    /**
     * Unmodifiable list of actions, proxy for {@link ActionPattern#actions}.
     */
    private final List<TriggeredAction> actionsUm = Collections.unmodifiableList(actions);
    /**
     * Formal parameters of this AP. Can be passed to the actions.
     */
    protected FormalParameters params;
    /**
     * Property name for name of this AP.
     */
    public static final String apName = "apName";
    /**
     * Property name for comment
     */
    public static final String apComment = "apComment";
    /**
     * Property name for parameters of AP.
     */
    public static final String apParams = "apParams";
    /**
     * Data flavor of AP for drag and drop.
     */
    public static final DataFlavor dataFlavor = new DataFlavor(ActionPattern.class, "action-pattern-node");

    /**
     * Create new AP.
     *
     * @param name Name of the AP
     * @param params Formal parameters of the AP
     * @param ap List of actions. AP will make a shallow copy
     * @param comment Comment about this action pattern
     */
    ActionPattern(String name, FormalParameters params, List<TriggeredAction> ap, String comment) {
        assert name != null;
        assert params != null;
        assert comment != null;

        this.name = name;
        this.params = params;
        this.comment = comment;

        for (TriggeredAction action : ap) {
            assert action.getParent() == null;
            action.setParent(this);
            actions.add(action);
        }
    }

    /**
     * Add new TriggeredAction as child of this AP.
     *
     * @param action action to be added into this AP
     */
    public void addAction(TriggeredAction action) throws CycleException {
        int beyondLastActionIndex = actionsUm.size();
        addAction(beyondLastActionIndex, action);
    }

    /**
     * Add new @action as action of this AP, emit new child.
     *
     * @param index Index at which to put the @action.
     * @param action orphan
     * @throws CycleException
     */
    public void addAction(int index, TriggeredAction action) throws CycleException {
        assert !action.isChildOfParent();

        action.setParent(this);
        actions.add(index, action);

        PoshPlan root = getRootNode();
        if (root != null && root.isCycled()) {
            actions.remove(action);
            throw CycleException.createFromName(action.getName());
        }
        emitChildNode(action);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\t(AP ");
        sb.append(name);

        // parameters of the competence are right after declaration
        if (!params.isEmpty()) {
            sb.append(" vars(");
            sb.append(params.toString());
            sb.append(")");
        }

        sb.append(" (");
        boolean firstAction = true;
        for (TriggeredAction action : actionsUm) {
            if (!firstAction) {
                sb.append(' ');
            } else {
                firstAction = false;
            }
            sb.append(action.toString());
        }
        sb.append(')');
        if (!comment.isEmpty()) {
            sb.append(" \"");
            sb.append(comment);
            sb.append('"');
        }
        sb.append(")\n");
        return sb.toString();
    }

    @Override
    public List<TriggeredAction> getChildDataNodes() {
        return actionsUm;
    }

    /**
     * Set name of AP. Check for cycles and make sure the name is unique not
     * same as some AP or Competence.
     *
     * @param name string tham matches <tt>IDENT_PATTERN</tt>
     */
    public void setName(String name) throws InvalidNameException, DuplicateNameException, CycleException {
        name = name.trim();

        if (!name.matches(IDENT_PATTERN)) {
            throw InvalidNameException.create(name);
        }
        if (!getName().equals(name)) {
            if (getRootNode() != null && !getRootNode().isUniqueNodeName(name)) {
                throw DuplicateNameException.create(name);
            }
        }

        String oldName = this.name;
        this.name = name;

        if (getRootNode() != null && getRootNode().isCycled()) {
            this.name = oldName;
            throw CycleException.createFromName(name);
        }

        firePropertyChange(apName, oldName, name);
    }

    /**
     * Get name of AP.
     *
     * @return Name of the AP
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get HTML description of AP.
     * @return HTML description of node.
     */
    public String getHtmlDescription() {
        return "<html>Action pattern: " + getName() + "<br/><pre>" + toString() + "</pre></html>";
    }
    
    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        assert child instanceof TriggeredAction;
        return moveChildInList(actions, (TriggeredAction) child, newIndex);
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.ACTION_PATTERN;
    }

    /**
     * Remove action from this AP and emit notification.
     *
     * @param action Action that will be removed.
     */
    public void removeAction(TriggeredAction action) {
        assert actions.contains(action);

        if (actions.size() == 1) {
            try {
                addAction(LapElementsFactory.createAction());
            } catch (CycleException ex) {
                String msg = MessageFormat.format("Adding an action with default name {0} causes a cycle.", LapElementsFactory.DEFAULT_ACTION);
                Logger.getLogger(ActionPattern.class.getName()).log(Level.SEVERE, msg, ex);
                throw new FubarException(msg, ex);
            }
        }

        int removedActionPosition = actionsUm.indexOf(action);

        actions.remove(action);
        action.setParent(null);

        emitChildDeleted(action, removedActionPosition);
    }

    /**
     * Get list of actions in this AP.
     *
     * @return unmodifiable list of actions.
     */
    public List<TriggeredAction> getActions() {
        return actionsUm;
    }

    /**
     * Get formal parametrs of this AP. Formal paramaters contain map of
     * parameterName-default value this AP accepts.
     *
     * @return formal parameters of this AP
     */
    @Override
    public FormalParameters getParameters() {
        return params;
    }

    /**
     * Get comment of the AP.
     *
     * @deprecated Because editor doesn't support it.
     */
    @Deprecated
    String getComment() {
        return comment;
    }

    /**
     * Set comment
     *
     * @deprecated Because editor doesn't support it.
     */
    @Deprecated
    void setComment(String newComment) {
        assert newComment != null;
        String oldComment = this.comment;
        this.comment = newComment;

        firePropertyChange(apComment, oldComment, newComment);
    }

    /**
     * Change parameters of the AP.
     *
     * @param newParams New parameters of AP.
     */
    @Override
    public void setParameters(FormalParameters newParams) {
        FormalParameters oldParams = params;
        this.params = newParams;

        firePropertyChange(apParams, oldParams, newParams);
    }

    // TODO: Unify with rename in competence
    /**
     * Rename {@link ActionPattern} in the {@link PoshPlan}.
     *
     * This method finds all references ín the plan where this AP is references
     * and changes them to the @newAPName
     *
     * @param newAPName New name for this AP in the plan.
     */
    @Override
    public void rename(String newAPName) throws InvalidNameException, DuplicateNameException, CycleException {
        PoshPlan plan = getRootNode();
        if (plan == null) {
            throw new IllegalStateException("AP " + getName() + " is not part of a plan.");
        }

        List<TriggeredAction> planReferences = plan.getAllReferences();
        List<TriggeredAction> referencingActions = new LinkedList<TriggeredAction>();

        for (TriggeredAction planAction : planReferences) {
            boolean actionReferencesAP = planAction.getName().equals(getName());
            if (actionReferencesAP) {
                referencingActions.add(planAction);
            }
        }

        // TODO: rollback if some error happens?
        for (TriggeredAction referencingAction : referencingActions) {
            referencingAction.setActionName(newAPName);
        }

        this.setName(newAPName);
    }

    /**
     * Get action with @actionId. Equivalent of {@link #getActions() }.{@link List#get(int)
     * }.
     *
     * @param actionId Id of desired action.
     * @return Specified action.
     */
    public TriggeredAction getAction(int actionId) {
        return actionsUm.get(actionId);
    }
}
