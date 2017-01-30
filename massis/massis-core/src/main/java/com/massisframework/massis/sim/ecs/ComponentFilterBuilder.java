package com.massisframework.massis.sim.ecs;

@SuppressWarnings("rawtypes")
public interface ComponentFilterBuilder {

	public ComponentFilter get();

	public ComponentFilterBuilder exclude(
			Class... componentTypes);

	public ComponentFilterBuilder one(
			Class... componentTypes);

	public ComponentFilterBuilder all(
			Class... componentTypes);

}
