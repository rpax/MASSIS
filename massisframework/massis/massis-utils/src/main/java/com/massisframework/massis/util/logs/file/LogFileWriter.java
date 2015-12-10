package com.massisframework.massis.util.logs.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;

import com.massisframework.massis.util.gson.CompressorProcessor;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.io.ZipOutputStreamWriter;
import com.massisframework.massis.util.logs.LogsConstants;


public class LogFileWriter implements LogsConstants{

	protected ZipOutputStreamWriter writer;
	protected Gson gson;
	protected boolean isClosed = false;
	protected CompressorProcessor processor;

	public LogFileWriter(String filepath) throws IOException {

		this.processor = new CompressorProcessor();

		this.gson = processor.createBuilder().createGson();
		ZipOutputStream zout = new ZipOutputStream(
				new BufferedOutputStream(new FileOutputStream(filepath)));
		
		writer = new ZipOutputStreamWriter(zout);
		
		
		writer.putNextEntry(new ZipEntry(SIMULATION_LOG_FILENAME));
	}

	
	public void write(long stepId, JsonState state)
			throws Exception {

		write(stepId, new JsonState[]{state});

	}

	
	public void close() throws IOException {
		if (isClosed)
			return;
		writer.flush();
		writer.closeEntry();
		writer.putNextEntry(new ZipEntry(COMPRESSION_MAP_FILENAME));
		this.gson.toJson(this.processor.getCompressionKeyValueArray(), this.writer);
		writer.flush();
		System.err.println("close()");

		
		writer.closeEntry();
		writer.finish();
		writer.close();
		this.isClosed = true;
	}

	
	public void write(long stepId, JsonState[] states)
			throws Exception {
		this.gson.toJson(states, this.writer);
		this.writer.write("\n");
	}


	
}
