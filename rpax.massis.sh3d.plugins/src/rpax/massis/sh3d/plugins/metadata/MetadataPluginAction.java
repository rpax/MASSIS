package rpax.massis.sh3d.plugins.metadata;

import java.awt.GridLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rpax.massis.sh3d.plugins.MASSISPluginAction;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.SelectionEvent;
import com.eteks.sweethome3d.model.SelectionListener;

/**
 * @author rpax
 */
public class MetadataPluginAction extends MASSISPluginAction {

	
	private List<Selectable> selectedFurniture = Collections
			.emptyList();

	/**
	 * @param metadataPlugin
	 */
	public MetadataPluginAction(final Home home) {
		super(home, MetadataPluginAction.class);
		
		putPropertyValue(Property.MENU, "Tools");
		putPropertyValue(Property.NAME, "Add Metadata");
		//
		//Transicion
		
		
		for (HomePieceOfFurniture f : home.getFurniture())
		{
			MetaDataUtils.getMetaData(home, f);
		}
		//
		
		
		setEnabled(false);
		home.addSelectionListener(
				new SelectionListener() {
					public void selectionChanged(SelectionEvent ev) {
						selectedFurniture = new ArrayList<Selectable>();
					//	metadataPlugin.getHome();
						selectedFurniture.addAll(Home.getWallsSubList(home.getSelectedItems()));
						//metadataPlugin.getHome();
						selectedFurniture.addAll(Home.getFurnitureSubList(home.getSelectedItems()));
					///	metadataPlugin.getHome();
						selectedFurniture.addAll(Home.getRoomsSubList(home.getSelectedItems()));
						if (selectedFurniture.isEmpty()
								|| selectedFurniture.size() != 1)
						{
							setEnabled(false);
						}
						else
						{
							setEnabled(true);
						}
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eteks.sweethome3d.plugin.PluginAction#execute()
	 */
	@Override
	public void execute() {
		if (selectedFurniture.isEmpty() || selectedFurniture.size() != 1)
			return;
		Selectable furniture = selectedFurniture.get(0);

		ArrayList<JTextField> keys = new ArrayList<JTextField>();
		ArrayList<JTextField> values = new ArrayList<JTextField>();
		keys.add(new JTextField(""));
		values.add(new JTextField(""));

		Map<String, String> initialMetaData = MetaDataUtils
				.getMetaData(home,(Serializable) furniture);
		for (Entry<String, String> entry : initialMetaData.entrySet())
		{
			keys.add(new JTextField(entry.getKey()));
			values.add(new JTextField(entry.getValue()));
		}

		JPanel panel = new JPanel(new GridLayout(keys.size() + 1, 2));
		panel.add(new JLabel("Key"));
		panel.add(new JLabel("Value"));
		for (int i = 0; i < keys.size(); i++)
		{
			panel.add(keys.get(i));
			panel.add(values.get(i));
		}

		int result = JOptionPane.showConfirmDialog(null, panel,
				"Metadata Editor", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION)
		{
			HashMap<String, String> metaData = new HashMap<String, String>();
			for (int i = 0; i < keys.size(); i++)
			{
				String key = String.valueOf(keys.get(i).getText());
				String value = String.valueOf(values.get(i).getText());
				metaData.put(key, value);
			}

			MetaDataUtils.setMetaData(home,(Serializable) furniture, metaData);
		}
		else
		{
			// System.out.println("Cancelled");
		}

	}

}