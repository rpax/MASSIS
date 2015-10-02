package rpax.massis.util.field.grid.quadtree.array;

public interface ArrayQuadTreeCallback<E> {

	public void query(E element);
	public boolean shouldStop();
}
