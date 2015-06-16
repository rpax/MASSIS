/**
 * 
 */
package rpax.massis.sh3d.plugins.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;


/**
 * Recupera/guarda metadatos
 * 
 * @author rpax
 * 
 */
public final class MetaDataUtils {
	public static final Map<String, String> getMetaData(Home home,
			Serializable s) {
		
		if (s instanceof HomePieceOfFurniture)
		{
			HashMap<String,String> meta=new HashMap<String,String>(MASSISHomeMetadataManager.getHomeMetaData(home).getMetadata(s));
			StringBuilder sb = new StringBuilder();
			HomePieceOfFurniture hpof = (HomePieceOfFurniture) s;
			if (!isNullOrEmpty(hpof.getDescription()))
			{
				String[] lines = hpof.getDescription().split("\n");
				for (String line : lines)
				{
					if (!line.contains("="))
						continue;
					String[] kv = line.split("=");
					if (kv != null && kv.length == 2 && !kv[0].equals(MASSISHomeMetadataManager.ID_KEY))
						meta.put(kv[0], kv[1]);
				}
				
				for (Entry<String, String> entry : meta.entrySet())
				{
					sb.append(entry.getKey()).append('=')
							.append(entry.getValue()).append('\n');

				}
				hpof.setDescription(sb.toString());
			}
			MASSISHomeMetadataManager.getHomeMetaData(home).addMetaData(s, meta);
		}
		
		
		return MASSISHomeMetadataManager.getHomeMetaData(home)
				.getMetadata(s);
	}

	private static boolean isNullOrEmpty(String str) {
		return str==null || str.equals("");
	}

	public static final void setMetaData(Home home, Serializable s,
			HashMap<String, String> metaData) {

		metaData.remove("");

		Map<String, String> oldMetadata = getMetaData(home, s);
		for (String key : oldMetadata.keySet().toArray(new String[] {}))
		{
			MASSISHomeMetadataManager.getHomeMetaData(home).removeMetadata(s, key);
		}

		for (Entry<String, String> entry : metaData.entrySet())
		{
			MASSISHomeMetadataManager.getHomeMetaData(home).setMetaData(s,
					entry.getKey(), entry.getValue());
		}
		if (s instanceof HomePieceOfFurniture)
		{
			StringBuilder sb = new StringBuilder();
			HomePieceOfFurniture hpof = (HomePieceOfFurniture) s;

			for (Entry<String, String> entry : metaData.entrySet())
			{
				sb.append(entry.getKey()).append('=').append(entry.getValue())
						.append('\n');

			}
			hpof.setDescription(sb.toString());
		}

	}
}
