package com.massisframework.massis.model.components.building.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.building.MetadataComponent;

public class HashMetadataComponent extends AbstractSimulationComponent implements MetadataComponent {

	private Map<String, String> map;
	@Inject
	private HashMetadataComponent()
	{
		this.map = new HashMap<>();
	}

	@Override
	public String get(String key)
	{
		return this.map.get(key);
	}

	@Override
	public boolean containsKey(String key)
	{
		return this.map.containsKey(key);
	}

	@Override
	public void put(String key, String value)
	{
		this.map.put(key, value);
	}
	
	@Override
	public void putAll(Map<String,String> values)
	{
		this.map.putAll(values);
	}
	

	@Override
	public void remove(String key)
	{
		this.map.remove(key);
	}

}
