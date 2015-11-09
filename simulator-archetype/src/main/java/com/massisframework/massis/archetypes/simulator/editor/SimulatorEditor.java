package com.massisframework.massis.archetypes.simulator.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eteks.sweethome3d.plugin.Plugin;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataReader;
import com.massisframework.sweethome3d.additionaldata.AdditionalDataWriter;
import com.massisframework.sweethome3d.additionaldata.SweetHome3DAdditionalDataApplication;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;
import com.massisframework.sweethome3d.plugins.BuildingMetadataPlugin;

public class SimulatorEditor {

	public static void main(String[] args) {
		HomeMetadataLoader metadataLoader = new HomeMetadataLoader();
		List<? extends AdditionalDataWriter> writers = Arrays.asList(metadataLoader);
		List<? extends AdditionalDataReader> loaders = Arrays.asList(metadataLoader);

		List<Class<? extends Plugin>> plugins = new ArrayList<>();
		plugins.add(BuildingMetadataPlugin.class);
		SweetHome3DAdditionalDataApplication.run(new String[] {}, loaders, writers, plugins);
	}

}
