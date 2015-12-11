package archetypes.hellosimulator;

import java.util.Arrays;
import java.util.List;

import com.eteks.sweethome3d.plugin.Plugin;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataReader;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataWriter;
import com.massisframework.sweethome3d.additionaldata.SweetHome3DAdditionalDataApplication;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;
import com.massisframework.sweethome3d.plugins.BuildingMetadataPlugin;
import com.massisframework.testdata.SampleHomesLoader;
import com.massisframework.testdata.TestDataPlugin;

public class EnvironmentEditor {

	public static void main(String[] args)
	{
		SampleHomesLoader.class.getName();
		final HomeMetadataLoader metadataLoader = new HomeMetadataLoader();
		final List<? extends AdditionalDataWriter> writers = Arrays
				.asList(metadataLoader);
		final List<? extends AdditionalDataReader> loaders = Arrays
				.asList(metadataLoader);

		final List<Class<? extends Plugin>> plugins = Arrays.asList(
				BuildingMetadataPlugin.class, TestDataPlugin.class);
		SweetHome3DAdditionalDataApplication.run(new String[] {}, loaders,
				writers, plugins);
	}

}
