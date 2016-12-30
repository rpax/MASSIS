package com.massisframework.massis.javafx.canvas2d;

import static com.massisframework.massis.javafx.util.Listeners.weakL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.massisframework.massis.javafx.JFXController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class CanvasTabbedPane extends AnchorPane implements JFXController {

	@FXML
	private TabPane tabPane;
	@FXML
	private ContextMenu contextMenu;
	private List<Canvas2D> canvases;
	private WeakChangeListener<? super Number> hL;
	private WeakChangeListener<? super Number> wL;
	// private List<CanvasLayer> layers;

	public CanvasTabbedPane()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		// this.layers = new ArrayList<>();
		this.canvases = new ArrayList<>();
		this.wL = weakL(n -> canvases.forEach(c -> c.setWidth(n)));
		this.hL = weakL(n -> canvases.forEach(c -> c.setHeight(n)));
		this.heightProperty().addListener(this.wL);
		this.widthProperty().addListener(this.hL);
		// for (int i = 0; i < 1; i++)
		// {
		// addTab("Tab_" + i, (gc) -> {
		//
		// });
		// }
	}

	public Group addTab(String text)
	{
		Tab tab = new Tab(text);

		AnchorPane sp = new AnchorPane();
		makeDraggable(sp);
		tab.setContent(sp);
		this.tabPane.getTabs().add(tab);
		return (Group) sp.getChildren().get(0);
	}

	public void removeTab(Group group)
	{
		this.tabPane.getTabs().removeIf(
				t -> ((Pane) t.getContent()).getChildren().get(0) == group);
	}

	private void addTab(String text, Consumer<GraphicsContext> drawHandler)
	{
		Tab tab = new Tab(text);
		Canvas2D canvas = new Canvas2D();
		tab.setContent(canvas);
		canvas.setWidth(this.getWidth());
		canvas.setHeight(this.getHeight());
		canvas.setDrawHandler(drawHandler);
		this.canvases.add(canvas);
		this.tabPane.getTabs().add(tab);
	}

	// public void addLayer(CanvasLayer layer)
	// {
	// CheckMenuItem mi = new CheckMenuItem(layer.getName());
	// mi.setUserData(layer);
	// mi.setSelected(layer.isEnabled());
	// this.layers.add(layer);
	// mi.setOnAction(evt -> {
	// CheckMenuItem source = ((CheckMenuItem) evt.getSource());
	// ((CanvasLayer) source.getUserData())
	// .setEnabled(source.isSelected());
	// redraw();
	// });
	// this.contextMenu.getItems().add(mi);
	// }

	private void redraw()
	{
		this.tabPane.getTabs()
				.stream()
				.filter(Tab::isSelected)
				.findAny()
				.map(Tab::getContent)
				.map(Canvas2D.class::cast)
				.ifPresent(Canvas2D::redraw);
	}

	private static final class DragContext {
		public double mouseAnchorX;
		public double mouseAnchorY;
		public double initialTranslateX;
		public double initialTranslateY;
	}

	private static Node makeDraggable(final Pane node)
	{

		node.getChildren().add(new Group());
		final DragContext dragContext = new DragContext();

		node.setOnMousePressed(evt -> {
			Node wrapGroup = ((Pane) evt.getSource())
					.getChildren().get(0);
			// remember initial mouse cursor coordinates
			// and node position
			dragContext.mouseAnchorX = evt.getX();
			dragContext.mouseAnchorY = evt.getY();
			dragContext.initialTranslateX = wrapGroup
					.getTranslateX();
			dragContext.initialTranslateY = wrapGroup
					.getTranslateY();
		});

		node.setOnMouseDragged(evt -> {
			Node wrapGroup = ((Pane) evt.getSource())
					.getChildren().get(0);
			// shift node from its initial position by delta
			// calculated from mouse cursor movement
			wrapGroup.setTranslateX(
					dragContext.initialTranslateX
							+ evt.getX()
							- dragContext.mouseAnchorX);
			wrapGroup.setTranslateY(
					dragContext.initialTranslateY
							+ evt.getY()
							- dragContext.mouseAnchorY);

		});

		return node;
	}

}
