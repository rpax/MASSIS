package com.massisframework.massis.sim.ecs.zayes;

import java.util.Set;

import com.simsilica.es.EntityId;

public interface SimulationEntitySet extends Iterable<SimulationEntity>{

	/**
	 * Returns true if this set contains the entity with the specified ID.
	 */
	public boolean containsId(EntityId id);

	/**
	 * Returns all of the EntityIds currently in this set.
	 */
	public Set<EntityId> getEntityIds();

	/**
	 * Returns this set's version of the Entity for the specified ID or null if
	 * this set does not contain the specified entity.
	 */
	public SimulationEntity getEntity(EntityId id);

	/**
	 * Returns the entities that were added during applyChanges().
	 */
	public Set<SimulationEntity> getAddedEntities();

	/**
	 * Returns the entities that were changed during applyChanges().
	 */
	public Set<SimulationEntity> getChangedEntities();

	/**
	 * Returns the entities that were removed during applyChanges().
	 */
	public Set<SimulationEntity> getRemovedEntities();

	/**
	 * Returns true if there were entity changes during the last applyChanges().
	 */
	public boolean hasChanges();

	/**
	 * Applies any accumulated changes to this list's entities since the last
	 * time it was called and returns true if there were changes.
	 */
	public boolean applyChanges();

	/**
	 * Releases this entity set from processing further entity updates. The
	 * entities contained in the set will remain until garbage collected
	 * normally or until clear() is called.
	 */
	public void release();

	/**
	 * Returns true if this EntitySet is made of entities that have the
	 * specified type of component.
	 */
	public boolean hasType(Class type);
}
