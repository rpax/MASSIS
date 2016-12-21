package com.massisframework.massis.util.logs.file.async;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.gson.JsonSyntaxException;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis   .util.logs.file.LogFileReader;

public class AsyncLogFileReader extends LogFileReader implements
		AsyncLogConstants {

	private final ArrayBlockingQueue<JsonState[]> queue = new ArrayBlockingQueue<>(
			MAX_QUEUE_SIZE + 1);
	private Thread readerThread;
	private boolean closed = false;
	private boolean ended = false;
	private final Object lock = new Object();

	public AsyncLogFileReader(String zipFilePath) throws IOException {
		super(zipFilePath);
		readerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
					while (!ended && !closed)
					{

						synchronized (lock)
						{
							if (!closed)
							{

								String line = reader.readLine();
								if (line == null)
								{
									ended = true;
								}
								else
								{
									queue.put(gson.fromJson(line,JsonState[].class));
//									System.out.println("QUEUE SIZE: "
//											+ queue.size());
								}
							}
						}

					}
					System.err.println("Ended: " + ended + ". closed: "
							+ closed);

				}
				catch (JsonSyntaxException | IOException e)
				{
					e.printStackTrace();
					return;
				}
				catch (InterruptedException e)
				{
					System.err.println("Interrupted");
					return;
				}

			}
		});
		readerThread.start();
	}

	@Override
	public JsonState[] nextStep() throws Exception {
		if (this.ended && queue.size() == 0)
			return new JsonState[]{};
		return queue.take();
	}

	@Override
	public void close() throws Exception {
		if (this.reader != null && !closed)
		{
			synchronized (lock)
			{
				this.closed = true;
				this.readerThread.join();
				this.reader.close();
				this.queue.clear();
			}
		}

	}

}
