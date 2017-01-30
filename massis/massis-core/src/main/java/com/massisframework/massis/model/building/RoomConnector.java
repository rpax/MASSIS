package com.massisframework.massis.model.building;

import java.util.List;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

/**
 * Connects one or more rooms
 *
 * @author rpax
 *
 */
public interface RoomConnector extends SimulationComponent{

    public List<SimulationEntity> getConnectedRooms();
    
}
