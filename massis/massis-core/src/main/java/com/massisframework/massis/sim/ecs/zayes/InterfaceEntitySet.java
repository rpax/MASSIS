package com.massisframework.massis.sim.ecs.zayes;

import java.util.Set;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntitySet;

@SuppressWarnings({ "rawtypes", "unchecked" })
class InterfaceEntitySet extends DefaultEntitySet {

	private InterfaceEntityData ed;
	private Class[] types;

	public InterfaceEntitySet(InterfaceEntityData ed, ComponentFilter filter,
			Class[] types)
	{
		super(ed, filter, types);
		this.ed = ed;
		this.types = types;

	}

	/**
	 * Called to have the entity set load its initial set of data. This is
	 * called during creation (but not construction) and when the filter is
	 * reset.
	 */

	protected void loadEntities(boolean reload)
	{

		Set<EntityId> idSet = ed.findEntities(getMainFilter(), types);
		if (idSet.isEmpty())
			return;

		// Note: we do a full component loop here just to
		// reuse the EntityComponent[] buffer. We could have
		// just as easily called ed.getEntity() for each ID.

		// Now we have the info needed to build the entity set
		SimulationComponent[] buffer = new SimulationComponent[types.length];
		for (EntityId id : idSet)
		{
			// If we already have the entity then it is not a new
			// add and we'll ignore it. This means that some entities
			// may have newer info than others but we will get their
			// event soon enough.
			// We include this for the reload after a filter change.
			if (reload && containsId(id))
			{
				continue;
			}

			for (int i = 0; i < buffer.length; i++)
			{
				buffer[i] = (SimulationComponent) ed.getComponent(id, types[i]);
			}

			// Now create the entity
			DefaultInterfaceEntity e = new DefaultInterfaceEntity(ed, id,
					buffer.clone(), types);
			if (add(e) && reload)
			{
				getAddedEntities().add(e);
			}
		}

		// I had a big long comment in AbstractEntityData worrying
		// about threading and stuff. But the EntityChange events
		// are getting queued and as long as they aren't rejected
		// out of hand (which would be a bug) then we don't care if
		// they come in while we build the entity set.
	}

}
