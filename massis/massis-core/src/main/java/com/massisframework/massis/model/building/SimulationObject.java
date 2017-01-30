package com.massisframework.massis.model.building;

import java.util.Collection;
import java.util.List;

import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.geom.CoordinateHolder;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public interface SimulationObject extends CoordinateHolder,
		Indexable {



	void animate();

	// @Override
	KPoint getXY();

	KPolygon getPolygon();

	/**
	 * TODO rpax. Remove it from here.
	 *
	 * @return the connectors on the floor of this agent
	 */
	List<RoomConnector> getRoomsConnectorsInSameFloor();

	Object getProperty(String propertyName);

	public Collection<String> getPropertyNames();

	boolean hasProperty(String propertyName);

	void setProperty(String propertyName, Object value);

	void removeProperty(String propertyName);

	double getAngle();

	public void addComponent(MassisComponent c);

	public <T extends MassisComponent> T getComponent(Class<T> type);

	public <T extends MassisComponent> void removeComponent(Class<T> type);
}