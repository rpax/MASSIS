package com.massisframework.massis.sim.ecs.zayes;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityChange;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntity;
import com.simsilica.es.base.DefaultEntitySet;

@SuppressWarnings({ "rawtypes", "unchecked" })
class InterfaceEntitySet extends DefaultEntitySet {

	private InterfaceEntityData ed;
	private Class[] types;
	private ComponentFilter[] filters;

	public InterfaceEntitySet(InterfaceEntityData ed, ComponentFilter filter,
			Class[] types)
	{
		super(ed, filter, types);
		this.ed = ed;
		this.types = types;
		try
		{
			Field f = DefaultEntitySet.class.getDeclaredField("filters");
			f.setAccessible(true);
			this.filters = (ComponentFilter[]) f.get(this);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		this.transaction = new InterfaceTransaction();

	}

	private int typeIndex(Class type)
	{
		for (int i = 0; i < types.length; i++)
		{
			if (types[i] == type)
			{
				return i;
			}
		}
		return -1;
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

	protected class InterfaceTransaction extends Transaction {

		Map<EntityId, DefaultEntity> _adds;
		Set<EntityId> mods;

		public InterfaceTransaction()
		{
			super();
			this._adds = getFieldValue("adds", Transaction.class);
			this.mods = getFieldValue("mods", Transaction.class);
		}

		public void addChange(EntityChange change, Set<EntityChange> updates)
		{
			EntityId id = change.getEntityId();
			EntityComponent comp = change.getComponent();
			DefaultEntity e = (DefaultEntity) getEntity(id);

			// If we don't have the entity then it's an add
			// and we need to create one.
			if (e == null)
			{
				// See if we already added this one
				e = _adds.get(id);

				if (e == null)
				{
					if (comp == null)
					{
						// We've never seen this entity before and we get
						// an event about removing a component. We can
						// safely ignore it
						return;
					}

					// We add the components even if they don't match because
					// otherwise we might have to retrieve them again. We
					// could get lots of changes for this entity and we will
					// validate what we have in the completion loop.
					// ...BUT...
					// On the other hand, we might not get any more changes
					// for this entity at all and if the component didn't match
					// then we created the entity for nothing. This is an
					// extremely common use-case for the types of components
					// most
					// likely to be spammed and filtered.
					if (!isMatchingComponent(comp))
					{
						return;
					}

					// Else we need to add it.

					// Create an empty entity with the right number
					// of components.
					e = new DefaultInterfaceEntity(ed, id,
							new SimulationComponent[types.length], types);
					_adds.put(id, e);
				}

			} else
			{
				// Then it's an entity we have already and we are about
				// to change it.
				mods.add(id);

				// We track the updates that caused a change... we'll
				// filter out the ones that were for removed entities
				// later. This is actually somewhat better than we did
				// before since we only send changes for entities that
				// are still relevant.
			}

			// Apply the change
			int index;
			if (comp == null)
			{
				index = typeIndex(change.getComponentType());
			} else
			{
				index = typeIndex(comp.getClass());
			}

			// We keep track of the changes that might have been
			// relevant. Technically this is too broadly scoped but
			// determining accurate affecting changes requires book-keeping
			// per entity.
			// If we do no filtering at all that means that all Position
			// changes get delivered to all clients even if they don't
			// have the entity... no. Because we would have pre-filtered
			// that case. Still, we can double-check here by hitting it
			// against the filter before adding it to the updates set.
			if (updates != null)
			{
				if (comp == null
						|| filters == null
						|| filters[index] == null
						|| filters[index].evaluate(comp))
				{

					// ||
					// mainFilter.evaluate(e.get(mainFilter.getComponentType()))
					// ) {
					//
					// The last condition above is proposed by user qxCsXO1
					// in this thread:
					// http://hub.jmonkeyengine.org/t/zay-es-net-componentfilter-not-working/33196
					//
					// What it does is avoid adding updates for entities that
					// don't
					// meet the main filter. This comes up in cases where a
					// player
					// is filtering by something like an OwnedBy component but
					// is still
					// getting position changes for those entities.
					//
					// Unfortunately, the issue with the above change is it
					// might
					// miss some changes. We may not have applied the change yet
					// that
					// lets e.get(mainFilter.getComponentType()) pass. When we
					// eventually
					// get to it then we've already missed the other updates. If
					// those
					// other changes happen infrequently then the client can be
					// really
					// behind.
					//
					// It could be that we have no choice but to make two passes
					// through
					// the change events or go through the updates and remove
					// the ones
					// for entities that aren't in the set anymore.
					updates.add(change);
				} else
				{
				}
			}

			// Setting a component to null because of a 'removed'
			// component is different than a component that just
			// happens to be null because it hasn't been filled in yet.
			e.getComponents()[index] = comp != null ? comp : REMOVED_COMPONENT;

			// There is another issue I've just thought of regarding the updates
			// set. The updates will potentially come out of order. Mostly
			// we update often enough that it won't happen much but it is
			// possible
			// that two changes to the same component get added to the set out
			// of
			// order and then the clients (in a network use-case) end up with
			// incorrect values.
			//
			// Two approaches I see:
			// 1) timestamp the updates so we can sort them by order.
			//
			// 2) abandon the collection of an updates set completely and go
			// with another solution in the networking layer that does its
			// own change tracking more accurately and sends them per entity set
			// or something.

		}

		private <K> K getFieldValue(String name, Class clazz)
		{
			try
			{
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				return (K) f.get(this);
			} catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

}
