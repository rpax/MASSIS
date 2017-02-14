package com.massisframework.massis.sim.ecs.zayes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

public class MultiHandlerSet implements Set<EntityId> {

	private static ThreadLocal<Set<EntityId>> tmpSetTL = ThreadLocal
			.withInitial(HashSet::new);
	private Class<? extends EntityComponent> cType;
	private InterfaceEntityData ed;
	private ComponentFilter filter;

	public MultiHandlerSet(InterfaceEntityData ed,
			Class<? extends EntityComponent> type, ComponentFilter filter)
	{
		this.cType = type;
		this.ed = ed;
		// Ensure type is registered
		this.ed.getHandler(type);
		this.filter = filter;
	}

	private void buildMatchingSet(Set<EntityId> set)
	{
		set.clear();
		this.ed.getHandlers().entrySet().stream()
				.filter(e -> cType.isAssignableFrom(e.getKey()))
				.map(e -> e.getValue().getEntities(filter))
				.forEach(set::addAll);
	}

	@Override
	public int size()
	{
		Set<EntityId> tmpSet = tmpSetTL.get();
		buildMatchingSet(tmpSet);
		return tmpSet.size();
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public boolean contains(Object o)
	{
		Set<EntityId> tmpSet = tmpSetTL.get();
		buildMatchingSet(tmpSet);
		return tmpSet.contains(o);
	}

	@Override
	public Iterator<EntityId> iterator()
	{
		Set<EntityId> tmpSet = tmpSetTL.get();
		buildMatchingSet(tmpSet);
		return tmpSet.iterator();
	}

	@Override
	public Object[] toArray()
	{
		Set<EntityId> tmpSet = tmpSetTL.get();
		buildMatchingSet(tmpSet);
		return tmpSet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		Set<EntityId> tmpSet = tmpSetTL.get();
		buildMatchingSet(tmpSet);
		return tmpSet.toArray(a);
	}

	@Override
	public boolean add(EntityId e)
	{
		throw new UnsupportedOperationException(MultiHandlerSet.class.getName()+ " is a view-only set");
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException(MultiHandlerSet.class.getName()+ " is a view-only set");
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		Set<EntityId> tmpSet = tmpSetTL.get();
		buildMatchingSet(tmpSet);
		return tmpSet.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends EntityId> c)
	{
		throw new UnsupportedOperationException(MultiHandlerSet.class.getName()+ " is a view-only set");
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException(MultiHandlerSet.class.getName()+ " is a view-only set");
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException(MultiHandlerSet.class.getName()+ " is a view-only set");
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException(MultiHandlerSet.class.getName()+ " is a view-only set");
	}

}
