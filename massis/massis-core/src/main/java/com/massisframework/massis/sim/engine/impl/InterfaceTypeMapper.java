package com.massisframework.massis.sim.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.Family.Builder;
import com.badlogic.ashley.signals.Listener;

@SuppressWarnings("unchecked")
public class InterfaceTypeMapper implements EntityListener {

	private Map<Class<? extends Component>, FamilyWrapper> familiesOne;
	private Listener<Entity> componentListener;

	public InterfaceTypeMapper()
	{
		this.familiesOne = new HashMap<>();
		this.componentListener = (signal, entity) -> this.map(entity);
	}

	public Family getFamily(Class<? extends Component> type)
	{
		this.ensureMapped(type);
		return this.familiesOne.get(type).getFamily();
	}

	public Family all(Class<? extends Component>... types)
	{
		Builder joined = Family.all(types[0]);
		for (int i = 1; i < types.length; i++)
		{
			joined.all(types[i]);
		}
		return joined.get();
	}

	private void map(Entity e)
	{
		for (Component c : e.getComponents())
		{
			if (c != null)
			{
				this.ensureMapped(c.getClass());
			}
		}
	}

	private void ensureMapped(Class<? extends Component>... types)
	{
		for (Class<? extends Component> type : types)
		{

			if (!isAlreadyMapped(type))
			{
				for (Class<? extends Component> itf : getAllInterfaces(type))
				{
					FamilyWrapper fw = this.familiesOne.get(itf);
					if (fw == null)
					{
						fw = new FamilyWrapper();
						this.familiesOne.put(itf, fw);
					}
					fw.addType(type);
				}
			}
		}
	}

	private Iterable<Class<? extends Component>> getAllInterfaces(
			Class<? extends Component> type)
	{
		List<Class<? extends Component>> itfs = new ArrayList<>();
		for (Object obj : ClassUtils.getAllInterfaces(type))
		{
			Class<?> itf = (Class<?>) obj;
			if (Component.class.isAssignableFrom(itf))
			{
				itfs.add((Class<? extends Component>) itf);
			}
		}
		return itfs;

	}

	private boolean isAlreadyMapped(Class<? extends Component> type)
	{
		return this.familiesOne.containsKey(type);
	}

	private static class FamilyWrapper {
		Family family;
		Set<Class<? extends Component>> classes;

		public FamilyWrapper()
		{
			this.classes = new HashSet<>();
		}

		public Family getFamily()
		{
			if (this.family == null)
			{
				this.family = Family.one(classes.toArray(new Class[] {})).get();
			}
			return this.family;
		}

		public void addType(Class<? extends Component> type)
		{
			if (this.classes.add(type))
			{
				this.reset();
			}
		}

		private void reset()
		{
			this.family = null;
		}
	}

	@Override
	public void entityAdded(Entity entity)
	{
		this.map(entity);
		entity.componentAdded.add(this.componentListener);
	}

	@Override
	public void entityRemoved(Entity entity)
	{
		entity.componentAdded.remove(this.componentListener);
	}

}
