package rpax.massis.util.field;

public abstract class Field2D<E> {
	/**
	 * Minimum x value
	 */
	public final int minX;
	/**
	 * Maximum x value
	 */
	public final int maxX;
	/**
	 * Minimum Y value
	 */
	public final int minY;
	/**
	 * Maximum Y value
	 */
	public final int maxY;
	/**
	 * X length (maxX - minX)
	 */
	public final int x_length;
	/**
	 * (maxY - minY)
	 */
	public final int y_length;

	public Field2D(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.x_length = (this.maxX - this.minX);
		this.y_length = (this.maxY - this.minY);
	}
}
