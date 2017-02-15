package com.massisframework.massis.model.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massisframework.massis.util.geom.CoordinateHolder;

public class PathComponentImpl implements PathComponent {

	private List<CoordinateHolder> path = new ArrayList<>();

	public PathComponent setPath(List<CoordinateHolder> path)
	{
		this.path.clear();
		this.path.addAll(path);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.systems.PathComponent#getPath()
	 */
	@Override
	public List<CoordinateHolder> getPath()
	{
		return Collections.unmodifiableList(path);
	}

}
