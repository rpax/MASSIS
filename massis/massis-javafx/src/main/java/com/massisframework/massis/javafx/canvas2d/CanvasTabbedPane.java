package com.massisframework.massis.javafx.canvas2d;

import static com.massisframework.massis.javafx.util.Listeners.weakL;

import java.util.ArrayList;
import java.util.List;

import com.massisframework.massis.javafx.JFXController;

import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class CanvasTabbedPane extends AnchorPane implements JFXController {

	@FXML
	private TabPane tabPane;
	@FXML
	private ContextMenu contextMenu;
	private List<Canvas2D> canvases;
	private WeakChangeListener<? super Number> hL;
	private WeakChangeListener<? super Number> wL;
	private List<CanvasLayer<?>> layers;
	private CanvasDD canvasDD;

	public CanvasTabbedPane()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		this.canvasDD = new CanvasDD();
		this.layers = new ArrayList<>();
		this.canvases = new ArrayList<>();
		this.wL = weakL(n -> canvases.forEach(c -> c.setWidth(n)));
		this.hL = weakL(n -> canvases.forEach(c -> c.setHeight(n)));
		this.heightProperty().addListener(this.wL);
		this.widthProperty().addListener(this.hL);
		for (int i = 0; i < 1; i++)
		{
			addTab("Tab_" + i);
		}
		this.addLayer(new CanvasLayer<Object>(canvasDD, "Layer 1") {
			@Override
			public void draw(Object model, GraphicsContext gc)
			{
				gc.setFill(Color.RED);
				gc.setStroke(Color.BLUE);
				gc.setLineWidth(5);
				gc.strokeLine(40, 10, 10, 40);
				gc.fillOval(10, 60, 30, 30);
				gc.strokeOval(60, 60, 30, 30);
				gc.fillRoundRect(110, 60, 30, 30, 10, 10);
				gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
				gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
				gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
				gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
				gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
				gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
				gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
				gc.fillPolygon(new double[] { 10, 40, 10, 40 },
						new double[] { 210, 210, 240, 240 }, 4);
				gc.strokePolygon(new double[] { 60, 90, 60, 90 },
						new double[] { 210, 210, 240, 240 }, 4);
				gc.strokePolyline(new double[] { 110, 140, 110, 140 },
						new double[] { 210, 210, 240, 240 }, 4);

			}
		});

	}

	public void addTab(String text)
	{
		Tab tab = new Tab(text);
		Canvas2D canvas = new Canvas2D();
		tab.setContent(canvas);
		canvas.setWidth(this.getWidth());
		canvas.setHeight(this.getHeight());
		canvas.setDrawHandler((gc) -> {
			this.layers.forEach(l -> l.draw(gc));
		});
		this.canvases.add(canvas);
		this.tabPane.getTabs().add(tab);
	}

	public void addLayer(CanvasLayer layer)
	{
		CheckMenuItem mi = new CheckMenuItem(layer.getName());
		mi.setUserData(layer);
		mi.setSelected(layer.isEnabled());
		this.layers.add(layer);
		mi.setOnAction(evt -> {
			CheckMenuItem source = ((CheckMenuItem) evt.getSource());
			((CanvasLayer) source.getUserData())
					.setEnabled(source.isSelected());
			redraw();
		});
		this.contextMenu.getItems().add(mi);
	}

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

	public static class CanvasDD implements CanvasDrawable<Object> {

		@Override
		public double getMaxX()
		{
			return 300;
		}

		@Override
		public double getMaxY()
		{
			return 300;
		}

		@Override
		public double getMinX()
		{
			return 0;
		}

		@Override
		public double getMinY()
		{
			return 0;
		}

		@Override
		public Object getModel()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}
}
