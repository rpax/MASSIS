package com.massisframework.massis.sim.ecs.zayes;

import java.lang.reflect.Field;
import java.util.List;

import com.massisframework.massis.sim.ecs.EntityComponentCreator;
import com.massisframework.massis.sim.ecs.InterfaceBindings;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.ComponentHandler;
import com.simsilica.es.base.DefaultEntity;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.base.DefaultEntitySet;

@SuppressWarnings({ "unchecked", "rawtypes" })
class InterfaceEntityData
		extends DefaultEntityData {

	private InterfaceBindings bindings;
	private List<DefaultEntitySet> entitySets;
	private EntityComponentCreator componentCreator;
	protected SimulationEntityData simED;

	public InterfaceEntityData(
			InterfaceBindings bindings,
			EntityComponentCreator componentCreator,
			SimulationEntityData simED)
	{
		super();
		this.simED = simED;
		this.bindings = bindings;
		this.entitySets = getFieldValue("entitySets");
		this.componentCreator = componentCreator;
	}

	@Override
	protected InterfaceEntitySet createSet(ComponentFilter filter,
			Class... types)
	{
		InterfaceEntitySet set = new InterfaceEntitySet(this, filter, types);
		entitySets.add(set);
		return set;
	}
	@Override
    public Entity getEntity( EntityId entityId, Class... types ) {
		SimulationComponent[] values = new SimulationComponent[types.length]; 
        for( int i = 0; i < values.length; i++ ) {
            values[i] = (SimulationComponent) getComponent( entityId, types[i] );
        }
        return new DefaultInterfaceEntity( this, entityId, values, types );            
    }
	@Override
	protected ComponentHandler getHandler(Class type)
	{
		return super.getHandler(this.bindings.getBinding(type));
	}

	////////////////////////////////////////////////
	private <K> K getFieldValue(String name)
	{
		try
		{
			Field f = DefaultEntityData.class.getDeclaredField(name);
			f.setAccessible(true);
			return (K) f.get(this);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public <T extends SimulationComponent> T addNewComponent(
			EntityId id, Class<T> c)
	{
		T cmp = componentCreator.create(c);
		super.setComponent(id, cmp);
		return cmp;
	}

}
