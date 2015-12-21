package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import java.util.ArrayList;
import java.util.List;
import java.awt.datatransfer.DataFlavor;

/**
 * CompetenceElement is basically one of choices of the {@link Competence}. It
 * has a name(not used elsewhere in the tree, but unique), {@link Trigger} and
 * defined {@link TriggeredAction} that should be evaluated if the triggers is
 * satisfied.
 * <p/>
 * Example in POSH:
 * <pre>(move (trigger ((see-player))) move-player)</pre>
 *
 * @author Honza
 */
public final class CompetenceElement extends PoshDummyElement<CompetenceElement, Competence> implements INamedElement, IConditionElement<CompetenceElement> {

    /**
     * Name of the choice.
     */
    private String name;
    /**
     * If this element is during one traversal evaluated for elegibility more
     * times than the this number, it is automatically unelegible for the
     * remainder of the traversal.
     *
     * @deprecated Because nobody is using it. Also not very useful.
     */
    @Deprecated
    private int retries;
    /**
     * Comment about this choice.
     *
     * @deprecated Because editor doesn't support it
     */
    @Deprecated
    private String comment;
    /**
     * Trigger of this choice. If satisfied, the action will be pursued.
     */
    private final Trigger<CompetenceElement> trigger = new Trigger<CompetenceElement>(this);
    /**
     * Action of this choice.
     */
    private final TriggeredAction action;
    /**
     * Property name of {@link CompetenceElement#name}.
     */
    public static final String ceName = "ceName";
    /**
     * Property name of {@link CompetenceElement#retries}.
     */
    public static final String ceRetries = "ceRetries";
    /**
     * Property name of {@link CompetenceElement#comment}.
     */
    public static final String ceComment = "ceComment";
    /**
     * Data flavor of competence element, used for drag and drop.
     */
    public static final DataFlavor dataFlavor = new DataFlavor(CompetenceElement.class, "competence-element");
    /**
     * Special value for infinite number of retries.
     *
     * @see CompetenceElement#getRetries()
     */
    public static final int INFINITE_RETRIES = -1;

    /**
     * Create a choice.
     *
     * @param name Name of a choice
     * @param triggerSenses List of senses that will trigger the action of this
     * choice
     * @param actionCall Action call(=action name with arguments), name can be a
     * normal action, an AP/C name.
     */
    public CompetenceElement(String name, List<Sense> triggerSenses, PrimitiveCall actionCall, int retries, String comment) {
        assert name != null;
        assert comment != null;

        this.name = name;
        this.retries = retries;
        this.comment = comment;
        this.action = LapElementsFactory.createAction(actionCall);
        this.action.setParent(this);

        for (Sense sense : triggerSenses) {
            trigger.add(sense);
        }
    }

    /**
     * Get trigger of this choice. Trigger can contain number of senses that are
     * evaluated (along with retries) to determine elegibility of the element
     * for traversal.
     *
     */
    public Trigger<CompetenceElement> getTrigger() {
        return trigger;
    }

    /**
     * Get trigger of the choice.
     *
     * @return trigger of choice, never null.
     */
    @Override
    public Trigger<CompetenceElement> getCondition() {
        return getTrigger();
    }

    /**
     * Get action of this choice.
     *
     * @return Action of this choice.
     */
    public TriggeredAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(name);
        if (!trigger.isEmpty()) {
            sb.append(" (trigger ");
            sb.append(trigger.toString());
            sb.append(')');
        }
        sb.append(' ');
        sb.append(action.toString());
        if (retries != INFINITE_RETRIES) {
            sb.append(' ');
            sb.append(retries);
        }
        if (!comment.isEmpty()) {
            sb.append(" \"");
            sb.append(comment);
            sb.append('"');
        }
        sb.append(')');

        return sb.toString();
    }

    @Override
    public List<PoshElement> getChildDataNodes() {
        List<PoshElement> children = new ArrayList<PoshElement>(trigger);
        children.add(action);
        return children;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set name of this choice.
     *
     * @param name new name of this choice.
     */
    public void setName(String name) throws InvalidNameException, DuplicateNameException {
        name = name.trim();
        if (!name.matches(IDENT_PATTERN)) {
            throw InvalidNameException.create(name);
        }
        boolean isSameName = this.name.equals(name);
        if (getParent() != null && isUsedName(name, getParent().getChildDataNodes()) && !isSameName) {
            throw new DuplicateNameException("Choice with " + name + " already exists in competence " + getParent().getName());
        }

        String oldName = this.name;
        this.name = name;
        firePropertyChange(ceName, oldName, name);

    }

    /**
     * Becasue name of a choice is not used as reference in the plan, this
     * method only changes a name of a choice, identical to {@link #setName(java.lang.String)
     * }.
     *
     * @see #setName(java.lang.String)
     * @param newName New name of choice.
     */
    @Override
    public void rename(String newName) throws InvalidNameException, CycleException, DuplicateNameException {
        setName(newName);
    }

    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        assert child instanceof Sense;
        trigger.moveSense(newIndex, (Sense) child);
        return true;
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.COMPETENCE_ELEMENT;
    }

    /**
     * When engine is evaluating drive and it encounteres a node How many
     * retries can this element experience before it is removed from list of
     * elegible elements for current traverasal.
     *
     * @return
     * @deprecated Remnants of original posh,
     */
    @Deprecated
    public int getRetries() {
        return retries;
    }

    /**
     * Change number of allowed retries of this node and fire the change.
     *
     * @param newRetries new number of retries. Must be &ge;0 or {@link CompetenceElement#INFINITE_RETRIES}.
     * @deprecated Not used in the editor
     */
    @Deprecated
    public void setRetries(int newRetries) {
        assert newRetries >= 0 || newRetries == INFINITE_RETRIES;

        int oldValue = retries;
        retries = newRetries;
        firePropertyChange(ceRetries, oldValue, newRetries);
    }

    /**
     * Get comment for this element.
     *
     * @return Comment, not null(maybe blank)
     * @deprecated Not supported in the editor.
     */
    @Deprecated
    public String getComment() {
        return comment;
    }

    /**
     * Change the comment of this element and fire the change.
     *
     * @param newComment New comment of this element
     * @deprecated Not used in the editor
     */
    @Deprecated
    public void setComment(String newComment) {
        assert newComment != null;
        String oldComment = comment;
        comment = newComment;
        firePropertyChange(ceComment, oldComment, newComment);
    }
}
