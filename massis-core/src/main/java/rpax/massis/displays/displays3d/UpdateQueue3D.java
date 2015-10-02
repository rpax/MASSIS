package rpax.massis.displays.displays3d;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.eteks.sweethome3d.j3d.Object3DBranch;
import com.eteks.sweethome3d.model.Selectable;

/**
 * A threaded update queue for accelerating the 3D view of SweetHome3D. It is
 * useful <strong>ONLY</strong> for simulation purposes.
 *
 * @author rpax
 *
 */
public class UpdateQueue3D {

    /**
     * The object's queue
     */
    private final Queue<Selectable> queue = new LinkedList<>();
    /**
     * If an element is already in the queue, it should not be added again.This
     * set is used in order to make that check faster.
     */
    private final HashSet<Selectable> objectsInQueue = new HashSet<>();
    /**
     * Map linking the SweetHome3D object with its corresponding 3D branch
     */
    private final Map<Selectable, Object3DBranch> homeObjects;

    /**
     * Default constructor.
     *
     * @param homeObjects the objects in the simulation
     */
    public UpdateQueue3D(Map<Selectable, Object3DBranch> homeObjects)
    {
        this.homeObjects = homeObjects;
    }

    /**
     *
     * @return if this update queue is empty or not
     */
    public synchronized boolean isEmpty()
    {
        return this.queue.isEmpty();
    }

    /**
     * Enqueues an element to be updated later
     *
     * @param obj the element to update
     */
    public synchronized void addElement(Selectable obj)
    {
        addElem(obj);
    }

    /**
     * Enqueues a collection of objects to this update queue
     *
     * @param objects the objects to be updated
     */
    public synchronized void addAll(Collection<? extends Selectable> objects)
    {
        for (Selectable obj : objects)
        {
            addElem(obj);
        }
    }

    /**
     * Updates the head of the queue and removes it.
     *
     * @return if the queue was empty.
     */
    public synchronized boolean updateHead()
    {
        if (this.queue.isEmpty())
        {
            return false;
        }
        updateNextElem();
        return true;
    }

    /**
     * Updates every element until this queue it is empty
     */
    public synchronized void updateAll()
    {
        while (!this.queue.isEmpty())
        {
            updateNextElem();
        }
    }

    /**
     * Updates the next element of this queue.
     */
    private void updateNextElem()
    {

        Selectable object = queue.poll();
        Object3DBranch objectBranch = homeObjects.get(object);
        // Check object wasn't deleted since updateObjects call
        if (objectBranch != null)
        {
            homeObjects.get(object).update();
        }
        this.objectsInQueue.remove(object);

    }

    private void addElem(Selectable obj)
    {
        if (!this.objectsInQueue.contains(obj))
        {
            this.queue.add(obj);
            this.objectsInQueue.add(obj);
        } else
        {
            System.err.println("Already in queue");
        }
    }
}
