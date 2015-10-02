/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.model.building;

import rpax.massis.model.location.SimLocation;

/**
 *
 * @author Rafael Pax
 */
public interface LocationHolder {

    /**
     *
     * @return the location of this element
     */
    public SimLocation getLocation();
}
