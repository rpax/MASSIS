package com.massisframework.massis.sim.ecs;

public interface ComponentModifier {

	<T extends SimulationComponent, K extends ComponentModifier & ComponentEdit<T>> K add(
			Class<T> type);

	<T extends SimulationComponent> ComponentModifier remove(Class<T> type);

	<T extends SimulationComponent> ComponentEdit<T> edit(Class<T> type);

}
