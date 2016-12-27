package com.massisframework.massis.ecs.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;

public class EntitiesCollections {

	public static Iterable<Integer> iterate(final IntBag bag)
	{
		return new Iterable<Integer>() {

			@Override
			public Iterator<Integer> iterator()
			{
				return new EntityIdIterator(bag);
			}

		};
	}

	public static Iterable<Entity> iterate(final IntBag bag, final World w)
	{
		return new Iterable<Entity>() {

			@Override
			public Iterator<Entity> iterator()
			{
				return new EntityIterator(new EntityIdIterator(bag), w);
			}
		};
	}

	private static class EntityIdIterator implements Iterator<Integer> {
		private int[] data;
		private int i;
		private int lastReturned;
		private int s;

		protected EntityIdIterator(IntBag bag)
		{
			this.data = bag.getData();
			this.i = 0;
			this.s = bag.size();
			this.lastReturned = -1;
		}

		public boolean hasNext()
		{
			return s > i;
		}

		public Integer next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			lastReturned = i++;
			return data[lastReturned];
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private static class EntityIterator implements Iterator<Entity> {
		private World world;
		private Iterator<Integer> entityIterator;

		protected EntityIterator(Iterator<Integer> entityIdIterator, World w)
		{
			this.entityIterator = entityIdIterator;
			this.world = w;
		}

		public boolean hasNext()
		{
			return this.entityIterator.hasNext();
		}

		public Entity next()
		{
			return this.world.getEntity(this.entityIterator.next());
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
