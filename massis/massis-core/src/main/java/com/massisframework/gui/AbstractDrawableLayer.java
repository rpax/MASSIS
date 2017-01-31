/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.gui;

import java.awt.Graphics2D;

/**
 * Represents a layer in the GUI.
 * 
 * @author Rafael Pax
 */
public abstract class AbstractDrawableLayer
		implements DrawableLayer<EngineDrawableZone> {

	private boolean enabled;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.gui.DrawableLayer#draw(DW, java.awt.Graphics2D)
	 */
	@Override
	public abstract void draw(EngineDrawableZone drawableZone, Graphics2D g);


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.gui.DrawableLayer#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.gui.DrawableLayer#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.gui.DrawableLayer#getName()
	 */
	@Override
	public abstract String getName();

}
