package com.massisframework.massis.model.components.building;

import java.util.Map;

import com.massisframework.massis.model.components.SimulationComponent;

public interface MetadataComponent extends SimulationComponent {

	public String get(String key);
	public boolean containsKey(String key);
	public void put(String key,String value);
	public void remove(String key);
	public void putAll(Map<String,String> values);
}
