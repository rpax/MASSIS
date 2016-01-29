package cz.cuni.amis.pogamut.sposh.elements;

import java.util.AbstractList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Trigger of an element. Trigger is basically a collection of senses, when all
 * senses are evaluated to be true, the trigger is true, otherwise trigger is
 * false. Note, that trigger can be empty, i.e. contain no senses.
 * <p/>
 * Things are little more interesting how should trigger behave when it doesn't
 * contain any senses. That depends if trigger is used as a goal or trigger.
 * When goal is true, it means that the element is finished and is no longer
 * necessary to execute it, thus it is false. Trigger is used to determine if
 * some element should be traversed and thus it is true. As you can see, default
 * behavior of trigger is specified in a way that encourages tree traversal
 * (because default goal result is by false, traversal won't stop prematurely
 * and because default trigger result is true, traversal will use the node
 * triggered by the trigger).
 *
 * @author HonzaH
 * @param OWNER type of lap element that owns this trigger
 */
public final class Trigger<OWNER extends PoshElement> extends AbstractList<Sense> {

    /**
     * List of senses of this trigger.
     */
    private final List<Sense> senses = new LinkedList<Sense>();
    /**
     * Unmodifiable list of senses, wrapper of {@link Trigger#senses}.
     */
    private final List<Sense> sensesUm = Collections.unmodifiableList(senses);
    /**
     * Owner of this trigger.
     */
    private final OWNER owner;

    /**
     * Create new trigger.
     *
     * @param owner owner of this trigger = parent node of all senses in the
     * trigger.
     * @param senses list of newly created senses (no parent) for this trigger.
     */
    Trigger(OWNER owner, List<Sense> senses) {
        this.owner = owner;

        for (Sense sense : senses) {
            assert sense.getParent() == null;
            add(sense);
        }
    }

    /**
     * Create empty trigger.
     *
     * @param owner owner of this trigger = parent node of all senses in the
     * trigger.
     */
    Trigger(OWNER owner) {
        this(owner, Collections.<Sense>emptyList());
    }

    /**
     * Serialize the trigger as serialization of its senses surrounded by
     * braces. Example: (sense1 (sense2) (sense3))
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        boolean first = true;

        for (Sense sense : sensesUm) {
            if (first) {
                first = false;
                sb.append(sense.toString());
            } else {
                sb.append(' ');
                sb.append(sense.toString());
            }
        }

        sb.append(')');
        return sb.toString();
    }

    /**
     * Add sense to this trigger and emit news about addition.
     *
     * @param sense sense to be added.
     */
    @Override
    public boolean add(Sense sense) {
        assert !contains(sense);
        assert !sense.isChildOfParent();

        sense.setParent(owner);
        boolean ret = senses.add(sense);

        owner.emitChildNode(sense);
        return ret;
    }

    /**
     * Get sense stored at indexed position in the trigger.
     *
     * @param index index of searched trigger.
     * @return found sense
     */
    @Override
    public Sense get(int index) {
        return senses.get(index);
    }

    /**
     * Insert sense at specified index and owner will emit new child.
     *
     * @param index index where to insert new sense
     * @param newSense sense to insert into the trigger
     */
    @Override
    public void add(int index, Sense newSense) {
        assert !contains(newSense);
        assert !newSense.isChildOfParent();

        newSense.setParent(owner);
        senses.add(index, newSense);

        owner.emitChildNode(newSense);
    }

    /**
     * Remove sense from specified position and the removed sense will emit node
     * deleted.
     *
     * @param index position which sense in the trigger to remove.
     * @return Removed sense
     */
    @Override
    public Sense remove(int index) {
        Sense sense = senses.remove(index);
        sense.setParent(null);
        owner.emitChildDeleted(sense, index);
        return sense;
    }

    /**
     * How many senses are there in the trigger
     *
     * @return Number of senses in the trigger
     */
    @Override
    public int size() {
        return senses.size();
    }

    /**
     * Get unmodifiable wrapper of this trigger.
     *
     * @return unmodifiable collection that is proxy for this trigger
     */
    List<Sense> unmodifiable() {
        return sensesUm;
    }

    /**
     * Move sense so that after move, it is at newIndex + emit event. If
     * original index and new index are same, don't emit event.
     *
     * @param newIndex New index of the sense. Once move is done, sense will be
     * at this index and the rest will somehow manage.
     * @param movedSense Sense that is being moved from its original position to
     * the @index. The sense must be part of this trigger.
     */
    public void moveSense(int newIndex, Sense movedSense) {
        assert sensesUm.contains(movedSense);
        assert movedSense.getTrigger() == this;

        int originalIndex = sensesUm.indexOf(movedSense);

        if (newIndex > originalIndex) {
            Sense removed = senses.remove(originalIndex);
            senses.add(newIndex, movedSense);
        } else if (newIndex < originalIndex) {
            senses.remove(originalIndex);
            senses.add(newIndex, movedSense);
        } else {
            return;
        }
        owner.emitChildMove(movedSense, originalIndex, newIndex);
    }
}
