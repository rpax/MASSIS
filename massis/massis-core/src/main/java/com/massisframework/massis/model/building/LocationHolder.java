/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.model.building;

import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.util.geom.CoordinateHolder;

/**
 *
 * @author Rafael Pax
 */
public interface LocationHolder extends CoordinateHolder{

    /**
     *
     * @return the location of this element
     */
    public Location getLocation();
}
