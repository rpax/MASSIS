package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.executor.DoNothing;
import cz.cuni.amis.pogamut.sposh.executor.Succeed;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a factory class that should be used to create various elements. Do
 * not create elements with their constructor, if you can avoid it, this factory
 * will make sure that returned elements are valid, e.g. all parent links in
 * elements are correctly linked and so on.
 *
 * @author HonzaH
 */
public class LapElementsFactory {

    /**
     * Name of action that will be used unless user specifies a name of action
     * that should be used.
     */
    public static String DEFAULT_ACTION = DoNothing.class.getName();
    /**
     * Name of trigger that will be used unless user specifies some other
     * trigger in the arguments.
     */
    public static String DEFAULT_TRIGGER_SENSE = Succeed.class.getName();
    public static String DEFAULT_DRIVE_COLLECTION_NAME = "life";

    /**
     * Create a new competence with multiple choices. The choices have default
     * action and trigger.
     *
     * @param name name of new competence
     * @param choices unique sequence of choices for this competence
     * @return created competence
     */
    public static Competence createCompetence(String name, String... choices) throws DuplicateNameException {
        List<CompetenceElement> elements = new ArrayList<CompetenceElement>(choices.length);

        for (String choice : choices) {
            elements.add(LapElementsFactory.createCompetenceElement(choice));
        }

        return new Competence(name, new FormalParameters(), elements);
    }

    /**
     * Create new drive with default trigger and action.
     *
     * @param driveName name of the drive
     */
    public static DriveElement createDriveElement(String driveName) {
        return new DriveElement(driveName, Arrays.asList(new Sense(DEFAULT_TRIGGER_SENSE)), new PrimitiveCall(DEFAULT_ACTION), Freq.INFINITE, "");
    }

    /**
     * Create new drive with specified name and empty list of triggers and
     * default action.
     *
     * @param name name of created drive element
     */
    public static DriveElement createDriveElementNoTriggers(String name) {
        return new DriveElement(name, Collections.<Sense>emptyList(), new PrimitiveCall(DEFAULT_ACTION), Freq.INFINITE, "");
    }

    /**
     * Create new competence element with no trigger sense and default action.
     *
     * @param name name of competence element
     * @return created element
     */
    public static CompetenceElement createCompetenceElement(String name) {
        return new CompetenceElement(name, Collections.<Sense>emptyList(), new PrimitiveCall(DEFAULT_ACTION), CompetenceElement.INFINITE_RETRIES, "");
    }

    /**
     * Create new sense.
     *
     * @param name name of the sense
     * @return created sense
     */
    public static Sense createSense(String name) {
        return new Sense(name);
    }

    /**
     * Create default action.
     */
    public static TriggeredAction createAction() {
        return new TriggeredAction(DEFAULT_ACTION);
    }

    /**
     * Create action with specified name.
     *
     * @param name Name of the action
     * @return created action
     */
    public static TriggeredAction createAction(String name) {
        return new TriggeredAction(name);
    }

    /**
     * Create action for specified call.
     */
    public static TriggeredAction createAction(PrimitiveCall call) {
        return new TriggeredAction(new PrimitiveCall(call));
    }

    /**
     * Create new action pattern without parameters and one {@link #DEFAULT_ACTION default}
     * action.
     *
     * @param name Name of new action pattern
     * @return created AP
     */
    public static ActionPattern createActionPattern(String name) {
        return new ActionPattern(name, new FormalParameters(), Arrays.asList(new TriggeredAction(DEFAULT_ACTION)), "");
    }

    /**
     * Create new action pattern without parameters and containing passed
     * actions.
     *
     * @param name Name of new action pattern
     * @param actions Sequence of actions that will in the AP
     * @return created AP
     */
    public static ActionPattern createActionPattern(String name, TriggeredAction... actions) {
        return new ActionPattern(name, new FormalParameters(), Arrays.asList(actions), "");
    }

    /**
     * Create empty action pattern with specified name and parameters, but
     * without any actions in it.
     *
     * @param name Name of the AP
     * @return created AP
     */
    public static ActionPattern createActionPattern(String name, FormalParameters params) {
        return new ActionPattern(name, params, Collections.<TriggeredAction>emptyList(), "");
    }

    /**
     * Create action pattern
     *
     * @param name Name of the AP
     * @param params parameters of this AP
     * @param actions actions that will be in the AP
     * @return created AP
     */
    public static ActionPattern createActionPattern(String name, FormalParameters params, List<TriggeredAction> actions) {
        return new ActionPattern(name, params, actions, "");
    }

    /**
     * Create an AP with same structure as passed ap (only structure, no
     * listeners or shared objects).
     *
     * @param ap action pattern to copy
     * @return ap with same structure as the ap.
     */
    public static ActionPattern createActionPattern(ActionPattern ap) {
        // XXX: Proper copy constructor
        String serializedActionPattern = ap.toString();
        try {
            return new PoshParser(new StringReader(serializedActionPattern.substring(serializedActionPattern.indexOf('(') + 1))).actionPattern();
        } catch (ParseException ex) {
            String msg = "Tried to serialize and deserialize action pattern as copy constructor: " + serializedActionPattern;
            Logger.getLogger(LapElementsFactory.class.getName()).log(Level.SEVERE, msg, ex);
            throw new FubarException(msg, ex);
        }
    }

    /**
     * Create a C with same structure as passed c (only structure, no listeners
     * or shared objects).
     *
     * @param c competence to copy
     * @return competence with same structure as the c.
     */
    public static Competence createCompetence(Competence c) {
        // XXX: Proper copy constructor
        String serializedCompetence = c.toString();
        try {
            return new PoshParser(new StringReader(serializedCompetence.substring(serializedCompetence.indexOf('(') + 1))).competence();
        } catch (ParseException ex) {
            String msg = "Tried to serialize and deserialize competence as copy constructor: " + serializedCompetence;
            Logger.getLogger(LapElementsFactory.class.getName()).log(Level.SEVERE, msg, ex);
            throw new FubarException(msg, ex);
        }
    }

    public static DriveElement createDriveElement(DriveElement drive) {
        // XXX: Proper copy constructor
        String serializedDrive = drive.toString();
        try {
            return new PoshParser(new StringReader(serializedDrive)).driveElement(new FormalParameters());
        } catch (ParseException ex) {
            String msg = "Tried to serialize and deserialize drive as copy constructor: " + serializedDrive;
            Logger.getLogger(LapElementsFactory.class.getName()).log(Level.SEVERE, msg, ex);
            throw new FubarException(msg, ex);
        }
    }

    /**
     * Create a copy of sense (only structure, no listeners or shared objects)
     *
     * @param sense
     * @return
     */
    public static Sense createSense(Sense sense) {
        // XXX: Proper copy constructor
        String serializedSense = sense.toString();
        try {
            return new PoshParser(new StringReader(serializedSense)).fullSense(new FormalParameters());
        } catch (ParseException ex) {
            String msg = "Tried to serialize and deserialize sense as copy constructor: " + serializedSense;
            Logger.getLogger(LapElementsFactory.class.getName()).log(Level.SEVERE, msg, ex);
            throw new FubarException(msg, ex);
        }
    }

    /**
     * Create empty drive collection without trigger senses and drives, with
     * blank name.
     *
     * @return
     */
    public static DriveCollection createDriveCollection() {
        // XXX: Current structure of parser requires to create DC before we have name, so put something there.
        return new DriveCollection("placeholder");
    }

    /**
     * Create empty (i.e. no drives) {@link DriveCollection} with specified
     * name.
     *
     * @param driveCollectionName Name of the created {@link DriveCollection}
     * @return Newly created {@link DriveCollection}.
     */
    public static DriveCollection createDriveCollection(String driveCollectionName) {
        return new DriveCollection(driveCollectionName);
    }

    public static CompetenceElement createCompetenceElement(String name, List<Sense> triggerSenses, String actionName) {
        return new CompetenceElement(name, triggerSenses, new PrimitiveCall(actionName), CompetenceElement.INFINITE_RETRIES, "");
    }

    /**
     * Create new drive element
     *
     * @param name Name of the drive element
     * @param triggerSenses list of senses that will be used as a trigger
     * @param actionCall What to call if drive is elected
     * @param freq How frequently can be this drive called (if called more
     * frequently, the calls will not be elegible)
     * @param comment Comment about the element
     * @return Created drive
     */
    public static DriveElement createDriveElement(String name, List<Sense> triggerSenses, PrimitiveCall actionCall, Freq freq, String comment) {
        return new DriveElement(name, triggerSenses, actionCall, freq, comment);
    }

    /**
     * Create choice from the passed drive.
     *
     * @param drive Base for created choice
     * @return Choice created according to the drive, infinite retries.
     */
    public static CompetenceElement createCompetenceElement(DriveElement drive) {
        return new CompetenceElement(
                drive.getName(),
                copySenses(drive.getTrigger()),
                new PrimitiveCall(drive.getAction().getActionCall()),
                CompetenceElement.INFINITE_RETRIES,
                drive.getComment());
    }

    /**
     * Create a copy of a passed choice.
     *
     * @param choice Choice that will be used as original
     * @return created element
     */
    public static CompetenceElement createCompetenceElement(CompetenceElement choice) {
        return new CompetenceElement(
                choice.getName(),
                copySenses(choice.getTrigger()),
                new PrimitiveCall(choice.getAction().getActionCall()),
                choice.getRetries(),
                choice.getComment());
    }
    
    
    /**
     * Create drive based on the choice.
     *
     * @param choice Choice used as base for new drive.
     * @return Drive created from the choice, infinite frequency
     */
    public static DriveElement createDriveElement(CompetenceElement choice) {
        return new DriveElement(
                choice.getName(),
                copySenses(choice.getTrigger()),
                new PrimitiveCall(choice.getAction().getActionCall()),
                Freq.INFINITE,
                choice.getComment());
    }

    /**
     * Create a deep copy of senses in the trigger.
     */
    private static <OWNER extends PoshElement> List<Sense> copySenses(Trigger<OWNER> trigger) {
        List<Sense> senses = new LinkedList<Sense>();
        for (Sense sense : trigger) {
            senses.add(createSense(sense));
        }
        return senses;
    }

    /**
     * Create new plan with DC
     *
     * @param dcName name of the DC
     * @return created plan
     */
    public static PoshPlan createPlan(String dcName) {
        return new PoshPlan(dcName);
    }

    /**
     * Create new plan with empty {@link DriveCollection} that has {@link #DEFAULT_DRIVE_COLLECTION_NAME}
     * name.
     */
    static PoshPlan createPlan() {
        return createPlan(DEFAULT_DRIVE_COLLECTION_NAME);
    }

    /**
     * Copy method for an action. Parent is not copied, only the data of the
     * element.
     *
     * @param original
     * @return
     */
    public static TriggeredAction createAction(TriggeredAction original) {
        return new TriggeredAction(new PrimitiveCall(original.getActionCall()));
    }
}
