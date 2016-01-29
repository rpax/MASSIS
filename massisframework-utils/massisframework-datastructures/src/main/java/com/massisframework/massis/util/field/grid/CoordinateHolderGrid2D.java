package com.massisframework.massis.util.field.grid;

import com.massisframework.massis.util.basic.XYHolder;

public class CoordinateHolderGrid2D<T extends XYHolder> extends
		ObjectGrid2D<T> {

	public CoordinateHolderGrid2D(Class<T> clazz, int minX, int maxX, int minY,
			int maxY, int cellSize) {
		super(clazz, minX, maxX, minY, maxY, cellSize);
	}

	public void set(final T elem) {
		this.set((int) elem.getX_2D(), (int) elem.getY_2D(), elem);
	}

}
