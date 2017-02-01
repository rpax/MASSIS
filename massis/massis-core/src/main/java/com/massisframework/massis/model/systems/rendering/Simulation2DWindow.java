package com.massisframework.massis.model.systems.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Affine;

public class Simulation2DWindow {

	@FXML
	private AnchorPane mainAnchorPane;
	private Canvas canvas;
	private Collection<OLDSimulationEntity<?>> entities = new ArrayList<OLDSimulationEntity<?>>();

	@FXML
	public void initialize()
	{
		// System.setProperty("javafx.animation.fullspeed", "true");
		configureCanvasPane();

	}

	private void configureCanvasPane()
	{

		this.canvas = new ResizableCanvas();
		this.mainAnchorPane.getChildren().add(this.canvas);
		updateCanvasSize();
		AnchorPane.setLeftAnchor(canvas, 0D);
		AnchorPane.setRightAnchor(canvas, 0D);
		AnchorPane.setTopAnchor(canvas, 0D);
		AnchorPane.setBottomAnchor(canvas, 0D);

		configureDrawingTimer();

	}

	public void configureDrawingTimer()
	{
		new AnimationTimer() {

			private Affine tr = new Affine();
			Simulation2DWindow window = Simulation2DWindow.this;

			@Override
			public void handle(long now)
			{

				if (!updated.getAndSet(false))
					return;
				Canvas canvas = window.getCanvas();
				GraphicsContext g2c = canvas.getGraphicsContext2D();

				tr.setToIdentity();
				g2c.setTransform(tr);

				canvas.getGraphicsContext2D().clearRect(0, 0,
						canvas.getWidth(),
						canvas.getHeight());

				double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE,
						minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
				synchronized (entities)
				{
					for (OLDSimulationEntity<?> se : entities)
					{
						Position2D position = se.get(Position2D.class);
						if (position != null)
						{
							minX = Math.min(minX, position.getX());
							minY = Math.min(minY, position.getY());

							maxX = Math.max(maxX, position.getX());
							maxY = Math.max(maxY, position.getY());
						}
					}

					double scale = Math.min(canvas.getWidth() / (maxX - minX),
							canvas.getHeight() / (maxY - minY));
					double translateX = -minX;
					double translateY = -minY;

					tr.appendScale(scale, scale);
					tr.appendTranslation(translateX, translateY);
					g2c.transform(tr);
					for (OLDSimulationEntity<?> se : entities)
					{
						se.get(RenderComponent.class).getRenderer().render(se,
								g2c);
					}
				}
			}
		}.start();
	}

	AtomicBoolean updated = new AtomicBoolean(false);

	public void setEntities(Iterable<OLDSimulationEntity<?>> entities)
	{
		synchronized (this.entities)
		{
			this.entities.clear();
			for (OLDSimulationEntity<?> simulationEntity : entities)
			{
				this.entities.add(simulationEntity);
			}
			updated.set(true);

		}
	}

	private void updateCanvasSize()
	{
		// canvas.resize(mainAnchorPane.getWidth(),mainAnchorPane.getHeight());
		canvas.setWidth(mainAnchorPane.getWidth());
		canvas.setHeight(mainAnchorPane.getHeight());
		// canvas.autosize();
		// refresh();
		// drawShapes(canvas.getGraphicsContext2D());
	}

	public Canvas getCanvas()
	{
		return this.canvas;
	}

	private static class ResizableCanvas extends Canvas {

		double _tmp_width = 64;
		double _tmp_heigth = 64;

		public ResizableCanvas()
		{
			new AnimationTimer() {

				@Override
				public void handle(long now)
				{
					if (ResizableCanvas.this.getWidth() != _tmp_width)
					{
						ResizableCanvas.this.setWidth(_tmp_width);
					}
					if (ResizableCanvas.this.getHeight() != _tmp_heigth)
					{
						ResizableCanvas.this.setHeight(_tmp_heigth);
					}
				}
			}.start();
		}

		@Override
		public double minHeight(double width)
		{
			return 64;
		}

		@Override
		public double maxHeight(double width)
		{
			return 1000;
		}

		@Override
		public double prefHeight(double width)
		{
			return minHeight(width);
		}

		@Override
		public double minWidth(double height)
		{
			return 0;
		}

		@Override
		public double maxWidth(double height)
		{
			return 10000;
		}

		@Override
		public boolean isResizable()
		{
			return true;
		}

		@Override
		public void resize(double width, double height)
		{
			// super.setWidth(width);
			// super.setHeight(height);
			_tmp_width = width;
			_tmp_heigth = height;
			// repaint?
		}
	}

}
