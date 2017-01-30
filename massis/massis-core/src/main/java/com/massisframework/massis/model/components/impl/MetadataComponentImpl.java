package com.massisframework.massis.model.components.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.massisframework.massis.model.components.Metadata;

public class MetadataComponentImpl implements Metadata {

	private Map<String, String> metadataMap;

	public MetadataComponentImpl()
	{
		this.metadataMap = new HashMap<>();
	}

	public String get(String key)
	{
		return this.metadataMap.get(key);
	}

	public String set(String key, String value)
	{
		return this.metadataMap.put(key, value);
	}

	public Collection<String> getKeys()
	{
		return Collections.unmodifiableCollection(this.metadataMap.keySet());
	}

	@Override
	public void set(Map<String, String> metadata)
	{
		this.metadataMap.clear();
		this.metadataMap.putAll(metadata);

	}

	@Override
	public String get(Enum<?> key)
	{
		return this.get(key.toString());
	}
}
