package com.massisframework.massis.displays.displays3d;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.eteks.sweethome3d.io.FileUserPreferences;
import com.eteks.sweethome3d.j3d.HomePieceOfFurniture3D;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.FileContentManager;
import com.eteks.sweethome3d.swing.SwingViewFactory;
import com.massisframework.massis.displays.SimulationDisplay;
import com.massisframework.massis.model.building.IBuilding;
import com.massisframework.massis.model.building.SimulationObject;

public class HomeDisplay3D extends JFrame implements SimulationDisplay {

    private static final long serialVersionUID = -6696779235522417183L;
    private HomeControllerDisplay3D planController;
    HomeComponentDisplay3D homeComponent3D;
    private final IBuilding building;
    private final Home home;
    private boolean initiated = false;

    public HomeDisplay3D(IBuilding building)
    {
        this.home = building.getHome();
        this.building = building;
        setTitle("HomeDisplay3D");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    @Override
    public void animate(SimulationObject obj)
    {
        if (!this.initiated)
        {
            return;
        }
        final HomePieceOfFurniture hpof = this.building.getSH3DRepresentation(obj);

        if (hpof == null)
        {
            System.err.println(obj + " has no representation");
            return;
        }
        if (hpof.getLevel() != obj.getLocation().getFloor().getLevel())
        {
            hpof.setLevel(obj.getLocation().getFloor().getLevel());
        }
        hpof.setAngle((float) (obj.getAngle() - Math.PI / 2));
        hpof.setY((float) obj.getLocation().getY());
        hpof.setX((float) obj.getLocation().getX());
        final HomePieceOfFurniture3D hpof3D = ((HomePieceOfFurniture3D) this.homeComponent3D.homeObjects
                .get(hpof));
        // si todavia no se ha cargado, fuera
        if (hpof3D == null)
        {
            return;
        }
        // animateFurniture3D(hpof3D);
        final Transform3D pieceTransform = getPieceOFFurnitureNormalizedModelTransformation(
                hpof);
        // Change model transformation
        ((TransformGroup) hpof3D.getChild(0)).setTransform(pieceTransform);

    }

    private static Transform3D getPieceOFFurnitureNormalizedModelTransformation(
            HomePieceOfFurniture piece)
    {
        // Set piece size
        final Transform3D scale = new Transform3D();
        float pieceWidth = piece.getWidth();
        // If piece model is mirrored, inverse its width
        if (piece.isModelMirrored())
        {
            pieceWidth *= -1;
        }
        scale.setScale(new Vector3d(pieceWidth, piece.getHeight(), piece
                .getDepth()));
        // Change its angle around y axis
        final Transform3D orientation = new Transform3D();
        orientation.rotY(-piece.getAngle());
        orientation.mul(scale);
        // Translate it to its location
        final Transform3D pieceTransform = new Transform3D();
        float z = piece.getElevation() + piece.getHeight() / 2;
        if (piece.getLevel() != null)
        {
            z += piece.getLevel().getElevation();
        }
        pieceTransform.setTranslation(new Vector3f(piece.getX(), z, piece
                .getY()));
        pieceTransform.mul(orientation);
        return pieceTransform;
    }

    private void init()
    {

        final UserPreferences fileUserPreferences = new FileUserPreferences();

        this.planController = new HomeControllerDisplay3D(this.home, fileUserPreferences,
                new SwingViewFactory(), new FileContentManager(
                fileUserPreferences), null);
        this.homeComponent3D = new HomeComponentDisplay3D(this.home, fileUserPreferences,
                this.planController);

        getContentPane().add(this.homeComponent3D);
        System.err.println("Making outer walls invisible...");
        VisualOps.makeOuterWallsInvisible(this.home);
        System.err.println("done.");
        this.home.setCamera(this.home.getTopCamera());
        pack();
        setSize(600, 500);
        registerListeners();
        this.homeComponent3D.removeHomeListeners();
        this.initiated = true;
    }

    private void unregisterListeners()
    {
        // planController.exit();
    }

    private void registerListeners()
    {
        // planController.enter();
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (visible)
        {
            if (!this.initiated)
            {
                init();
            } else
            {
                registerListeners();
            }
        } else
        {
            unregisterListeners();
        }
        super.setVisible(visible);
    }

    @Override
    public boolean isDisplayEnabled()
    {
        return this.isVisible();
    }
}
