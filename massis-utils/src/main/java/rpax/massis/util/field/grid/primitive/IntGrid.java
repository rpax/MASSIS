package rpax.massis.util.field.grid.primitive;

import java.util.Arrays;

import rpax.massis.util.field.grid.AbstractGrid2D;

public class IntGrid extends AbstractGrid2D<Integer>{

	protected final int[/* x */][/* y */] grid;
	public IntGrid(int minX, int maxX, int minY, int maxY, int cellSize) {
		super(minX, maxX, minY, maxY, cellSize);
		this.grid = new int[ x_length/ cellSize][y_length/ cellSize];
	}
	@Override
	public void setInGridCoordinates(int gridX, int gridY, Integer val) {
		this.grid[gridX][gridY]=val;
	}
	
	@Override
	public Integer getInGridCoordinates(int gridX, int gridY) {
		return this.grid[gridX][gridY];
	}
	
	@Override
	public void setAllTo(Integer val) {
		final int v=val;
		for (int i = 0; i < this.grid.length; i++)
		{
			Arrays.fill(this.grid[i], v);
		}
	}
}
