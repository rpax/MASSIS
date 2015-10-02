package rpax.massis.displays.buildingmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import rpax.massis.displays.floormap.layers.FloorMapLayer;
import rpax.massis.model.building.Building;
import rpax.massis.model.building.Floor;

/**
 * A a JPanel capable of pan and zoom. It manages the 2D visualization of the
 * different layers of the building. <br>
 * </br><strong>Attributions:</strong></p> Adapted from code posted by R.J.
 * Lorimer in an article entitled "Java2D: Have Fun With Affine Transform". The
 * original post and code can be found at
 * http://www.javalobby.org/java/forums/t19387.html. </p>
 *
 * @author rpax
 *
 */
public class PanAndZoomJPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private double translateX;
    private double translateY;
    private double scale;
    private AffineTransform at;
    private Floor floor;
    private Point2D XFormedPoint;
    private List<FloorMapLayer> layers;
    private boolean initiated = false;

    /**
     * Empty constructor. It is useful for using this component in a graphical
     * GUI designer (such as Netbeans')
     */
    public PanAndZoomJPanel()
    {
    }

    /**
     * Proper constructor. It creates a new JPanel for displaying the 2D views.
     *
     * @param floor The floor that this panel should display
     * @param building the building of the current simulation
     * @param layers a list of {@link FloorMapLayer}. The
     * {@link FloorMapLayer#drawIfEnabled(Floor, Graphics2D)} method of each one
     * of the elements of the list will be called in order, from bottom to top.
     * Example:
     * <p>
     * With the layers {@code [A,B,C,D]}:<br>
     * </br>
     * <ul>
     * <li>Draws A</li>
     * <li><strong>On top of A</strong> draws B</li>
     * <li><strong>On top of B</strong> draws C</li>
     * <li>Finally, draws D</li>
     * </ul>
     * As can be seen, the elements of one layer can <i>overwrite</i>
     * the previous layers drawings.
     * </p>
     */
    public PanAndZoomJPanel(Floor floor, Building building,
            List<FloorMapLayer> layers)
    {
        this.initiate(floor, building, layers);

    }

    /**
     * Initiates this Panel. This separation is made in order to make easier the
     * integration within a graphical GUI designer
     *
     * @param floor the corresponding floor
     * @param building the simulation building
     * @param layers the layers to be displayed
     * @return This panel. Useful for chaining
     * @see <p> {@link #PanAndZoomJPanel(Floor, Building, List)}
     * </p>
     */
    private PanAndZoomJPanel initiate(Floor floor, Building building,
            List<FloorMapLayer> layers)
    {
        if (initiated)
        {
            return this;
        }

        translateX = 0;
        translateY = 0;
        this.floor = floor;
        this.layers = layers;
        this.setScale(1);
        PanningHandler panner = new PanningHandler();
        this.addMouseListener(panner);
        this.addMouseMotionListener(panner);

        this.setBorder(BorderFactory.createLineBorder(Color.black));
        this.initiated = true;
        return this;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        if (!initiated)
        {
            return;
        }
        render(g);
    }

    public void render(Graphics g)
    {

        Graphics2D ourGraphics = (Graphics2D) g;
        // save the original transform so that we can restore
        // it later

        AffineTransform saveTransform = ourGraphics.getTransform();

        // blank the screen. If we do not call super.paintComponent, then
        // we need to blank it ourselves
        ourGraphics.setColor(Color.BLACK);
        ourGraphics.fillRect(0, 0, getWidth(), getHeight());

        // We need to add new transforms to the existing
        // transform, rather than creating a new transform from scratch.
        // If we create a transform from scratch, we will
        // will start from the upper left of a JFrame,
        // rather than from the upper left of our component
        at = new AffineTransform(saveTransform);

        // The zooming transformation. Notice that it will be performed
        // after the panning transformation, zooming the panned scene,
        // rather than the original scene
        // at.translate(getWidth() / 2, getHeight() / 2);
        at.scale(getScale(), getScale());
        // at.translate(-getWidth() / 2, -getHeight() / 2);

        // The panning transformation
        at.translate(translateX, translateY);

        ourGraphics.setTransform(at);

        /*
         * Main rendering method
         */
        renderGraphics(ourGraphics);

        ourGraphics.setTransform(saveTransform);
    }

    /**
     * Iterates over every layer, drawing it if enabled
     *
     * @param g the {@link Graphics2D} object of this panel
     */
    private void renderGraphics(Graphics2D g)
    {

        for (FloorMapLayer layer : layers)
        {
            layer.drawIfEnabled(floor, g);
        }

    }

    @Override
    public Dimension getPreferredSize()
    {

        return new Dimension(500, 500);

    }

    public double getScale()
    {
        return scale;
    }

    /**
     * Sets the zoom's scale
     *
     * @param scale
     */
    public void setScale(double scale)
    {
        this.scale = scale
                * (getPreferredSize().width * 1.0 / (floor.getMaxX() - floor
                .getMinX()));
    }

    private class PanningHandler implements MouseListener, MouseMotionListener {

        double referenceX;
        double referenceY;
        // saves the initial transform at the beginning of the pan interaction
        AffineTransform initialTransform;

        // capture the starting point
        @Override
        public void mousePressed(MouseEvent e)
        {

            // first transform the mouse point to the pan and zoom
            // coordinates
            try
            {
                XFormedPoint = at.inverseTransform(e.getPoint(), null);
            } catch (NoninvertibleTransformException te)
            {
                System.out.println(te);
            }

            // save the transformed starting point and the initial
            // transform
            referenceX = XFormedPoint.getX();
            referenceY = XFormedPoint.getY();
            initialTransform = at;

        }

        @Override
        public void mouseDragged(MouseEvent e)
        {

            // first transform the mouse point to the pan and zoom
            // coordinates. We must take care to transform by the
            // initial tranform, not the updated transform, so that
            // both the initial reference point and all subsequent
            // reference points are measured against the same origin.
            try
            {
                XFormedPoint = initialTransform.inverseTransform(e.getPoint(),
                        null);
            } catch (NoninvertibleTransformException te)
            {
                System.out.println(te);
            }

            // the size of the pan translations
            // are defined by the current mouse location subtracted
            // from the reference location
            double deltaX = XFormedPoint.getX() - referenceX;
            double deltaY = XFormedPoint.getY() - referenceY;

            // make the reference point be the new mouse point.
            referenceX = XFormedPoint.getX();
            referenceY = XFormedPoint.getY();

            PanAndZoomJPanel.this.translateX += deltaX;
            PanAndZoomJPanel.this.translateY += deltaY;

            // schedule a repaint.
            PanAndZoomJPanel.this.repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
        }
    }

    /**
     * Zooms the view and schedules a repaint
     *
     * @param zoomPercent the percent desired
     */
    public void zoom(float zoomPercent)
    {
        PanAndZoomJPanel.this.setScale(Math.max(0.00001, zoomPercent / 100.0));
        PanAndZoomJPanel.this.repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent evt)
    {

        double delta = 0.005f * evt.getPreciseWheelRotation();
        scale -= delta;
        revalidate();
        repaint();

    }

    public void keyPressed(KeyEvent e)
    {
        double delta = 0.0005f;
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_Z:
                scale -= delta;
                revalidate();
                repaint();
                break;
            case KeyEvent.VK_X:
                scale += delta;
                revalidate();
                repaint();
                break;
            case KeyEvent.VK_W:
                PanAndZoomJPanel.this.translateY += 5;
                revalidate();
                repaint();
                break;
            case KeyEvent.VK_S:
                PanAndZoomJPanel.this.translateY -= 5;
                revalidate();
                repaint();
                break;
            case KeyEvent.VK_D:
                PanAndZoomJPanel.this.translateX += 5;
                revalidate();
                repaint();
                break;
            case KeyEvent.VK_A:
                PanAndZoomJPanel.this.translateX -= 5;
                revalidate();
                repaint();
                break;
        }

    }

    public void keyReleased(KeyEvent e)
    {
        // keyPressed(e);
    }
}
