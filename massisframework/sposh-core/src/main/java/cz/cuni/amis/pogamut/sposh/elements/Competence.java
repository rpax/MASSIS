package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.*;
import java.awt.datatransfer.DataFlavor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Competence is basically a named structure offering several {@link CompetenceElement choices}.
 * If you imagine a decision tree, the point, where node splits into several
 * subnodes is akin to the to the {@link Competence}.
 * <p/>
 * What is difference between {@link Competence} and {@link DriveCollection}?
 * Well, for one, DC is in the root of the plan, DC has a goal (well, Cs had
 * goal too in the past), the main difference how they are processed by the
 * engine (see description of how {@link DriveCollection} is evaluated).
 * <p/>
 * Unlike DC, Competence is simple decision tree, engine will take the element
 * with highest priority and satisfied trigger and traverses into that node
 * without looking back.
 *
 * @see DriveCollection
 * @author HonzaH
 */
public final class Competence extends PoshDummyElement<Competence, PoshPlan> implements IParametrizedElement {

    /**
     * Name of this competence, can be referenced
     */
    private String name;
    /**
     * Formal parameters of the competence
     */
    private FormalParameters params = new FormalParameters();
    /**
     * List of all possible elements (choices) of this competence
     */
    private final List<CompetenceElement> elements = new LinkedList<CompetenceElement>();
    /**
     * Unmodifiable proxy list of elements
     */
    private final List<CompetenceElement> elementsUm = Collections.unmodifiableList(elements);
    /**
     * Property string of competence name
     */
    public static final String cnName = "cnName";
    /**
     * Property string of competence parameters
     */
    public static final String cnParams = "cnParams";
    /**
     * Data flavor of competence classs, used for drag-and-drop
     */
    public static final DataFlavor dataFlavor = new DataFlavor(Competence.class, "competence-node");

    /**
     * Create a new Competence with passed name and assign passed elements to
     * this competence (set parent).
     *
     * @param name Name of competence node, it can be referenced
     * @param elements List of elements that are not part of any other
     * competence. Shallow copy
     * @throws FubarException if names of elements are not unique
     */
    Competence(String name, FormalParameters params, List<CompetenceElement> elements) throws DuplicateNameException {
        this(name, params);

        for (CompetenceElement element : elements) {
            assert element.getParent() == null;
            addElement(element);
        }
    }

    /**
     * Create new competence without {@link CompetenceElement elements}.
     *
     * @param name Name of new C
     * @param params formal parameters of the C
     */
    Competence(String name, FormalParameters params) {
        assert name != null;
        assert params != null;

        this.name = name;
        this.params = new FormalParameters(params);
    }

    /**
     * Add passed element as the last element of this competence and emit.
     *
     * @param choice element that will be added into this competence
     */
    public void addElement(CompetenceElement choice) throws DuplicateNameException {
        int beyondLastElementIndex = elementsUm.size();
        addElement(beyondLastElementIndex, choice);
    }

    /**
     * Add choice as the @index element of all competences choices + emit.
     *
     * @param index Index at which should the choice be added
     * @param choice Choice to add. Orphan.
     * @throws DuplicateNameException
     */
    public void addElement(int index, CompetenceElement choice) throws DuplicateNameException {
        assert !choice.isChildOfParent();

        if (isUsedName(choice.getName(), elementsUm)) {
            throw new DuplicateNameException("Competence " + name + " already has element with name " + choice.getName());
        }

        elements.add(index, choice);
        choice.setParent(this);

        emitChildNode(choice);
    }

    /**
     * Create text representation of this competence, compatible with parser, so
     * we can directly output it.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\t(C ");
        sb.append(name);
        if (!params.isEmpty()) {
            sb.append(" vars(");
            sb.append(params.toString());
            sb.append(')');
        }
        sb.append("\n\t\t(elements");
        for (CompetenceElement element : elements) {
            // In order to utilize compatibility with older plans, use two braces to enclose the element
            sb.append("\n\t\t\t(");
            sb.append(element.toString());
            sb.append(")");
        }
        sb.append("\n\t\t)\n\t)");

        return sb.toString();
    }

    @Override
    public List<CompetenceElement> getChildDataNodes() {
        return elementsUm;
    }

    /**
     * @return All choices of this competence.
     */
    public List<CompetenceElement> getChoices() {
        return elementsUm;
    }
    
    /**
     * Get choice with specified id. Equivalent of {@link #getChoices() }.{@link List#get(int)
     * }.
     *
     * @param choiceId Id of desired choice
     * @return Found choice.
     */
    public CompetenceElement getChoice(int choiceId) {
        return elementsUm.get(choiceId);
    }

    /**
     * Change name of competence node.
     *
     * @param name new name of competence node
     */
    public void setName(String name) throws DuplicateNameException, CycleException, InvalidNameException {
        PoshPlan plan = getRootNode();

        name = name.trim();

        if (!name.matches(IDENT_PATTERN)) {
            throw new InvalidNameException("Name " + name + " is not valid.");
        }

        // Check for duplicity
        if (!this.name.equals(name)) {
            if (plan != null && !plan.isUniqueNodeName(name)) {
                throw new DuplicateNameException("New name for competence '" + this.name + "'(" + name + ") is not unique for reaction plan.");
            }
        }

        String oldName = this.name;
        this.name = name;

        if (plan != null && plan.isCycled()) {
            this.name = oldName;
            throw new CycleException("New name (" + name + ") for competence '" + this.name + "' is causing cycle.");
        }
        firePropertyChange(cnName, oldName, name);
    }

    /**
     * Get name of the competence
     *
     * @return name of the competence
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get HTML description of the competence. Use e.g. in palette of Shed.
     * @return HTML description of competence
     */
    public String getHtmlDescription() {
        return "<html>Competence: " + getName() + "<br/><pre>" + toString() + "</pre></html>";
    }
    
    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        assert child instanceof CompetenceElement;
        return moveChildInList(elements, (CompetenceElement) child, newIndex);
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.COMPETENCE;
    }

    /**
     * Remove the {@link CompetenceElement} from this competence and emit
     * notification about deletion.
     *
     * @param element Element to be removed.
     */
    public void removeElement(CompetenceElement element) {
        assert elements.contains(element);

        if (elements.size() == 1) {
            String unusedName = getUnusedName("choice-", elementsUm);
            try {
                addElement(LapElementsFactory.createCompetenceElement(unusedName));
            } catch (DuplicateNameException ex) {
                String msg = "Unused name " + unusedName + " is not unused.";
                Logger.getLogger(Competence.class.getName()).log(Level.SEVERE, msg, ex);
                throw new FubarException(msg, ex);
            }
        }

        int removedElementPosition = elementsUm.indexOf(element);

        elements.remove(element);
        element.setParent(null);

        emitChildDeleted(element, removedElementPosition);
    }

    /**
     * Get list of formal parametrs of competence (names and default values).
     *
     * @return
     */
    @Override
    public FormalParameters getParameters() {
        return params;
    }

    @Override
    public void setParameters(FormalParameters newParams) {
        FormalParameters oldParams = params;
        this.params = newParams;

        firePropertyChange(cnParams, oldParams, newParams);
    }

    // TODO: Somehow unify with ActionPattern one.
    /**
     * Rename {@link Competence} in the {@link PoshPlan}. This method changes
     * name fo the comeptence, finds all
     * {@link TriggeredAction references} to the competence (the ones with old
     * name) and changed them to @newCompetenceName.
     *
     * @param newCompetenceName New name of comeptence after renaming
     */
    @Override
    public void rename(String newCompetenceName) throws InvalidNameException, CycleException, DuplicateNameException {
        PoshPlan plan = getRootNode();
        if (plan == null) {
            throw new IllegalStateException("Competence " + getName() + " is not part of the plan.");
        }

        List<TriggeredAction> allReferences = plan.getAllReferences();
        List<TriggeredAction> referencingActions = new LinkedList<TriggeredAction>();

        for (TriggeredAction planAction : allReferences) {
            boolean actionReferencesCompetence = planAction.getName().equals(getName());
            if (actionReferencesCompetence) {
                referencingActions.add(planAction);
            }
        }
        // TODO: rollback if some error happens?
        for (TriggeredAction referencingAction : referencingActions) {
            referencingAction.setActionName(newCompetenceName);
        }

        this.setName(newCompetenceName);
    }

    /**
     * Get index of @choice.
     *
     * @param choice Choice for which we are looking for an index.
     * @return Found index
     * @throws IllegalArgumentException If choice is not among choices of this
     * competence.
     */
    public int getChoiceId(CompetenceElement choice) {
        return getElementId(elementsUm, choice);
    }
}
