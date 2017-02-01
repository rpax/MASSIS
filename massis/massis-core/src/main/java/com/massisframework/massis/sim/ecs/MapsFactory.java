package com.massisframework.massis.sim.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

public class MapsFactory {

	private static final List<MapFactory> FACTORY_LIST;

	static
	{
		FACTORY_LIST = MapFactory.builder()
				// ints
				.add(Integer.class, Integer.class, Int2IntOpenHashMap::new)
				.add(Integer.class, Long.class, Int2LongOpenHashMap::new)
				.add(Integer.class, Object.class, Int2ObjectOpenHashMap::new)
				// longs
				.add(Integer.class, Integer.class, Int2IntOpenHashMap::new)
				.add(Integer.class, Long.class, Int2LongOpenHashMap::new)
				.add(Integer.class, Object.class, Int2ObjectOpenHashMap::new)

				.get();
	}

	public static <K, V> Map<K, V> get(Class<K> a, Class<V> b)
	{
		for (MapFactory f : FACTORY_LIST)
		{
			Map<K, V> map = f.get(a, b);
			if (map != null)
			{
				return map;
			}
		}
		return new HashMap<K, V>();
	}

	public static void main(String[] args)
	{
		Map<Integer, Long> m = MapsFactory.get(Integer.class,
				Long.class);

		m.put(1, 2L);
		m.get(7);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <V> Map createLongMap(Class<V> value)
	{

		if (value == int.class)
		{
			return new Long2IntOpenHashMap();
		}
		if (value == long.class)
		{
			return new Long2LongOpenHashMap();
		}
		return new HashMap<Long, V>();
	}

	private static <A, B> MapFactory<A, B> c(Class<A> a,
			Class<B> b, Supplier<Map<A, B>> supplier)
	{
		return new MapFactory<>(a, b, supplier);
	}

	private static class MapFactory<A, B> {

		private Class<A> a;
		private Class<B> b;
		private Supplier<Map<A, B>> supplier;

		public MapFactory(Class<A> a, Class<B> b, Supplier<Map<A, B>> supplier)
		{
			this.a = a;
			this.b = b;
			this.supplier = supplier;
		}

		public <A_2, B_2> Map<A_2, B_2> get(Class<A_2> a, Class<B_2> b)
		{
			if (satisfies(a, b))
			{
				return (Map<A_2, B_2>) this.supplier.get();
			} else
			{
				return null;
			}
		}

		public boolean satisfies(Class a, Class b)
		{
			return (this.a.isAssignableFrom(a) && this.b.isAssignableFrom(b));
		}

		public static FactoryListBuilder builder()
		{
			return new FactoryListBuilder();
		}
	}

	private static class FactoryListBuilder {
		private List<MapFactory> factories = new ArrayList<>();

		public <A, B> FactoryListBuilder add(Class<A> a, Class<B> b,
				Supplier<Map<A, B>> supplier)
		{
			this.add(new MapFactory(a, b, supplier));
			return this;
		}

		public <A, B> FactoryListBuilder add(MapFactory<A, B> f)
		{
			this.factories.add(f);
			return this;
		}

		public List<MapFactory> get()
		{
			return new CopyOnWriteArrayList<>(this.factories);
		}

	}
}
