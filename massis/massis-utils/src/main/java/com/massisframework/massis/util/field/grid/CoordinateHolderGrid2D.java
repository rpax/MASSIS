package com.massisframework.massis.util.field.grid;

import com.massisframework.massis.util.geom.CoordinateHolder;

public class CoordinateHolderGrid2D<T extends CoordinateHolder> extends
		ObjectGrid2D<T> {

	public CoordinateHolderGrid2D(Class<T> clazz, int minX, int maxX, int minY,
			int maxY, int cellSize) {
		super(clazz, minX, maxX, minY, maxY, cellSize);
	}

	public void set(final T elem) {
		this.set((int) elem.getX(), (int) elem.getY(), elem);
	}

}
