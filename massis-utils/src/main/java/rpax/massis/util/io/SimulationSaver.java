package rpax.massis.util.io;

import com.eteks.sweethome3d.model.RecorderException;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpax.massis.util.gson.CompressorProcessor;
import rpax.massis.util.io.storage.DefaultMassisStorage;
import rpax.massis.util.io.storage.MassisStorage;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class SimulationSaver implements Steppable, Stoppable, RestorableObserver {

    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_MAX_QUEUE_SIZE = 50;
    long currentStep;
    private final ArrayBlockingQueue<JsonState[]> queue;
    private boolean closed = false;
    private final HashSet<Restorable> changedObjects = new HashSet<>();
    private List<JsonState> states = new ArrayList<>();
    private final MassisStorage destination;
    private final CompressorProcessor processor;
    private final Gson gson;
    private final BufferedWriter logBufferedWriter;
    private boolean started;
    private final Thread writerThread;
    private final Object lock = new Object();

    public SimulationSaver(MassisStorage original, File destination) throws IOException, RecorderException, ClassNotFoundException
    {
        this(original, destination, DEFAULT_MAX_QUEUE_SIZE);
    }

    public SimulationSaver(MassisStorage original, File destinationFile,
            int max_queue_size) throws IOException, RecorderException, ClassNotFoundException
    {
        
        this.queue = new ArrayBlockingQueue<>(max_queue_size);
        this.destination = DefaultMassisStorage.getStorage(destinationFile);
        this.destination.saveHome(original.loadHome());
        this.destination.saveMetadata(original.loadMetadata());
        
        this.processor = new CompressorProcessor();
        this.gson = this.processor.createBuilder().createGson();

        final OutputStream logOS = this.destination.getLogOutputStream();
        final OutputStreamWriter osw = new OutputStreamWriter(logOS);
        this.logBufferedWriter = new BufferedWriter(osw);
        this.started = false;
        this.writerThread = new Thread(new LogWriterRunnable());
        this.writerThread.start();
    }

    @Override
    public void stop()
    {
        if (this.closed)
        {
            return;
        }
        try
        {
            synchronized (lock)
            {
                this.closed = true;
                while (!queue.isEmpty())
                {
                    this.gson.toJson(queue.poll(), this.logBufferedWriter);
                    this.logBufferedWriter.write("\n");
                }
                /*
                 * Flush & close. There's no need to write anything else.
                 */
                logBufferedWriter.flush();
                logBufferedWriter.close();
                /*
                 * Save the compression map
                 */
                String[][] compressionMap = this.processor.getCompressionKeyValueArray();
                this.destination.saveCompressionMap(compressionMap);

            }
//            synchronized (writerThread)
//            {
//                writerThread.interrupt();
//            }
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        this.stop();
    }

    @Override
    public void step(SimState simState)
    {
        checkStarted();
        try
        {

            this.queue.put(this.states.toArray(new JsonState[this.states.size()]));
            System.err.println("SIZE STATES "+this.states.size());

        } catch (InterruptedException ex)
        {
            Logger.getLogger(SimulationSaver.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        this.changedObjects.clear();
        this.states.clear();
    }

    @Override
    public void notifyChange(Restorable restorable, JsonState state)
    {
        this.states.add(state);
    }

    private void checkStarted()
    {
        if (!started)
        {
            this.started = true;
           
        }
    }

    private class LogWriterRunnable implements Runnable {

        @Override
        public void run()
        {
            try
            {
                while (!SimulationSaver.this.closed)
                {
                    JsonState[] states = queue.take();
                    synchronized (lock)
                    {
                        gson.toJson(states, logBufferedWriter);
                        logBufferedWriter.write("\n");
                        logBufferedWriter.flush();
                    }
                }
            } catch (InterruptedException ex)
            {
                Logger.getLogger(SimulationSaver.class.getName()).log(
                        Level.INFO,
                        "Writer Thread interrupted");
            } catch (IOException ex)
            {
                Logger.getLogger(SimulationSaver.class.getName()).log(
                        Level.SEVERE,
                        null, ex);
            }



        }
    }
}
