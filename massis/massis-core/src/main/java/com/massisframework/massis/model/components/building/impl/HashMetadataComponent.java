package com.massisframework.massis.model.components.building.impl;

import java.util.HashMap;
import java.util.Map;

import com.massisframework.massis.model.components.building.MetadataComponent;

public class HashMetadataComponent implements MetadataComponent {

	private Map<String, String> map;

	public HashMetadataComponent(Map<String, String> map)
	{
		this.map = new HashMap<>(map);
	}

	public HashMetadataComponent()
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
	public void remove(String key)
	{
		this.map.remove(key);

	}

}
