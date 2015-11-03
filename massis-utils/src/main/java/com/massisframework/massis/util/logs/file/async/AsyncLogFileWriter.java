package com.massisframework.massis.util.logs.file.async;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.ZipEntry;

import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.logs.file.LogFileWriter;

public class AsyncLogFileWriter extends LogFileWriter implements
		AsyncLogConstants {
	private final ArrayBlockingQueue<JsonState[]> queue = new ArrayBlockingQueue<>(
			MAX_QUEUE_SIZE + 1);
	
	private Thread writerThread;
	private final Object lock = new Object();

	public AsyncLogFileWriter(String filepath) throws IOException {
		super(filepath);
		writerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{

					while (!isClosed)
					{
						//System.err.println("QSIZE: " + queue.size());
						JsonState[] states = queue.take();
						synchronized (lock)
						{
							if (!isClosed)
							{
								gson.toJson(states, writer);
								writer.write("\n");
							}
						}
					}
					System.err.println("Stream closed");
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return;
				}
				catch (InterruptedException e)
				{
					
				}
			}
		});
		writerThread.start();
	}

	@Override
	public void write(long stepId, JsonState[] states)
			throws IOException {
		try
		{
			this.queue.put(states);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (lock)
		{
			if (isClosed)
				return;
			this.isClosed = true;
			synchronized (writerThread)
			{
				writerThread.interrupt();
			}
			while (!queue.isEmpty())
			{
				JsonState[] states = queue.poll();
				this.gson.toJson(states, this.writer);
				this.writer.write("\n");
			}

			writer.flush();
			writer.closeEntry();
			writer.putNextEntry(new ZipEntry(COMPRESSION_MAP_FILENAME));
			this.gson.toJson(this.processor.getCompressionKeyValueArray(),
					this.writer);
			writer.flush();
			writer.closeEntry();
			writer.finish();
			writer.close();
			System.err.println("closed");
		}
	}

}
