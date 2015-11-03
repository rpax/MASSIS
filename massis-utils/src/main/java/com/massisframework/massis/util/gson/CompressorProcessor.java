package com.massisframework.massis.util.gson;

import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;
import io.gsonfire.PreProcessor;
import io.gsonfire.TypeSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.massisframework.massis.util.io.JsonState;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Compressor for JSON States
 * 
 * @author rpax
 * 
 */
public class CompressorProcessor implements PostProcessor<JsonState>,
		PreProcessor<JsonState>, TypeSelector<JsonState> {
	/**
	 * The prefix for every compressed key
	 */
	private static final String COMPRESSED_PREFIX = "@";
	/**
	 * The available substitution symbols
	 */
	private static final char[] COMPRESSED_SYMBOLS = { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z' };
	/**
	 * uncompressed - compressed equivalence names map
	 */
	private final HashMap<String, String> uncompressed_c = new HashMap<>();
	/**
	 * compressed - uncompressed equivalence map
	 */
	private final HashMap<String, String> compressed_u;

	/**
	 * Creates an ewmpty compressor.
	 */
	public CompressorProcessor() {
		compressed_u = new HashMap<>();

	}

	/**
	 * Creates a compressor
	 * 
	 * @param cKeys
	 *            a map relating the compressed keys with the uncompressed ones
	 */
	public CompressorProcessor(Map<String, String> cKeys) {
		this.compressed_u = new HashMap<>(cKeys);
	}

	/**
	 * The same as {@link #CompressorProcessor(Map)}, but with a list of
	 * String[] of length 2 representing K-V pairs.
	 * 
	 * @param cKeys
	 *            a map relating the compressed keys with the uncompressed ones
	 */
	public CompressorProcessor(ArrayList<String[]> cKeys) {
		this.compressed_u = new HashMap<String, String>();
		for (String[] pair : cKeys)
		{
			this.compressed_u.put(pair[0], pair[1]);
		}
	}

	/**
	 * The same as {@link #CompressorProcessor(ArrayList)}, but instead of using
	 * an arraylist uses an array
	 * 
	 * @param cKeys
	 *            a map relating the compressed keys with the uncompressed ones
	 */
	public CompressorProcessor(String[][] cKeys) {
		this.compressed_u = new HashMap<String, String>();
		for (String[] pair : cKeys)
		{
			this.compressed_u.put(pair[0], pair[1]);
		}
	}

	/**
	 * Maximum id at the moment
	 */
	private int maxId = 0;

	/**
	 * Executes the uncompression of a (possibly) compressed {@link JsonElement}
	 * 
	 * @param json
	 *            the element to uncompress
	 */
	public void processUnCompression(JsonElement json) {
		/*
		 * Depending on the type, the element should be treated in different
		 * ways. Until it is a JSONObject, it call recursively to this method.
		 */
		if (json.isJsonArray())
		{
			JsonArray array = json.getAsJsonArray();

			for (JsonElement elem : array)
			{
				processUnCompression(elem);
			}
		}
		else if (json.isJsonObject())
		{
			JsonObject object = json.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries = object.entrySet();
			ArrayList<StringJsonTuple> compressedKeys = new ArrayList<>();
			for (Entry<String, JsonElement> entry : entries)
			{
				processUnCompression(entry.getValue());
				if (this.isCompressed(entry.getKey()))
				{
					compressedKeys.add(new StringJsonTuple(entry));
				}
			}
			/**
			 * NOW it is a String. Proceed to the uncompression
			 */
			for (StringJsonTuple entry : compressedKeys)
			{
				JsonElement data = object.remove(entry.getKey());

				JsonElement uncompressedData = uncompressJsonPrimitiveString(data);

				object.add(this.getUncompressed(entry.getKey()),
						uncompressedData);

			}

		}
	}

	/**
	 * uncompression of a JSON representing compressed string
	 * 
	 * @param json
	 *            the json
	 * @return the same json string, but uncompressed.
	 */
	public JsonElement uncompressJsonPrimitiveString(JsonElement json) {
		if (json.isJsonPrimitive())
		{
			JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
			if (jsonPrimitive.isString())
			{
				String str = jsonPrimitive.getAsString();
				if (this.isCompressed(str))
				{

					return new JsonPrimitive(this.getUncompressed(str));
				}
			}
		}
		return json;
	}

	/**
	 * Checks whetether it is possible to compress this jsonElement and compress
	 * it if it is.
	 * 
	 * @param json
	 * @return
	 */
	public JsonElement compressJsonIfPossible(JsonElement json) {
		if (json.isJsonPrimitive())
		{
			JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
			if (jsonPrimitive.isString())
			{
				String str = jsonPrimitive.getAsString();

				return new JsonPrimitive(this.compressString(str));

			}
		}
		return json;
	}
	
	public void processCompression(JsonElement json) {
		if (json.isJsonArray())
		{
			JsonArray array = json.getAsJsonArray();

			for (JsonElement elem : array)
			{
				processCompression(elem);
			}
		}
		else if (json.isJsonObject())
		{
			JsonObject object = json.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries = object.entrySet();
			ArrayList<StringJsonTuple> entryCopy = StringJsonTuple
					.getAsStringJsonTuple(entries);
			for (Entry<String, JsonElement> entry : entries)
			{
				processCompression(entry.getValue());
			}
			for (StringJsonTuple entry : entryCopy)
			{
				JsonElement data = object.remove(entry.getKey());
				JsonElement compressedData = compressJsonIfPossible(data);

				object.add(this.compressString(entry.getKey()), compressedData);

			}

		}
	}

	private String getUncompressed(String compressed) {
		return compressed_u.get(compressed);
	}

	private static Pattern alphanumeric = Pattern.compile("^[a-zA-Z0-9]*$");

	private boolean isCompressed(String compressed) {

		return (compressed.startsWith(COMPRESSED_PREFIX) && alphanumeric
				.matcher(compressed.substring(COMPRESSED_PREFIX.length()))
				.matches());

	}

	private String compressString(String uncompressed) {
		if (!this.uncompressed_c.containsKey(uncompressed))
		{
			final int id = this.maxId;
			final String compressedString = getCompressedFormattedString(id);
			if (compressedString.length() >= uncompressed.length())
				return uncompressed;

			this.uncompressed_c.put(uncompressed, compressedString);
			this.compressed_u.put(compressedString, uncompressed);
			this.maxId++;
			return compressedString;

		}
		return this.uncompressed_c.get(uncompressed);
	}

	private static String getCompressedFormattedString(int i) {
		char buf[] = new char[65];
		int radix = 62;
		int charPos = 64;
		i = -i;
		while (i <= -radix)
		{
			buf[charPos--] = COMPRESSED_SYMBOLS[-(i % radix)];
			i = i / radix;
		}
		buf[charPos] = COMPRESSED_SYMBOLS[-i];

		return COMPRESSED_PREFIX
				.concat(new String(buf, charPos, (65 - charPos)));
	}

	@Override
	public void postDeserialize(JsonState result, JsonElement src, Gson gson) {
	}

	@Override
	public void postSerialize(JsonElement result, JsonState src, Gson gson) {

		result.getAsJsonObject().addProperty(JsonState.KIND_KEY_NAME,
				src.getClass().getName());
		processCompression(result);

	}

	@Override
	public void preDeserialize(Class<? extends JsonState> clazz,
			JsonElement src, Gson gson) {
		processUnCompression(src);
	}

	public Set<Entry<String, String>> getCompressionEntrySet() {
		return this.compressed_u.entrySet();
	}

	public String[][] getCompressionKeyValueArray() {
		int i = 0;
		String[][] ret = new String[this.compressed_u.entrySet().size()][2];
		for (Entry<String, String> entry : this.compressed_u.entrySet())
		{
			ret[i][0] = entry.getKey();
			ret[i][1] = entry.getValue();
			i++;
		}
		return ret;
	}

	public Map<String, String> getCompressionMap() {
		return Collections.unmodifiableMap(this.compressed_u);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends JsonState> getClassForElement(JsonElement readElement) {

		String kind = readElement.getAsJsonObject()
				.get(JsonState.KIND_KEY_NAME).getAsString();
		try
		{
			return (Class<? extends JsonState>) Class.forName(kind);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static class StringJsonTuple {
		private final String string;
		private final JsonElement elem;

		public StringJsonTuple(Entry<String, JsonElement> entry) {
			this.string = entry.getKey();
			this.elem = entry.getValue();
		}

		public String getKey() {
			return this.string;
		}

		@SuppressWarnings("unused")
		public JsonElement getValue() {
			return this.elem;
		}

		public static ArrayList<StringJsonTuple> getAsStringJsonTuple(
				Set<Entry<String, JsonElement>> entries) {
			ArrayList<StringJsonTuple> tuples = new ArrayList<>(entries.size());
			for (Entry<String, JsonElement> entry : entries)
			{
				tuples.add(new StringJsonTuple(entry));
			}
			return tuples;
		}
	}

	public GsonFireBuilder createBuilder() {
		GsonFireBuilder builder = new GsonFireBuilder();
		builder.enableExposeMethodResult();
		builder.registerPreProcessor(JsonState.class, this);
		builder.registerPostProcessor(JsonState.class, this);
		builder.registerTypeSelector(JsonState.class, this);
		return builder;
	}

}
