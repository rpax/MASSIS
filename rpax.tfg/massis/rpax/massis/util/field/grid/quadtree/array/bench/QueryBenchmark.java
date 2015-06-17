package rpax.massis.util.field.grid.quadtree.array.bench;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import rpax.massis.util.Indexable;
import rpax.massis.util.field.grid.quadtree.array.ArrayQuadTree;
import rpax.massis.util.field.grid.quadtree.array.ArrayQuadTreeCallback;
import rpax.massis.util.geom.CoordinateHolder;
import rpax.massis.util.geom.KVector;
import straightedge.geom.KPoint;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class QueryBenchmark {
	@Param({ "100", "200", "300", "400", "500", "600", "700", "800", "900",
			"1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700",
			"1800", "1900", "2000", "2100", "2200", "2300", "2400", "2500",
			"2600", "2700", "2800", "2900", "3000", "3100", "3200", "3300",
			"3400", "3500", "3600", "3700", "3800", "3900", "4000", "4100",
			"4200", "4300", "4400", "4500", "4600", "4700", "4800", "4900",
			"5000", "5100", "5200", "5300", "5400", "5500", "5600", "5700",
			"5800", "5900", "6000", "6100", "6200", "6300", "6400", "6500",
			"6600", "6700", "6800", "6900", "7000", "7100", "7200", "7300",
			"7400", "7500", "7600", "7700", "7800", "7900", "8000", "8100",
			"8200", "8300", "8400", "8500", "8600", "8700", "8800", "8900",
			"9000", "9100", "9200", "9300", "9400", "9500", "9600", "9700",
			"9800", "9900", "10000" })
	public int nElements;
	public TestTreeElement[] elements;

	public int minX = 0, maxX = 80000;
	public int minY = 0, maxY = 80000;
	public int maxLevels = 8;
	ArrayQuadTree<TestTreeElement> quadPilu;
	public int rangeArea;

	@Setup(Level.Iteration)
	public void fillElements() {
		rangeArea = maxX / 2;
		quadPilu = new ArrayQuadTree<>(maxLevels, minX, maxX, minY, maxY);
		elements = new TestTreeElement[nElements];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new TestTreeElement(minX, maxX, minY, maxY);
			this.quadPilu.insert(elements[i]);
		}

	}

	private static class DummyCallback implements
			ArrayQuadTreeCallback<TestTreeElement> {

		@Override
		public void query(TestTreeElement element) {/* do something */
		}

		@Override
		public boolean shouldStop() {
			return false;
		}

	}

	private static DummyCallback callback = new DummyCallback();

	@Benchmark
	public void rangeQuery() {
		for (int i = 0; i < elements.length; i++) {

			this.quadPilu.searchInRange(elements[i].getX() - rangeArea,
					elements[i].getY() - rangeArea, elements[i].getX()
							+ rangeArea, elements[i].getY() + rangeArea,
					callback);
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(
				QueryBenchmark.class.getSimpleName()).build();

		new Runner(opt).run();
	}

	private static int ID = 0;

	private class TestTreeElement implements CoordinateHolder, Indexable {
		private final KVector v = new KVector();

		int id = ID++;

		public TestTreeElement(int minX, int maxX, int minY, int maxY) {
			this.setRandom(minX, maxX, minY, maxY);
		}

		public void setRandom(int minX, int maxX, int minY, int maxY) {
			this.v.x = ThreadLocalRandom.current().nextInt(minX, maxX);
			this.v.y = ThreadLocalRandom.current().nextInt(minX, maxX);
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

		@Override
		public int getID() {
			return id;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestTreeElement other = (TestTreeElement) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		private QueryBenchmark getOuterType() {
			return QueryBenchmark.this;
		}

	}

}
