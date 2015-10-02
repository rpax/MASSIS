package rpax.massis.util.field.grid;

import java.lang.reflect.Array;

public class ObjectGrid2D<T> extends AbstractGrid2D<T> {

	private final T[][] grid;

	@SuppressWarnings("unchecked")
	public ObjectGrid2D(Class<T> clazz, int minX, int maxX, int minY, int maxY,
			int cellSize) {
		super(minX, maxX, minY, maxY, cellSize);

		this.grid = (T[][]) Array.newInstance(clazz, this.x_length,
				this.y_length);

	}

	@Override
	public void setInGridCoordinates(int gridX, int gridY, T val) {
		this.grid[gridX][gridY] = val;
	}

	@Override
	public T getInGridCoordinates(int gridX, int gridY) {
		return this.grid[gridX][gridY];
	}

	@Override
	public void setAllTo(T val) {
		for (int i = 0; i < this.grid.length; i++)
		{
			for (int j = 0; j < this.grid[i].length; j++)
			{
				grid[j][i] = val;
			}
		}
	}

}
