package com.massisframework.massis.util.field.grid.primitive;

import java.util.BitSet;

import com.massisframework.massis.util.field.grid.AbstractGrid2D;

public class BitGrid extends AbstractGrid2D<Boolean> {

	protected final BitSet grid;

	public BitGrid(int minX, int maxX, int minY, int maxY, int cellSize) {
		super(minX, maxX, minY, maxY, cellSize);
		this.grid = new BitSet((this.x_length * this.y_length)/cellSize);
	}

	@Override
	public void setAllTo(Boolean val) {
		this.grid.set(0, this.grid.size(), val);
	}

	@Override
	public void setInGridCoordinates(int gridX, int gridY, Boolean val) {
		this.grid.set(gridX * this.y_length + gridY, val);

	}

	@Override
	public Boolean getInGridCoordinates(int gridX, int gridY) {
		return this.grid.get(gridX * this.y_length + gridY);
	}
	// public boolean lineIntersectsObstacle(int x0, int y0, int x1, int y1,
	// boolean local, boolean includeOrigin, boolean includeEnd) {
	// // if (x0<this.minX || y0<this.minY || x1>this.maxX || y1 > this.maxY)
	// // throw new
	// // Transformamos a local
	// if (!local)
	// {
	// x0 = getGridXCoordinates(x0);
	// x1 = getGridXCoordinates(x1);
	// y0 = getGridYCoordinates(y0);
	// y1 = getGridYCoordinates(y1);
	// //
	// }
	// int dx = Math.abs(x1 - x0);
	// int dy = Math.abs(y1 - y0);
	// int x = x0;
	// int y = y0;
	// int n = 1 + dx + dy;
	// int x_inc = (x1 > x0) ? 1 : -1;
	// int y_inc = (y1 > y0) ? 1 : -1;
	// int error = dx - dy;
	// dx *= 2;
	// dy *= 2;
	//
	// for (; n > 0; --n)
	// {
	// // si hay que tener en cuenta origen y destino
	// if (includeOrigin && x == x0 && y == y0)
	// {
	//
	// }
	// else if (includeEnd && x == x1 && y == y1)
	// {
	//
	// }
	// else if (this.getGridValue(x, y) == OBSTACLE)
	// {
	// return true;
	// }
	//
	// }
	//
	// if (error > 0)
	// {
	// x += x_inc;
	// error -= dy;
	// }
	// else
	// {
	// y += y_inc;
	// error += dx;
	// }
	//
	// return false;
	// }

}
