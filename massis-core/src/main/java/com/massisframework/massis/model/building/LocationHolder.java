/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.model.building;

import com.massisframework.massis.model.location.SimLocation;

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
