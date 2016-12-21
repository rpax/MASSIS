package com.massisframework.massis.sim;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.BuildingProgressMonitor;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.util.io.SimulationSaver;

public class Simulation extends AbstractSimulation {

    private static final long serialVersionUID = 1L;
    protected SimulationSaver simulationSaver;

    public Simulation(long seed, String buildingFilePath, String resourcesPath,
            String outputFileLocation, BuildingProgressMonitor buildingProgress)
    {
        super(seed, buildingFilePath, resourcesPath, outputFileLocation,
                buildingProgress);
        if (this.outputFileLocation != null)
        {
            try
            {
                final File outputFile = new File(this.outputFileLocation);

                this.simulationSaver = new SimulationSaver(this.buildingFile,
                        outputFile);
            } catch (Exception ex)
            {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE,
                        null, ex);
                System.exit(1);
            }
        }

    }

    public Simulation(long seed, String buildingFilePath, String resourcesPath,
            String saveLocation)
    {
        this(seed, buildingFilePath, resourcesPath, saveLocation, null);
    }

    public SimulationSaver getSimulationSaver()
    {
        return simulationSaver;
    }

    @Override
    public void start()
    {
        super.start();
        // Schedule a todos los agentes
        for (HighLevelController hlc : this.building.getScheduledControllers())
        {
            this.schedule.scheduleRepeating(hlc);
        }
        for (Floor f : this.building.getFloors())
        {
            for (SimRoom r : f.getRooms())
            {
                this.schedule.scheduleRepeating(r);
            }
        }
        if (this.simulationSaver != null)
        {
            for (Floor f : this.building.getFloors())
            {
                for (LowLevelAgent agent : f.getAgents())
                {
                    agent.addRestorableObserver(simulationSaver);
                }
            }
            this.schedule.scheduleRepeating(this.simulationSaver);
        }
    }

    @Override
    protected void endSimulation()
    {
        for (Floor f : this.building.getFloors())
        {
            for (SimRoom r : f.getRooms())
            {
                r.stop();
            }
        }
        for (HighLevelController hlc : this.building.getScheduledControllers())
        {
            hlc.stop();
        }
        if (this.simulationSaver != null)
        {
            this.simulationSaver.stop();
        }
    }
}
