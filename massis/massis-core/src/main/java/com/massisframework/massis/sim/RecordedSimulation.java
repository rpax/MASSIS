package com.massisframework.massis.sim;

import java.io.IOException;

import com.eteks.sweethome3d.model.RecorderException;
import com.massisframework.massis.model.building.Building.BuildingProgressMonitor;
import com.massisframework.massis.model.building.IBuilding;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.logs.file.LogFileReader;
import com.massisframework.massis.util.logs.file.async.AsyncLogFileReader;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class RecordedSimulation extends AbstractSimulation {

    private static final long serialVersionUID = 1L;
    private SimulationPlayer simPlayer;

    public RecordedSimulation(long seed, String buildingFilePath,
            String resourcesPath, BuildingProgressMonitor progressMonitor,
            String logFileLocation)
    {
        super(seed, buildingFilePath, resourcesPath,
                logFileLocation, progressMonitor);
    }

    public RecordedSimulation(long seed, String buildingFilePath,
            String resourcesPath, String logFileLocation)
    {
        super(seed, buildingFilePath, resourcesPath, logFileLocation);
    }

    @Override
    public void start()
    {
        super.start();
        try
        {
            this.simPlayer = new SimulationPlayer(this.outputFileLocation);
        } catch (ClassNotFoundException | IOException e)
        {
            throw new RuntimeException(e);
        }
        this.schedule.scheduleRepeating(this.simPlayer);
    }

    @Override
    protected void endSimulation()
    {
        this.simPlayer.stop();
    }

    @Override
    protected IBuilding createBuilding() throws RecorderException
    {
        return super.createBuilding();
    }

    private class SimulationPlayer implements Steppable, Stoppable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private final LogFileReader lr;

        public SimulationPlayer(String zipFilePath)
                throws ClassNotFoundException, IOException
        {
            this.lr = new AsyncLogFileReader(zipFilePath);
        }

        @SuppressWarnings("unchecked")
		@Override
        public void step(SimState state)
        {
            try
            {

                for (final JsonState<IBuilding> ks : this.lr.nextStep())
                {
                    ks.restore(RecordedSimulation.this.building);
                }

            } catch (final Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void stop()
        {
            try
            {
                this.lr.close();
            } catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
