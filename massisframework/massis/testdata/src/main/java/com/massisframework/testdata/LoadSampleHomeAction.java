package com.massisframework.testdata;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;

import com.eteks.sweethome3d.plugin.PluginAction;
import com.massisframework.testdata.gui.SampleHomeGUIControl;
import com.massisframework.testdata.gui.SampleHomesGUI;

/**
 * Shows & loads test data homes
 * 
 * @author rpax
 *
 */
public class LoadSampleHomeAction extends PluginAction {

	private final TestDataPlugin plugin;

	public LoadSampleHomeAction(TestDataPlugin plugin)
	{
		super(LoadSampleHomeAction.class.getPackage().getName()
				+ ".ApplicationPlugin",
				LoadSampleHomeAction.class.getName(),
				plugin.getPluginClassLoader(), true);
		this.plugin=plugin;
		// Sections: Help->Sample Homes
		putPropertyValue(Property.MENU, "Help");
		putPropertyValue(Property.NAME, "Load Sample Home...");
	}

	@Override
	public void execute()
	{
		SampleHomesLoader.listAvailable();
		final SampleHomeGUIControl control = new SampleHomeGUIControl() {

			@Override
			public void sampleHomesListValueChanged(SampleHomesGUI gui,
					ListSelectionEvent evt)
			{
				if (evt.getValueIsAdjusting())
				{
					return;
				}
				updateGUI(gui);

			}

			@Override
			public void loadHomeButtonActionPerformed(SampleHomesGUI gui,
					ActionEvent evt)
			{
				// Close the frame
				gui.dispatchEvent(
						new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
				// Load the home.
				final String selectedHomeName = gui.getSelectedHomeName();
				File f;
				try
				{
					f = SampleHomesLoader
							.loadHomeTempFile(selectedHomeName);
					LoadSampleHomeAction.this.plugin.getHomeController().open(f.getAbsolutePath());
					
				} catch (final IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		final SampleHomesGUI gui = new SampleHomesGUI(control);

		final ArrayList<String> listValues = new ArrayList<>();
		final List<SampleHomeDescription> descriptions = SampleHomesLoader
				.listAvailable();
		for (final SampleHomeDescription homeDesc : descriptions)
		{
			listValues.add(homeDesc.getFilename());

		}
		gui.setHomeListValues(listValues);
		gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		gui.setVisible(true);

	}

	private static void updateGUI(SampleHomesGUI gui)
	{
		final String selectedHomeName = gui.getSelectedHomeName();
		try
		{
			final SampleHomeDescription desc = SampleHomesLoader
					.loadDescription(selectedHomeName.replace(".sh3d", ""));
			gui.setImage(desc.getImage());
			gui.setDescription(desc.getDescription());
		} catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
