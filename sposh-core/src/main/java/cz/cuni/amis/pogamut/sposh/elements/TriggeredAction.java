package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.awt.datatransfer.DataFlavor;
import java.util.Collections;
import java.util.List;

/**
 * Triggered action is basically a call of some other entity that is supposed to
 * be executed. The referenced element can be action (through {@link IWorkExecutor}), {@link Competence}
 * or {@link ActionPattern}. The call needs name of a callee and possibly some
 * arguments to pass.
 * <p/>
 * How is the target determined? Engine takes the name and checks if there is a {@link Competence}
 * or {@link ActionPattern} with same name (names of all C and AP are unique,
 * otherwise they get {@link DuplicateNameException}). If there is, the action
 * will execute the found C/AP. If there is no such C/AP, it assumes that is is
 * a name of an action so it tries to use {@link IWorkExecutor}. If that fails,
 * stop the bot.
 * <p/>
 * Note that we do not allow cycles (thx to {@link CycleException}).
 *
 * @author HonzaH
 */
public class TriggeredAction extends PoshDummyElement implements IReferenceElement, INamedElement {

    private PrimitiveCall actionCall;
    public static final DataFlavor dataFlavor = new DataFlavor(TriggeredAction.class, "triggered_action");
    public static final String taName = "taName";
    public static final String taArgs = "taAruments";

    TriggeredAction(String actionName) {
        this.actionCall = new PrimitiveCall(actionName);
    }

    TriggeredAction(PrimitiveCall actionCall) {
        this.actionCall = actionCall;
    }

    @Override
    public List<PoshElement> getChildDataNodes() {
        return Collections.<PoshElement>emptyList();
    }

    @Override
    public String toString() {
        return actionCall.toString();
    }

    @Override
    public String getName() {
        return actionCall.getName();
    }

    public PrimitiveCall getActionCall() {
        return actionCall;
    }

    /**
     * Set name of the action.
     *
     * @param newName New name of an action
     * @throws InvalidNameException If name is not valid
     * @throws CycleException if namechange would cause a cycle.
     */
    public void setActionName(String newName) throws InvalidNameException, CycleException {
        newName = newName.trim();

        if (!newName.matches(IDENT_PATTERN)) {
            throw InvalidNameException.create(newName);
        }

        String oldName = this.getName();
        this.actionCall = new PrimitiveCall(newName, actionCall.getParameters());

        if (getRootNode() != null && getRootNode().isCycled()) {
            this.actionCall = new PrimitiveCall(oldName, actionCall.getParameters());
            throw CycleException.createFromName(newName);
        }
        firePropertyChange(taName, oldName, newName);
    }

    /**
     * XXX: Not implemented, because I don't have a usecase, but interface has it.
     * 
     * Finds all actions in the plan with the same name as this sense and changes
     * their name to newName. } + fire property.
     * 
     * @param newName What will be new name of all actions with old name.
     */
    @Override
    public void rename(String newName) throws InvalidNameException, CycleException, DuplicateNameException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.ACTION;
    }
    
    /**
     * Take @source action and set all properties (name, parameters...) to be
     * same as the @source. All changes will be emitted.
     *
     * @param source The source of the data this action will synchronize to.
     */
    public void synchronize(TriggeredAction source) throws CycleException {
        try {
            this.setActionName(source.getName());
            Arguments sourceArguments = source.getActionCall().getParameters();
            setArguments(new Arguments(sourceArguments));
        } catch (InvalidNameException ex) {
            throw new FubarException("Name of the source action \"" + source.getName() + "\" is not valid for this action.", ex);
        }
    }
    
    /**
     * Action basically consists from
     */
    public void setArguments(Arguments newArguments) {
        String actionName = actionCall.getName();
        Arguments oldArguments = actionCall.getParameters();
        this.actionCall = new PrimitiveCall(actionName, newArguments);

        // TODO: Check that all variables used as values in the newArguments are present in the parameters of parent, use getParentParameters().
        
        firePropertyChange(taArgs, oldArguments, newArguments);
    }

    /**
     * Get arguments of this action.
     *
     * @return Arguments of the action.
     */
    @Override
    public Arguments getArguments() {
        return this.actionCall.getParameters();
    }
}
