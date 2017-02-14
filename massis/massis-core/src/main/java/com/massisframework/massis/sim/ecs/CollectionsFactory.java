package com.massisframework.massis.sim.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CollectionsFactory {

	private static final List<MapFactory> MAP_FACTORIES;
	private static final Map<Class, Supplier<List>> LIST_FACTORIES;
	private static final Map<Class, Supplier<Set>> SET_FACTORIES;

	static
	{
		MAP_FACTORIES = MapFactory.builder()
				// ints
				.add(Integer.class, Integer.class, Int2IntOpenHashMap::new)
				.add(Integer.class, Long.class, Int2LongOpenHashMap::new)
				.add(Integer.class, Object.class, Int2ObjectOpenHashMap::new)
				// longs
				.add(Long.class, Integer.class, Long2IntOpenHashMap::new)
				.add(Long.class, Long.class, Long2LongOpenHashMap::new)
				.add(Long.class, Object.class, Long2ObjectOpenHashMap::new)

				.get();

		LIST_FACTORIES = new HashMap<>();
		LIST_FACTORIES.put(Short.class, ShortArrayList::new);
		LIST_FACTORIES.put(Integer.class, IntArrayList::new);
		LIST_FACTORIES.put(Long.class, LongArrayList::new);
		LIST_FACTORIES.put(Float.class, FloatArrayList::new);
		LIST_FACTORIES.put(Double.class, DoubleArrayList::new);

		SET_FACTORIES = new HashMap<>();
		SET_FACTORIES.put(Short.class, ShortOpenHashSet::new);
		SET_FACTORIES.put(Integer.class, IntOpenHashSet::new);
		SET_FACTORIES.put(Long.class, LongOpenHashSet::new);
		SET_FACTORIES.put(Float.class, FloatOpenHashSet::new);
		SET_FACTORIES.put(Double.class, DoubleOpenHashSet::new);

	}

	public static <K, V> Map<K, List<V>> newMapList(Class<K> a,
			Class<V> listClass)
	{
		return (Map) newMap(a, List.class);
	}

	public static <K, V> Map<K, V> newMap(Class<K> a, Class<V> b)
	{
		for (MapFactory f : MAP_FACTORIES)
		{
			Map<K, V> map = f.get(a, b);
			if (map != null)
			{
				return map;
			}
		}
		return new HashMap<K, V>();
	}

	public static <E> List<E> newList(Class<E> a)
	{
		Supplier<List> supplier = LIST_FACTORIES.get(a);
		if (supplier == null)
		{
			return new ArrayList<>();
		}
		return supplier.get();
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

	public static <T> Set<T> newSet(Class<T> type)
	{
		Supplier<Set> supplier = SET_FACTORIES.get(type);
		if (supplier == null)
		{
			return new HashSet<>();
		}
		return (Set<T>) supplier.get();
	}
}
