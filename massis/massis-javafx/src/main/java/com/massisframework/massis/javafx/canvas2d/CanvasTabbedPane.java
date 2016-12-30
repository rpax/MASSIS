package com.massisframework.massis.javafx.canvas2d;

import static com.massisframework.massis.javafx.util.Listeners.weakL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.massisframework.massis.javafx.JFXController;

import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

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

	public JFXSceneGraph addTab(String text)
	{
		Tab tab = new Tab(text);
		AnchorPaneSceneGraph sceneGraph = new AnchorPaneSceneGraph();
		tab.setContent(sceneGraph);
		this.tabPane.getTabs().add(tab);
		return sceneGraph;
	}

	public void removeTab(JFXSceneGraph sg)
	{
		this.tabPane.getTabs().removeIf(t->t.getContent()==sg);
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

	
}
