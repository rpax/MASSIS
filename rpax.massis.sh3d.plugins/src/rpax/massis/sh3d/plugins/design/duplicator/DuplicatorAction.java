package rpax.massis.sh3d.plugins.design.duplicator;

import rpax.massis.sh3d.plugins.MASSISPluginAction;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.SelectionEvent;
import com.eteks.sweethome3d.model.SelectionListener;
import com.eteks.sweethome3d.viewcontroller.HomeController;

public class DuplicatorAction extends MASSISPluginAction {

	private final HomeController homecontroller;
 
	public DuplicatorAction(Home home, final HomeController homecontroller) {
		super(home, DuplicatorAction.class);
		this.homecontroller = homecontroller;
		setEnabled(true);
		putPropertyValue(Property.MENU, "Tools");
		putPropertyValue(Property.NAME, "DuplicatorTest");
		
		
		this.homecontroller.getFurnitureCatalogController().addSelectionListener(new SelectionListener() {
			
			public void selectionChanged(SelectionEvent selectionEvent) {
				if (selectionEvent.getSelectedItems().size()==1) {
					setEnabled(true);
				}
				else {
					setEnabled(false);
				}
			}
		});
	}

	@Override
	public void execute() {
		try
		{
		//	CatalogPieceOfFurniture f = this.homecontroller.getFurnitureCatalogController().getSelectedFurniture().get(0);
			new ObstacleMatrix(home, this.homecontroller,100);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}
}
