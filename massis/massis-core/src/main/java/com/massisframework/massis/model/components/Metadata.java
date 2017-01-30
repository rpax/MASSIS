package com.massisframework.massis.model.components;

import java.util.Map;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.util.SimObjectProperty;

public interface Metadata extends SimulationComponent {

	void set(Map<String, String> metadata);

	String get(Enum<?> key);

	String get(String key);

}
