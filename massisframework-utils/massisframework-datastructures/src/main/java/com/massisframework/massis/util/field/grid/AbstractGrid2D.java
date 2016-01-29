package com.massisframework.massis.util.field.grid;

import com.massisframework.massis.util.field.Field2D;

/**
 * A simple 2D grid
 * 
 * @author rpax
 * 
 * @param <E>
 *            The type of elements that this grid contains
 */
public abstract class AbstractGrid2D<E> extends Field2D<E>{
	
	/**
	 * Discretization factor
	 */
	public final int cellSize;

	public AbstractGrid2D(int minX, int maxX, int minY, int maxY, int cellSize) {
		super(minX, maxX, minY, maxY);
		this.cellSize = cellSize;
	}

	public void set(final int x, final int y, final E val) {
		this.setInGridCoordinates((x - minX) / cellSize,(y - minY) / cellSize,val);
	}

	public E get(final int x, final int y) {
		return this.getInGridCoordinates((x - minX) / cellSize,(y - minY) / cellSize);
	}

	public abstract void setInGridCoordinates(final int gridX, final int gridY,
			final E val);

	public abstract E getInGridCoordinates(final int gridX, final int gridY);

	public abstract void setAllTo(E val);


	public int getCellSize() {
		return cellSize;
	}

}
