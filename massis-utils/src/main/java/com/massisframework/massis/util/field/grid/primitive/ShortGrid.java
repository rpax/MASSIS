package com.massisframework.massis.util.field.grid.primitive;

import java.util.Arrays;

import com.massisframework.massis.util.field.grid.AbstractGrid2D;

public class ShortGrid extends AbstractGrid2D<Short> {

	protected final short[/* x */][/* y */] grid;

	public ShortGrid(int minX, int maxX, int minY, int maxY, int cellSize) {
		super(minX, maxX, minY, maxY, cellSize);
		this.grid = new short[x_length / cellSize][y_length / cellSize];
	}

	@Override
	public void setInGridCoordinates(int gridX, int gridY, Short val) {
		this.grid[gridX][gridY] = val;
	}

	@Override
	public Short getInGridCoordinates(int gridX, int gridY) {
		return this.get(gridX, gridY);
	}

	@Override
	public void setAllTo(Short val) {
		final short v = val;
		for (int i = 0; i < this.grid.length; i++)
		{
			Arrays.fill(this.grid[i], v);
		}
	}
}
