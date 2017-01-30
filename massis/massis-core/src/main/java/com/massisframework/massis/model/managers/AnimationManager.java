package com.massisframework.massis.model.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.massisframework.massis.displays.SimulationDisplay;
import com.massisframework.massis.sim.ecs.SimulationEntity;

/**
 * Controls the animations. Basically it consists on a list of
 * {@link SimulationDisplay}s, whose are updated when requested.
 */
public class AnimationManager {

    private final List<SimulationDisplay> displays;

    public AnimationManager()
    {
        this.displays = new ArrayList<>();
    }

    public AnimationManager(List<SimulationDisplay> displays)
    {
        this();
        this.displays.addAll(displays);
    }

    public AnimationManager(SimulationDisplay... displays)
    {
        this(Arrays.asList(displays));

    }

    public void add(SimulationDisplay... displays)
    {
        this.displays.addAll(Arrays.asList(displays));

    }

    public void animate(SimulationEntity obj)
    {
        for (SimulationDisplay disp : this.displays)
        {
            if (disp.isDisplayEnabled())
            {
                disp.animate(obj);
            }
        }
    }
}
