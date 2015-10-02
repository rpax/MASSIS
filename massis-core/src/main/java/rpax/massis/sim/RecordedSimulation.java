package rpax.massis.sim;

import java.io.IOException;

import rpax.massis.util.io.JsonState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpax.massis.model.building.Building.BuildingProgressMonitor;
import rpax.massis.util.gson.CompressorProcessor;
import rpax.massis.util.io.storage.MassisStorage;

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
        
        try
        {
            this.simPlayer = new SimulationPlayer(this.storage);
            super.start();
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

    private class SimulationPlayer implements Steppable, Stoppable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private ArrayBlockingQueue<JsonState[]> states;
        private final InputStream logInputStream;
        private final Thread readerThread;
        private final Gson gson;
        private boolean finished;

        public SimulationPlayer(MassisStorage storage)
                throws ClassNotFoundException, IOException
        {

            this.states = new ArrayBlockingQueue<>(50);
            this.logInputStream = storage.getLogInputStream();
            final String[][] compressionMap = storage.loadCompressionMap();
            final CompressorProcessor compressorProcessor = new CompressorProcessor(
                    compressionMap);
            this.gson = compressorProcessor.createBuilder().createGson();
            this.finished = false;
            this.readerThread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(logInputStream)))
                    {
                        String line;
                        while ((line = reader.readLine()) != null)
                        {
                            JsonState[] stepState = gson.fromJson(line,
                                    JsonState[].class);
                            states.put(stepState);
                        }

                    } catch (InterruptedException ex)
                    {
                        Logger.getLogger(
                                RecordedSimulation.class.getName()).log(
                                Level.INFO,
                                null, "Reader Thread interrupted");
                    } catch (IOException ex)
                    {
                        Logger.getLogger(RecordedSimulation.class.getName()).log(
                                Level.SEVERE,
                                null, ex);
                    }
                    finished = true;
                }
            });
            readerThread.start();
        }

        @Override
        public void step(SimState state)
        {

            if (finished)
            {
                return;
            }
            try
            {
                final JsonState[] stepsStates = this.states.take();
                for (JsonState js : stepsStates)
                {
                    js.restore(building);
                }
            } catch (InterruptedException ex)
            {
                Logger.getLogger(RecordedSimulation.class.getName()).log(
                        Level.INFO,
                        null, "Queue wait stage interrupted");
            }



        }

        @Override
        public void stop()
        {
            this.readerThread.interrupt();
        }
    }
}
