package com.massisframework.massis.sim.ecs.zayes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ObjectPool<T> {
	private Consumer<T> resetter;
	private final List<T> cache;
	private Supplier<T> instantiator;

	private ObjectPool(Supplier<T> supplier, Consumer<T> resetter)
	{
		this.instantiator = supplier;
		this.resetter = resetter;
		cache = new ArrayList<T>();
	}

	public static <R extends PooledObject> ObjectPool<R> create(Class<R> type,
			Supplier<R> instantiator)
	{
		return new ObjectPool<>(instantiator, (item) -> item.reset());
	}

	public static <T> ObjectPool<T> create(Class<T> type,
			Supplier<T> instantiator, Consumer<T> resetter)
	{
		return new ObjectPool<>(instantiator, resetter);
	}

	public T get()
	{
		System.out.println("retrieved item. Cache size: " + this.cache.size());
		if (cache.size() > 0)
			return cache.remove(cache.size() - 1);
		else
		{
			return instantiator.get();
		}
	}

	void free(T component)
	{
		this.resetter.accept(component);
		cache.add(component);
		Logger.getLogger(getClass().getName())
				.info("freed item. Cache size: " + this.cache.size());
	}

}
