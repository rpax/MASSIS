package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.MissingParameterException;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Adopt element is a stopgap. When engine requires to clear the stack, stack
 * will be cleared only up to first adopt with failed exit condition.
 *
 * @author Jimmy
 * @author Honza
 */
public class Adopt extends PoshDummyElement<Adopt, PoshPlan> implements IParametrizedElement, IConditionElement<Adopt> {

    /**
     * Data flavor of adopt class, used for drag-and-drop
     */
    public static final DataFlavor dataFlavor = new DataFlavor(Adopt.class, "adopt-node");
    /**
     * Property string of competence name
     */
    public static final String adName = "adName";
    /**
     * Property string used for {@link PropertyChangeEvent} when {@link FormalParameters}
     * are changed.
     */
    public static final String adParams = "adParams";
    private String name;
    private FormalParameters parameters;
    private Trigger<Adopt> exitCondition;
    private final TriggeredAction adoptedElement;

    public Adopt(String name, FormalParameters parameters, List<Sense> exitCondition, PrimitiveCall adoptedElement) {
        this.name = name;
        this.parameters = parameters;

        this.exitCondition = new Trigger<Adopt>(this, exitCondition);

        this.adoptedElement = LapElementsFactory.createAction(adoptedElement);
        this.adoptedElement.setParent(this);
    }

    @Override
    public FormalParameters getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(FormalParameters newParams) {
        FormalParameters oldParams = this.parameters;
        this.parameters = newParams;
        firePropertyChange(adParams, oldParams, newParams);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidNameException, DuplicateNameException, CycleException {
        PoshPlan plan = getRootNode();

        name = name.trim();

        if (!name.matches(IDENT_PATTERN)) {
            throw new InvalidNameException("Name " + name + " is not valid.");
        }

        // Check for duplicity
        if (!this.name.equals(name)) {
            if (plan != null && !plan.isUniqueNodeName(name)) {
                throw new DuplicateNameException("New name for adopt '" + this.name + "'(" + name + ") is not unique for reaction plan.");
            }
        }

        String oldName = this.name;
        this.name = name;

        if (plan != null && plan.isCycled()) {
            this.name = oldName;
            throw new CycleException("New name (" + name + ") for adopt '" + this.name + "' is causing cycle.");
        }
        firePropertyChange(adName, oldName, name);
    }


    @Override
    public void rename(String newName) throws InvalidNameException, CycleException, DuplicateNameException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void addCondition(Sense sense) {
        exitCondition.add(sense);
    }

    public Trigger<Adopt> getExitCondition() {
        return exitCondition;
    }

    /**
     * Get exit condition of the adopt.
     *
     * @see #getExitCondition()
     * @return Exit condition
     */
    @Override
    public Trigger<Adopt> getCondition() {
        return getExitCondition();
    }

    public TriggeredAction getAdoptedElement() {
        return adoptedElement;
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.ADOPT;
    }

    @Override
    public List<? extends PoshElement> getChildDataNodes() {
        List<PoshElement> children = new ArrayList<PoshElement>();
        children.addAll(exitCondition);
        children.add(this.adoptedElement);

        return children;
    }

    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        throw new UnsupportedOperationException("Moving Adopt doesn't make sense.");
    }
}
