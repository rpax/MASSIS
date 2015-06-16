package rpax.massis.tests;

import java.util.HashMap;
import java.util.Map;

import rpax.massis.model.agents.MetadataConstants;
import rpax.massis.sh3d.plugins.metadata.MetaDataUtils;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;

public class Renamer {

	public static void main(String[] args) {
		String homeFilePath = "/home/rpax/Desktop/example.sh3d";
		String saveHomePath = "/home/rpax/Desktop/example2.sh3d";
		try
		{
			Home home = new HomeFileRecorder().readHome(homeFilePath);
			for (HomePieceOfFurniture hpof : home.getFurniture())
			{
				Map<String, String> metadata = MetaDataUtils.getMetaData(home,
						hpof);
				if (metadata.containsKey("classname"))
				{
//					if (
//							metadata.get("classname").equals("furniture") ||
//							metadata.get("Person").equals("chair")
//						
//						)
//					{
//						HashMap<String, String> meta = new HashMap<>(metadata);
//						meta.remove("Person");
//						meta.put(MetadataConstants.CLASS_NAME,
//								"rpax.massis.model.people.FurnitureAgent");
//						
//						MetaDataUtils.setMetaData(home, hpof, meta);
//					}
//					else
//					{
//						HashMap<String, String> meta = new HashMap<>(metadata);
//						meta.remove("Person");
//						meta.put(MetadataConstants.CLASS_NAME,
//								"rpax.massis.model.people.SPOSHAgent");
//						meta.put(MetadataConstants.PLAN_FILE_PATH,
//								"plans/EvacuationSimple.lap");
//						MetaDataUtils.setMetaData(home, hpof, meta);
//					}
					
					
					HashMap<String, String> meta = new HashMap<>(metadata);
					String className = metadata.get(MetadataConstants.CLASS_NAME);					
					meta.put(MetadataConstants.CLASS_NAME,className.replace("rpax.massis.model.people", "rpax.massis.model.agents"));
					
					MetaDataUtils.setMetaData(home, hpof, meta);
				}
			}

			new HomeFileRecorder().writeHome(home, saveHomePath);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
