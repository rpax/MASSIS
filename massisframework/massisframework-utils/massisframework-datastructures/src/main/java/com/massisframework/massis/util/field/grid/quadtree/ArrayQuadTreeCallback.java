package com.massisframework.massis.util.field.grid.quadtree;


public interface ArrayQuadTreeCallback<E> {

	public void query(E element);
	public boolean shouldStop();
}
