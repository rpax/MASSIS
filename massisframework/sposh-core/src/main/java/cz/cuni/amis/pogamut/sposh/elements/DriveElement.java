package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;

/**
 * Drive is a child of {@link DriveCollection}. What is drive? Basically it is a
 * top node of decision tree. It has a trigger and an action. When trigger is
 * satisfied, the drive is elegible to be traversed. Action is just a name, it
 * can be reference (=having same name) to some action, but it can also be
 * reference to C or AP.
 * <p/>
 * In posh it has following syntax(minus variables):
 * <code>
 *   (&lt;name&gt; [&lt;trigger&gt;] &lt;name&gt;)
 * </code> The first name is name of the element and second name is name of the
 * action.
 * <p/>
 * In the past, it used to have this form, but not anymore (none knew how to use
 * it and no need to waste scarce resources to support unused features.)
 * <code>
 *   (&lt;name&gt; [&lt;trigger&gt;] &lt;name&gt; [&lt;freq&gt;] [&lt;comment&gt;])
 * </code> The drive is elegible for execution if it has satisfied trigger and
 * it was called less frequently that specified by its frequency ({@link DriveElement#getFreq()
 * }).
 *
 * @see DriveCollection Useful to have an idea what is {@link DriveElement} for.
 * @author HonzaH
 */
public final class DriveElement extends PoshDummyElement<DriveElement, DriveCollection> implements INamedElement, IConditionElement<DriveElement> {

    /**
     * Name of the drive. Unique in the DC.
     */
    private String name;
    /**
     * Trigger of this drive.
     */
    private final Trigger<DriveElement> trigger = new Trigger<DriveElement>(this);
    /**
     * Action of this drive.
     */
    private final TriggeredAction action;
    /**
     * If drive is called more frequenctly than specified by this frequency, it
     * is not elegible to be processed during evaluation.
     *
     * @deprecated XXX: Not used in editor, maybe remove later
     */
    @Deprecated
    private Freq freq;
    /**
     * Comment about this drive element.
     *
     * @deprecated XXX: Not used in editor, maybe remove later
     */
    @Deprecated
    private String comment;
    /**
     * Property name for name of a drive
     */
    public static final String deName = "deName";
    /**
     * Data flavor of drive, used in drag-and-drop
     */
    public static final DataFlavor dataFlavor = new DataFlavor(DriveElement.class, "drive-element");

    /**
     * Create a new DriveElement
     *
     * @param name name of drive element
     * @param triggerSenses Senses that will form the trigger.
     * @param actionCall action that will be called if driver is eligible and
     * its trigger is satisfied.
     */
    DriveElement(String name, List<Sense> triggerSenses, PrimitiveCall actionCall, Freq freq, String comment) {
        assert name != null;
        this.name = name;

        assert freq != null;
        this.freq = freq;

        assert comment != null;
        this.comment = comment;

        for (Sense triggerSense : triggerSenses) {
            assert !triggerSense.isChildOfParent();
            trigger.add(triggerSense);
        }

        this.action = LapElementsFactory.createAction(actionCall);
        this.action.setParent(this);
    }

    /**
     * Get trigger of this drive.
     */
    public Trigger<DriveElement> getTrigger() {
        return trigger;
    }

    /**
     * Get trigger of the element.
     *
     * @see #getTrigger()
     * @return Trigger of element, never null.
     */
    @Override
    public Trigger<DriveElement> getCondition() {
        return getTrigger();
    }

    /**
     * Get action of this drive (will be performed if elegible, has satisfied
     * trigger and highest priority).
     *
     * @return action of this drive
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
        sb.append(this.action.toString());
        sb.append(')');

        return sb.toString();
    }

    @Override
    public List<PoshElement> getChildDataNodes() {
        List<PoshElement> children = new ArrayList<PoshElement>();
        children.addAll(trigger);
        children.add(this.action);

        return children;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set name of the drive to new name.
     *
     * @param newName New name of the drive.
     * @throws InvalidNameException If name doesn't match {@link PoshDummyElement#IDENT_PATTERN}
     * regexp.
     */
    public void setName(String newName) throws InvalidNameException, DuplicateNameException {
        newName = newName.trim();
        if (!newName.matches(IDENT_PATTERN)) {
            throw InvalidNameException.create(newName);
        }
        DriveCollection dc = getParent();
        for (DriveElement drive : dc.getDrives()) {
            if (drive.getName().equals(newName) && drive != this) {
                throw new DuplicateNameException("There already is a drive with name " + newName);
            }
        }


        String oldName = this.name;
        this.name = newName;

        firePropertyChange(deName, oldName, newName);
    }

    /**
     * Becasue name of a drive is not used as reference in the plan, only call {@link #setName(java.lang.String)
     * }.
     *
     * @param newName New name of drive.
     */
    @Override
    public void rename(String newName) throws InvalidNameException, CycleException, DuplicateNameException {
        setName(newName);
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
        return LapType.DRIVE_ELEMENT;
    }

    /**
     * Get how frequent can this drive be executed. Basically {@link PoshEngine}
     * in each evaluation selects the drive with highest priority that has
     * satisfied trigger and is being called less frequently, than specified by
     * the frequency of the drive element.
     *
     * @return maximal frequency with which this drive can be called.
     * @deprecated Part of original posh, not supported in editor.
     */
    @Deprecated
    public Freq getFreq() {
        return freq;
    }

    /**
     * Get comment of this drive.
     *
     * @return comment of the drive
     * @deprecated Not supported in editor
     */
    @Deprecated
    public String getComment() {
        return comment;
    }
}
