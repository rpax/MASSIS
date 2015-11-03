/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.util.io;

/**
 *
 * @author Rafael Pax
 */
public interface RestorableObserver {
    public void notifyChange(Restorable restorable,JsonState state);
}
