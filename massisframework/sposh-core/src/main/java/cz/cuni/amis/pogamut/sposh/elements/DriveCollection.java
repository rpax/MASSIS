package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import java.util.ArrayList;
import java.util.List;
import java.awt.datatransfer.DataFlavor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is root of POSH plan in execution sense. In source of POSH plan, this is
 * leaf of root. Every POSH plan can have only one DriveCollection.
 * <p/>
 * drive collection: this is the root of the POSH hierarchy, and on every POSH
 * action-selection cycle, this reconsiders which goal the agent should be
 * working on. This is how a BOD agent can respond rapidly to changes in the
 * environment.
 * <p/>
 * <b>How does it work?</b> Fact: Engine remembers which node of the drive it
 * processed last time. When engine evaluates the drive, it starts evaluation in
 * the place, where it left off in the last evaluation of drive. Engine
 * evaluates the plan each tick, but it doesn't matter if drive was evaluated in
 * previsous tick or 10000 ticks ago (e.g. trigger of the drive was unsatisfied
 * for a long time), engine will take the state (state=path from the drive to
 * some node in the drive tree) it was left off last time and goes from there,
 * e.g. if drive was evaluating which action it should do, it will continue to
 * do so, if drive was executing some action, it will continue in its execution.
 * <p/>
 * By "will continue in..." is meant "will try to continue...", because in many
 * cases, it will fail(the action will signal its failure ect.), but it doesn't
 * matter, in such case the engine will traverse the drive's decision tree from
 * the root. Originally it was supposed to enable quick switch to some action
 * and go back to doing whatever the original drive was doing. Not sure about
 * how useful it is.
 *
 * @see Competence Competences are similar, but slightly different. There is a
 * lot of confusion about how they differ from DC, so take a look.
 * @author HonzaH
 */
public final class DriveCollection extends PoshDummyElement<DriveCollection, PoshPlan> implements INamedElement, IConditionElement<DriveCollection> {

    /**
     * Name of the collection
     */
    private String name;
    /**
     * Goal of drive collection. If goal is not fulfilled, run the engine.
     * Useful when there are multiple plans (e.g. one for shooting and one for
     * moving), goal of the shooting plan would be "no-enemy-in-sight" so when
     * enemy would be in sight, it would work, but w/o enemy, it would be
     * dormant. In most plans, goal is "fail" so plan is evaluated indefinitely.
     */
    private final Trigger<DriveCollection> goal = new Trigger<DriveCollection>(this);
    /**
     * List of drive elements of this DC. Basically one choice at the top of
     * decision tree what to do next.
     */
    private final List<DriveElement> elements = new LinkedList<DriveElement>();
    /**
     * Unmodifiable list of drive elements of this DC, basically a wrapper for {@link DriveCollection#elements}.
     */
    private final List<DriveElement> elementsUm = Collections.unmodifiableList(elements);
    /**
     * Property name for change of name.
     */
    public static final String dcName = "dcName";
    /**
     * Data flavor of DC, used for drag and drop
     */
    public static final DataFlavor dataFlavor = new DataFlavor(DriveCollection.class, "drive-collection-node");

    /**
     * Create new drive collection without any trigger or drives, specify only
     * name.
     *
     * @param name Name of the drive collection.
     */
    DriveCollection(String name) {
        assert name != null && !name.isEmpty();
        this.name = name;
    }

    /**
     * Add passed drive at the specified index of all drives.
     */
    /**
     * Add passed drive as the last drive of this DC and emit new it.
     *
     * @param drive drive to add
     */
    public void addDrive(DriveElement drive) throws DuplicateNameException {
        int behindLastDriveIndex = elementsUm.size();
        addDrive(behindLastDriveIndex, drive);
    }

    public void addDrive(int index, DriveElement drive) throws DuplicateNameException {
        assert !elementsUm.contains(drive);
        // sanity: check that new drive's parent doesn't have drive listed as a child
        if (drive.getParent() != null) {
            assert !drive.getParent().getChildDataNodes().contains(drive);
        }

        if (isUsedName(drive.getName(), elements)) {
            throw new DuplicateNameException("DC " + name + " already have drive with name " + drive.getName());
        }

        elements.add(index, drive);
        drive.setParent(this);

        emitChildNode(drive);
    }

    /**
     * Get goal of the DC. As long as goal is not satisfied, the DC will go on.
     *
     * @return goal of the DC
     */
    public Trigger<DriveCollection> getGoal() {
        return goal;
    }

    /**
     * Get goal of the DC.
     *
     * @see #getGoal()
     * @return Goal, never null.
     */
    @Override
    public Trigger<DriveCollection> getCondition() {
        return getGoal();
    }

    /**
     * Get list of all drives of this DC in correct order (drive with higest
     * priority is first, drive with lowest priority is last).
     *
     * @return unmodifiable list of drives.
     */
    public List<DriveElement> getDrives() {
        return elementsUm;
    }

    /**
     * Get drive with @id, equivalent of {@link #getDrives() }.{@link List#get(int)
     * }.
     *
     * @return Drive with @id
     */
    public DriveElement getDrive(int id) {
        return elementsUm.get(id);
    }

    /**
     * Serialize DC into a parser readable form. If goal is empty, don't include
     * it. Example:
     * <code>
     *   (DC funbot (goal ((cz.cuni.HaveFun)(cz.cuni.Sing)))
     *      (drives (
     *                (fun (trigger ((cz.cuni.IsAtParty)(cz.cuni.FriendsInSight)) ) cz.cuni.Enjoy )
     *                (default cz.cuni.DoNothing)
     *              )
     *      )
     *   )
     * </code>
     *
     * @return multi-line string that parser can read.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t(DC ").append(name);

        if (!goal.isEmpty()) {
            sb.append(" (goal ").append(goal.toString()).append(')');
        }
        sb.append('\n');

        sb.append("\t\t(drives \n");
        for (DriveElement element : elements) {
            // Keep the extra braces for compatibility
            sb.append("\t\t\t  (").append(element.toString()).append(")\n");
        }
        sb.append("\t\t)\n");
        sb.append("\t)");
        return sb.toString();
    }

    /**
     * Get all child nodes of the DC. It consists of goal (at first place) and
     * all drives of DC from second place forward (in correct order).
     *
     * @return all children of this DC,
     */
    @Override
    public List<PoshElement> getChildDataNodes() {
        List<PoshElement> children = new ArrayList<PoshElement>(goal);
        children.addAll(elementsUm);

        return children;
    }

    /**
     * Get name of the DC
     *
     * @return name of the DC
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Change name of the DC and notify property listeners.
     *
     * @param newName New name of the DC.
     * @throws InvalidNameException throw if name is not valid (spaces,
     * braces...)
     */
    public void setName(String newName) throws InvalidNameException {
        newName = newName.trim();
        if (newName.matches(IDENT_PATTERN)) {
            String oldName = name;
            name = newName;
            firePropertyChange(dcName, oldName, name);
        } else {
            throw new InvalidNameException("Name " + newName + " is not valid.");
        }
    }

    /**
     * Because DC is not referenced anywhere, it has same effect as {@link #setName(java.lang.String)
     * }.
     *
     * @see #setName(java.lang.String)
     */
    @Override
    public void rename(String newName) throws InvalidNameException, CycleException, DuplicateNameException {
        setName(newName);
    }

    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        assert child instanceof DriveElement;
        return moveChildInList(elements, (DriveElement) child, newIndex);
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.DRIVE_COLLECTION;
    }

    /**
     * Remove drive from the drive collection. If it is the last drive of DC,
     * create new one before removing passed drive in order to have at least one
     * drive in DC at all times.
     *
     * @param drive Drive to be removed.
     */
    public void removeDrive(DriveElement drive) {
        assert elements.contains(drive);

        if (elements.size() == 1) {
            String unusedName = getUnusedName("drive-", elementsUm);
            try {
                addDrive(LapElementsFactory.createDriveElement(unusedName));
            } catch (DuplicateNameException ex) {
                String msg = "Unused name " + unusedName + " is not unused.";
                Logger.getLogger(DriveCollection.class.getName()).log(Level.SEVERE, msg, ex);
                throw new FubarException(msg, ex);
            }
        }

        int removedDrivePosition = elementsUm.indexOf(drive);

        elements.remove(drive);
        drive.setParent(null);

        emitChildDeleted(drive, removedDrivePosition);
    }
}
