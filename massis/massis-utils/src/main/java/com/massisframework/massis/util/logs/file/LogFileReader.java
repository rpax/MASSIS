package com.massisframework.massis.util.logs.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.massisframework.massis.util.gson.CompressorProcessor;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.logs.LogsConstants;

public class LogFileReader implements LogsConstants{

	protected BufferedReader reader;
	protected Gson gson;
	protected CompressorProcessor processor;

	public LogFileReader(String zipFilePath) throws IOException {

		try (Reader r = openReaderAtEntry(zipFilePath, COMPRESSION_MAP_FILENAME))
		{

			processor = new CompressorProcessor(new Gson().fromJson(r,
					String[][].class));
			if (processor == null)
			{
				throw new IOException("Error when processing \""
						+ COMPRESSION_MAP_FILENAME + "\"");
			}
		}
		this.gson = processor.createBuilder().createGson();
		reader = openReaderAtEntry(zipFilePath, SIMULATION_LOG_FILENAME);
		System.err.println("Reader ready");
	}

	
	public Collection<JsonState> getStatesAt(long step) throws Exception {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	
	public void close() throws Exception {
		if (this.reader != null)
		{
			this.reader.close();
		}

	}

	
	public Collection<JsonState> getChangedStatesAt(long step) throws Exception {

		throw new UnsupportedOperationException("Not implemented yet");
	}

	public JsonState[] nextStep() throws Exception {
		String line = reader.readLine();
		if (line == null)
		{
			return new JsonState[]{};
		}
		return this.gson.fromJson(line, JsonState[].class);

	}

	private BufferedReader openReaderAtEntry(String zipFilePath,
			String zipEntryname) throws IOException {
		ZipInputStream zin = new ZipInputStream(new FileInputStream(new File(
				zipFilePath)));
		ZipEntry entry;
		while ((entry = zin.getNextEntry()) != null
				&& !zipEntryname.equals(entry.getName()))
			;
		return new BufferedReader(new InputStreamReader(zin));
	}
}
