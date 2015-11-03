package com.massisframework.massis.util.field.grid.quadtree.array.bench;

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

import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.field.grid.quadtree.array.ArrayQuadTree;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;
import straightedge.geom.KPoint;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class InsertionBenchmark {
	@Param({ "10", "100", "1000", "10000" })
	public int nElements;
	public TestTreeElement[] elements;

	public int minX = 0, maxX = 80000;
	public int minY = 0, maxY = 80000;
	public int maxLevels = 8;
	ArrayQuadTree<TestTreeElement> quadPilu;

	@Setup(Level.Iteration)
	public void fillElements() {

		quadPilu = new ArrayQuadTree<>(maxLevels, minX, maxX, minY, maxY);
		elements = new TestTreeElement[nElements];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new TestTreeElement(minX, maxX, minY, maxY);
		}

	}

	@Benchmark
	public void moveOrInsert() {
		for (int i = 0; i < elements.length; i++) {
			this.quadPilu.insert(elements[i]);
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(
				InsertionBenchmark.class.getSimpleName()).build();

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

		private InsertionBenchmark getOuterType() {
			return InsertionBenchmark.this;
		}

	}

}
