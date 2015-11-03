package com.massisframework.massis.displays.floormap.layers;

import java.awt.Graphics2D;

import com.massisframework.massis.model.building.Floor;

/**
 * Represents a layer of the 2D display.
 *
 * @author rpax
 *
 */
public abstract class FloorMapLayer {

    private boolean enabled = true;

    /**
     * Main constructor.
     *
     * @param enabled if this layer should be enabled or not. For enabling or
     * disabling a layer the method {@link #setEnabled(boolean)} should be used.
     */
    public FloorMapLayer(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Draws the this layer if it is enabled. Reduces checks outside this class.
     *
     * @param f the floor to be drawn
     * @param g the graphics object where this layer will be drawn
     */
    public void drawIfEnabled(Floor f, Graphics2D g)
    {
        if (enabled)
        {
            draw(f, g);
        }
    }

    /**
     * Draws this layer on top of a graphics object
     *
     * @param f the floor to be drawn
     * @param g the graphics object where this layer will be drawn
     */
    protected abstract void draw(Floor f, Graphics2D g);

    /**
     *
     * @return the name of this layer. It is intended for displaying its name in
     * a GUI.
     */
    public abstract String getName();

    /**
     *
     * @return if this layer is enabled or not
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Activates /Deactivates this layer
     *
     * @param enabled if this layer should be enabled or disabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
