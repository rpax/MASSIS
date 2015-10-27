/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.gui;

import java.awt.Graphics2D;

/**
 * Represents a layer in the GUI.
 * @author Rafael Pax
 */
public abstract class DrawableLayer<DW extends DrawableZone> {
    private boolean enabled;
    public abstract void draw(DW drawableZone,Graphics2D g);
    public boolean isEnabled(){return this.enabled;}
    public void setEnabled(boolean enabled){this.enabled=enabled;}
    public abstract String getName();
    
}
