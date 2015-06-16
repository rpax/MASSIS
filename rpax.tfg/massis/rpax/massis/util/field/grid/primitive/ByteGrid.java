package rpax.massis.util.field.grid.primitive;

import java.util.Arrays;

import rpax.massis.util.field.grid.AbstractGrid2D;

public class ByteGrid extends AbstractGrid2D<Byte>{
	
	protected final byte[/* x */][/* y */] grid;
	public ByteGrid(int minX, int maxX, int minY, int maxY, int cellSize) {
		super(minX, maxX, minY, maxY, cellSize);
		this.grid = new byte[ x_length/ cellSize][y_length/ cellSize];
	}
	@Override
	public void setInGridCoordinates(int gridX, int gridY, Byte val) {
		this.grid[gridX][gridY]=val;
	}
	public void setInGridCoordinates(int gridX, int gridY, byte val) {
		this.grid[gridX][gridY]=val;
	}
	@Override
	public Byte getInGridCoordinates(int gridX, int gridY) {
		return this.grid[gridX][gridY];
	}
	
	@Override
	public void setAllTo(Byte val) {
		final byte v=val;
		for (int i = 0; i < this.grid.length; i++)
		{
			Arrays.fill(this.grid[i], v);
		}
	}
	
	
	

	

	
}
