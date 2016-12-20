package com.massisframework.massis.model.managers;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.IBuilding;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.util.collections.filters.Filters;

/**
 * Manages the environment information of an agent
 *
 * @author rpax
 *
 */
public class EnvironmentManager {

    protected IBuilding building;

    public EnvironmentManager(IBuilding building)
    {
        this.building = building;
    }

    public Iterable<DefaultAgent> getAgentsInRange(Location l, double range)
    {
        return l.getFloor().getAgentsInRange((int) (l.getX() - range),
                (int) (l.getY() - range), (int) (l.getX() + range),
                (int) (l.getY() + range));

    }

    public Iterable<DefaultAgent> getAgentsInRange(DefaultAgent a, double range)
    {
        return Filters.allExcept(this.getAgentsInRange(a.getLocation(), range),
                a);
    }

    public RoomComponent getRandomRoom()
    {
        return this.building.getRandomRoom();
    }

    public Location getNamedLocation(String name)
    {
        return this.building.getNamedLocation(name);
    }
}
