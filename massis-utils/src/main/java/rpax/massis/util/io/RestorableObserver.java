/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.util.io;

/**
 *
 * @author Rafael Pax
 */
public interface RestorableObserver {
    public void notifyChange(Restorable restorable,JsonState state);
}
