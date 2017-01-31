package com.massisframework.gui;

import java.awt.Graphics2D;

public interface DrawableLayer<DW extends DrawableZone> {

	void draw(DW drawableZone, Graphics2D g);

	boolean isEnabled();

	void setEnabled(boolean enabled);

	String getName();

}