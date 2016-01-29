package com.massisframework.massis.util.field.grid.primitive;

import java.util.Arrays;

import com.massisframework.massis.util.field.grid.AbstractGrid2D;

public  class FloatGrid extends AbstractGrid2D<Float>{

	protected final float[/* x */][/* y */] grid;
	public FloatGrid(int minX, int maxX, int minY, int maxY, int cellSize) {
		super(minX, maxX, minY, maxY, cellSize);
		this.grid = new float[ x_length/ cellSize][y_length/ cellSize];
	}
	@Override
	public void setInGridCoordinates(int gridX, int gridY, Float val) {
		this.grid[gridX][gridY]=val;
	}
	
	@Override
	public Float getInGridCoordinates(int gridX, int gridY) {
		return this.grid[gridX][gridY];
	}
	
	@Override
	public void setAllTo(Float val) {
		final float v=val;
		for (int i = 0; i < this.grid.length; i++)
		{
			Arrays.fill(this.grid[i], v);
		}
	}
}
