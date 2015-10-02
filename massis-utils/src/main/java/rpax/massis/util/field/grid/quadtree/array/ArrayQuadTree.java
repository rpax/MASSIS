package rpax.massis.util.field.grid.quadtree.array;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import rpax.massis.util.Indexable;
import rpax.massis.util.field.Field2D;
import rpax.massis.util.geom.CoordinateHolder;
import rpax.massis.util.geom.KVector;
import straightedge.geom.AABB;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public class ArrayQuadTree<E extends CoordinateHolder> extends Field2D<E> {

	// private static final int UMASK = Short.MAX_VALUE * 2 + 1;

	/**
	 * <pre>
	 *                                                                                   Parent nodes
	 *                                                                                           +    
	 * +         +---+                               +---+ <-------------------------------------+    
	 * |         | 0 +-----------------------------^ | 0 |                                       |    
	 * |         +---+                         +-----+-+-+-+---+ <-------------------------------+    
	 * |         | 1 +-----------------------^ | 0 | 1 | 2 | 3 |                                 |    
	 * |         +---+                 +---------------------------+---+ <-----------------------+    
	 * |         | 2 +---------------^ | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |                              
	 * |         +---+ +---------------------------------------------------+---+---+---+              
	 * v nLevels | 3 +^+ 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | A | B | C | D | E | F |              
	 *           +---+ +-+-+---+-+-+---+---+---+---+---+---+---+---+---+---+---+---+---+              
	 *                   |       |                                                                    
	 *                  +-+     +-+   +                                                               
	 *                  +-+     +-+   |                                                               
	 *                   |       |    |                                                               
	 *                  +-+     +-+   |                                                               
	 *                  +-+     +-+   |                                                               
	 *                   |       |    +-----> Buckets                                                 
	 *                  +-+     +-+   |                                                               
	 *                  +-+     +-+   |                                                               
	 *                   |       |    |                                                               
	 *                  +-+     +-+   |                                                               
	 *                  +-+     +-+   +
	 * 
	 * </pre>
	 */
	private final short[][] parentNodes;
	private final QTBucketA[][] childNodes;

	private final int noLevels;

	private final Map<E, QTBucketNode> bucketNodes;
	private final double inv_cellSizeX;
	private final double inv_cellSizeY;
	private final double inv_cellSizeX_minX;
	private final double inv_cellSizeY_minY;

	@SuppressWarnings("unchecked")
	public ArrayQuadTree(final int noLevels, int minX, int maxX, int minY,
			int maxY) {
		super(minX, maxX, minY, maxY);
		// initialize parent array
		this.parentNodes = new short[noLevels][];
		int arraySize = 1;
		int nrows = 1;
		int ncols = 1;
		for (int i = 0; i < parentNodes.length; i++)
		{
			// ;
			parentNodes[i] = new short[arraySize];
			arraySize <<= 2;
			nrows <<= 1;
			ncols <<= 1;
		}
		nrows >>= 1;
		ncols >>= 1;

		this.childNodes = new ArrayQuadTree.QTBucketA[nrows][ncols];

		this.noLevels = noLevels;
		this.bucketNodes = new ConcurrentHashMap<>();

		inv_cellSizeX = 1 / (x_length * 1D / childNodes.length);
		inv_cellSizeY = 1 / (y_length * 1D / childNodes[0].length);

		inv_cellSizeX_minX = inv_cellSizeX * minX;
		inv_cellSizeY_minY = inv_cellSizeY * minY;
		this.link();
	}

	public void insert(E e) {

		final int x = (int) (e.getX() * inv_cellSizeX - inv_cellSizeX_minX);
		final int y = (int) (e.getY() * inv_cellSizeY - inv_cellSizeY_minY);
		QTBucketNode node = this.bucketNodes.get(e);
		if (node == null)
		{
			node = new QTBucketNode(e);
			this.bucketNodes.put(e, node);

		}

		this.childNodes[x][y].add(node);

	}

	public void remove(Object e) {
		QTBucketNode node = this.bucketNodes.remove(e);
		if (node == null)
		{
			return;

		}
		node.unlink();
	}

	public Iterable<E> getElementsIn() {
		return this.bucketNodes.keySet();
	}

	public int countElements() {
		return this.bucketNodes.size();
	}

	public boolean contains(E elem) {
		return this.bucketNodes.containsKey(elem);
	}

	private static boolean contains(double rect_x0, double rect_y0,
			double rect_x1, double rect_y1, double px, double py) {
		return (px >= rect_x0 && py >= rect_y0 && px <= rect_x1 && py <= rect_y1);
	}

	public void searchInRange(double rx0, double ry0, double rx1, double ry1,
			ArrayQuadTreeCallback<E> callback) {
		searchInRange(0, 0, this.minX, this.minY, this.maxX, this.maxY, rx0,
				ry0, rx1, ry1, callback);
	}

	private void searchInRange(int level, int index, double x0, double y0,
			double x1, double y1, double rx0, double ry0, double rx1,
			double ry1, ArrayQuadTreeCallback<E> callback) {
		if (callback.shouldStop())
			return;
		if (level == this.noLevels-1)
		{
			int x = (int) ((x0 - minX) * inv_cellSizeX);
			int y = (int) ((y0 - minY) * inv_cellSizeY);
			for (E elem : this.childNodes[x][y])
			{
				if (elem==null) break;
				if (contains(rx0, ry0, rx1, ry1, elem.getX(), elem.getY()))
					callback.query(elem);
			}
		}

		else
		{

			double parent_csx = Math.abs(x1 - x0);
			double child_csx = parent_csx / 2;
			double left_x = x0;
			double mid_x = x0 + child_csx;
			double right_x = x0 + parent_csx;

			double parent_csy = Math.abs(y1 - y0);
			double child_csy = parent_csy / 2;

			double top_y = y0;
			double mid_y = y0 + child_csy;
			double bottom_y = y0 + parent_csy;
			int first_index = index << 2;

			// NW
			if(this.parentNodes[level+1][first_index] > 0)
			if (overlaps(left_x, top_y, mid_x, mid_y, rx0, ry0, rx1, ry1))
			{
				
				searchInRange(level + 1, first_index, left_x, top_y, mid_x,
						mid_y, rx0, ry0, rx1, ry1, callback);
			}
			// NE
			if(this.parentNodes[level+1][first_index+1] > 0)
			if (overlaps(mid_x, top_y, right_x, mid_y, rx0, ry0, rx1, ry1))
			{
				searchInRange(level + 1, first_index + 1, mid_x, top_y,
						right_x, mid_y, rx0, ry0, rx1, ry1, callback);
			}
			// SW
			if(this.parentNodes[level+1][first_index+2] > 0)
			if (overlaps(left_x, mid_y, mid_x, bottom_y, rx0, ry0, rx1, ry1))
			{
				searchInRange(level + 1, first_index + 2, left_x, mid_y, mid_x,
						bottom_y, rx0, ry0, rx1, ry1, callback);
			}
			// SE
			if(this.parentNodes[level+1][first_index+3] > 0)
			if (overlaps(mid_x, mid_y, right_x, bottom_y, rx0, ry0, rx1, ry1))
			{
				searchInRange(level + 1, first_index + 3, mid_x, mid_y,
						right_x, bottom_y, rx0, ry0, rx1, ry1, callback);
			}
		}

	}

	public Iterable<KPolygon> getRectangles() {
		ArrayList<KPolygon> rects = new ArrayList<>();
		this.addRectangles(0, 0, this.minX, this.minY, this.maxX, this.maxY,
				rects);
		return rects;
	}

	private void addRectangles(int level, int index, double x0, double y0,
			double x1, double y1, ArrayList<KPolygon> rectangles) {
		if (level >= this.noLevels)
			return;
		if (this.parentNodes[level][index] <= 0)
		{
			rectangles.add(KPolygon.createRect(x0, y0, x1, y1));
		}
		else
		{

			double parent_csx = Math.abs(x1 - x0);
			double child_csx = parent_csx / 2;
			double left_x = x0;
			double mid_x = x0 + child_csx;
			double right_x = x0 + parent_csx;

			double parent_csy = Math.abs(y1 - y0);
			double child_csy = parent_csy / 2;

			double top_y = y0;
			double mid_y = y0 + child_csy;
			double bottom_y = y0 + parent_csy;
			int first_index = index << 2;

			// NW
			// if (overlaps(left_x, top_y, mid_x, mid_y, rx0, ry0, rx1, ry1))
			{
				addRectangles(level + 1, first_index, left_x, top_y, mid_x,
						mid_y, rectangles);
			}
			// NE
			// if (overlaps(mid_x, top_y, right_x, mid_y, rx0, ry0, rx1, ry1))
			{
				addRectangles(level + 1, first_index + 1, mid_x, top_y,
						right_x, mid_y, rectangles);
			}
			// SW
			// if (overlaps(left_x, mid_y, mid_x, bottom_y, rx0, ry0, rx1, ry1))
			{
				addRectangles(level + 1, first_index + 2, left_x, mid_y, mid_x,
						bottom_y, rectangles);
			}
			// SE
			// if (overlaps(mid_x, mid_y, right_x, bottom_y, rx0, ry0, rx1,
			// ry1))
			{
				addRectangles(level + 1, first_index + 3, mid_x, mid_y,
						right_x, bottom_y, rectangles);
			}
		}

	}

	private void link() {
		link(0, 0, this.minX, this.minY, this.maxX, this.maxY);
	}

	private void link(int level, int index, double x0, double y0, double x1,
			double y1) {

		if (level >= this.noLevels - 1)
		{

			int x = (int) (((x0 + (Math.abs(x0 - x1)) / 2 - minX)) * inv_cellSizeX);
			int y = (int) (((y0 + (Math.abs(y0 - y1)) / 2 - minY)) * inv_cellSizeY);
			if (this.childNodes[x][y] == null)
			{
				this.childNodes[x][y] = new QTBucketA(index);
			}

		}
		else
		{

			// NW
			double parent_csx = Math.abs(x1 - x0);
			double child_csx = parent_csx / 2;
			double left_x = x0;
			double mid_x = x0 + child_csx;
			double right_x = x0 + parent_csx;

			double parent_csy = Math.abs(y1 - y0);
			double child_csy = parent_csy / 2;

			double top_y = y0;
			double mid_y = y0 + child_csy;
			double bottom_y = y0 + parent_csy;
			int first_index = index << 2;

			// NW
			link(level + 1, first_index, left_x, top_y, mid_x, mid_y);
			// NE

			link(level + 1, first_index + 1, mid_x, top_y, right_x, mid_y);
			// SW

			link(level + 1, first_index + 2, left_x, mid_y, mid_x, bottom_y);
			// SE
			link(level + 1, first_index + 3, mid_x, mid_y, right_x, bottom_y);
		}

	}

	private static final boolean overlaps(double x0, double y0, double x1,
			double y1, double rx0, double ry0, double rx1, double ry1) {
		return (x0 < rx1 && x1 > rx0 && y0 < ry1 && y1 > ry0);
	}

	public static void main(String[] args) {
		testInsert(8);

	}

	private static void testInsert(int maxLevels) {
		ArrayQuadTree<TestTreeElement> quadPilu = new ArrayQuadTree<>(
				maxLevels, -2310, 100, -8210, 100);
		quadPilu.link();
		for (int i = 0; i < 10; i++)
		{
			quadPilu.insert(new TestTreeElement(ThreadLocalRandom.current()
					.nextInt(-2310, 100), ThreadLocalRandom.current().nextInt(
					-8210, 100)));
		}

		for (int i = 0; i < quadPilu.childNodes.length; i++)
		{
			for (int j = 0; j < quadPilu.childNodes[i].length; j++)
			{
				double x0 = i
						* (quadPilu.x_length * 1D / quadPilu.childNodes.length)
						+ quadPilu.minX;
				double y0 = j
						* (quadPilu.y_length * 1D / quadPilu.childNodes[0].length)
						+ quadPilu.minY;

				double x1 = (i + 1)
						* (quadPilu.x_length * 1D / quadPilu.childNodes.length)
						+ quadPilu.minX;
				double y1 = (j + 1)
						* (quadPilu.y_length * 1D / quadPilu.childNodes[0].length)
						+ quadPilu.minY;

				for (TestTreeElement node : quadPilu.childNodes[i][j])
				{
					if (!contains(x0, y0, x1, y1, node.getX(), node.getY()))
					{
						System.out.println("NOK : ");
						System.out.println(node.getXY() + "|=> "
								+ new AABB(x0, y0, x1, y1));
					}
				}
			}
		}

		System.out.println("===============");
	}

	public static class TestTreeElement implements CoordinateHolder, Indexable {
		private final KVector v = new KVector();

		public TestTreeElement(int x, int y) {
			this.v.x = x;
			this.v.y = y;
		}

		public void setY(int y) {
			this.v.y = y;
		}

		public void setX(int x) {
			this.v.x = x;
		}

		@Override
		public double getX() {
			return this.v.x;
		}

		@Override
		public double getY() {
			return this.v.y;
		}

		@Override
		public KPoint getXY() {
			return this.v.getXY();
		}

		private static int ID = 0;
		int id = ID++;

		@Override
		public int getID() {
			return id;
		}

	}

	@SuppressWarnings("unused")
	private final void decrementAtIndex(final int referenceIndex) {
		for (int k = referenceIndex, i = this.noLevels - 1; i >= 0; i--, k >>= 2)
		{
			this.parentNodes[i][k]--;
		}
	}

	private final void incrementAtIndex(final int referenceIndex) {
		for (int k = referenceIndex, i = this.noLevels - 1; i >= 0; i--, k >>= 2)
		{
			this.parentNodes[i][k]++;
		}
	}

	private final void decrementAndIncrementAtIndexes(
			final int referenceIndex1, final int referenceIndex2) {
		for (int k1 = referenceIndex1, k2 = referenceIndex2, i = this.noLevels - 1; i >= 0
				&& k1 != k2; i--, k1 >>= 2, k2 >>= 2)
		{
			this.parentNodes[i][k1]--;
			this.parentNodes[i][k2]++;
		}
	}

	private class QTBucketA implements Iterable<E> {

		QTBucketNode head;

		public QTBucketA(int referenceIndex) {
			this.head = new QTBucketNode(referenceIndex);

		}

		public void add(QTBucketNode node) {
			// unlink
			node.unlink();
			if (node.getReferenceIndex() != -1)
			{

				decrementAndIncrementAtIndexes(node.getReferenceIndex(),
						head.getReferenceIndex());
			}
			else
			{
				incrementAtIndex(head.getReferenceIndex());
			}
			node.setReferenceIndex(this.head.getReferenceIndex());
			// link
			node.link(head);

		}

		@Override
		public Iterator<E> iterator() {
			return new QTBucketIterator();
		}

		private class QTBucketIterator implements Iterator<E> {

			private QTBucketNode current;
			
			public QTBucketIterator() {
				this.current = head;
			}

			@Override
			public boolean hasNext() {
				return current.getNext() != null;
			}

			@Override
			public E next() {
				this.current = current.getNext();
				return current.getItem();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not implemented yet");
			}

		}

	}

	private class QTBucketNode {
		private E item;
		private QTBucketNode next;
		protected QTBucketNode prev;
		private int referenceIndex = -1;

		public QTBucketNode(E item) {
			this.setItem(item);
			this.setNext(null);
			this.prev = null;
		}

		public QTBucketNode(int referenceIndex) {
			this.setItem(null);
			this.setNext(null);
			this.prev = null;
			this.referenceIndex = referenceIndex;
		}

		public void unlink() {

			if (this.getNext() != null)
			{
				this.getNext().prev = this.prev;
			}
			// no deberia pasar pero bueno
			if (this.prev != null)
			{
				this.prev.setNext(this.getNext());
			}
			this.setNext(null);
			this.prev = null;
		}

		public void link(QTBucketNode head) {
			this.setNext(head.getNext());
			this.prev = head;
			//
			if (this.getNext() != null)
			{
				this.getNext().prev = this;
			}
			head.setNext(this);
		}

		public int getReferenceIndex() {
			return referenceIndex;
		}

		public void setReferenceIndex(int referenceIndex) {
			this.referenceIndex = referenceIndex;
		}

		public QTBucketNode getNext() {
			return next;
		}

		public void setNext(QTBucketNode next) {
			this.next = next;
		}

		public E getItem() {
			return item;
		}

		public void setItem(E item) {
			this.item = item;
		}

	}
}
