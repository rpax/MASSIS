package com.massisframework.massis.sim.ecs.zayes;

import com.simsilica.es.EntityData;

public class EntityEditImpl<T extends SimulationEntityComponent>
		implements EntityEdit<T>, Reseteable {

	private Object cmp;
	private SimulationEntityData ed;
	private SimulationEntity se;
	private ObjectPool op;

	public EntityEditImpl()
	{
	}

	@Override
	public T get()
	{
		return (T) this.cmp;
	}

	@Override
	public SimulationEntity commit()
	{
		((EntityData) ed).setComponent(se.getId(), (T) cmp);
		this.op.free(this);
		return se;
	}

	public void reset()
	{
		this.cmp = null;
		this.ed = null;
		this.se = null;
		this.op = null;
	}

	public Object getCmp()
	{
		return cmp;
	}

	public void setCmp(Object cmp)
	{
		this.cmp = cmp;
	}

	public SimulationEntityData getEd()
	{
		return ed;
	}

	public void setEd(SimulationEntityData ed)
	{
		this.ed = ed;
	}

	public SimulationEntity getSe()
	{
		return se;
	}

	public void setSe(SimulationEntity se)
	{
		this.se = se;
	}

	public void setObjectPool(ObjectPool objectPool)
	{
		this.op = objectPool;
	}

}
