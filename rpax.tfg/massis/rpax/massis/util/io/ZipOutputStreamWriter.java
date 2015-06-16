package rpax.massis.util.io;

import java.io.IOException;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * Writes into a zipped Stream
 * @author rpax
 *
 */
public class ZipOutputStreamWriter extends Writer {

	private final ZipOutputStream zipStream;

	public ZipOutputStreamWriter(ZipOutputStream zipStream) {
		this.zipStream = zipStream;

	}

	@Override
	public void write(char[] buffer, int off, int len) throws IOException {
		final char[] cbuf2 = new char[len];
		System.arraycopy(buffer, off, cbuf2, 0, len);
		this.zipStream.write(new String(cbuf2).getBytes());
	}

	public void closeEntry() throws IOException {
		this.zipStream.closeEntry();
	}

	public void finish() throws IOException {
		this.zipStream.finish();
	}

	public void putNextEntry(ZipEntry e) throws IOException {
		this.zipStream.putNextEntry(e);
	}

	@Override
	public void close() throws IOException {
		zipStream.finish();
		zipStream.close();
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		this.zipStream.write(new String(cbuf).getBytes());
	}

	@Override
	public void write(String str) throws IOException {
		this.zipStream.write(str.getBytes());
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		this.zipStream.write(str.substring(off, off + len).getBytes());
	}

	@Override
	public void flush() throws IOException {
		this.zipStream.flush();
	}

}