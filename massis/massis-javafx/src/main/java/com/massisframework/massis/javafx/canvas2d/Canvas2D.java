package com.massisframework.massis.javafx.canvas2d;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;

public class Canvas2D extends AnchorPane implements JFXController {

	@FXML
	private Canvas canvas;
	@FXML
	private GridPane gridPane;
	@FXML
	private AnchorPane anchorPane;
	private double pressedX, pressedY;
	private Affine transform;
	private static final Affine IDENTITY = new Affine();
	private AtomicBoolean needsRedraw;

	public Canvas2D()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		this.needsRedraw = new AtomicBoolean(true);
		this.transform = new Affine();
		anchorPane.widthProperty().addListener((obs, o, n) -> {
			canvas.setWidth(n.doubleValue());
			needsRedraw.set(true);
		});
		anchorPane.heightProperty().addListener((obs, o, n) -> {
			canvas.setHeight(n.doubleValue());
			needsRedraw.set(true);
		});
		this.enablePan();
		this.enableZoom();
	}

	public void setZoom(double percentage)
	{
		if (percentage <= 0)
		{
			percentage = 0.0001D;
		}
		this.transform.setMxx(percentage);
		this.transform.setMyy(percentage);
		this.canvas.getGraphicsContext2D().setTransform(transform);
		needsRedraw.set(true);
	}

	public double getZoom()
	{
		return this.transform.getMxx();
	}

	public void setCanvasTranslation(double tx, double ty)
	{
		this.transform.setTx(tx);
		this.transform.setTy(ty);
		this.canvas.getGraphicsContext2D().setTransform(transform);
		needsRedraw.set(true);
	}

	public double getCanvasTranslationX()
	{
		return this.transform.getTx();
	}

	public double getCanvasTranslationY()
	{
		return this.transform.getTy();
	}

	private void enableZoom()
	{
		canvas.setOnScroll(evt -> {
			final double inc = Math.signum(evt.getDeltaY()) > 0 ? 0.1 : -0.1;
			this.setZoom(getZoom() + inc);
			evt.consume();
		});
	}

	private boolean panning = false;

	private void enablePan()
	{

		canvas.setOnMouseReleased(evt -> {
			panning = false;
		});

		canvas.setOnMouseDragged(evt -> {
			if (panning)
			{
				final double x = getCanvasTranslationX();
				final double y = getCanvasTranslationY();
				final double dx = (evt.getScreenX() - pressedX);
				final double dy = (evt.getScreenY() - pressedY);
				this.setCanvasTranslation(x + dx, y + dy);
			} else
			{
				panning = true;
			}
			pressedX = evt.getScreenX();
			pressedY = evt.getScreenY();
			evt.consume();
		});

	}

	private static ThreadLocal<Affine> drawShapes_transform_TL = ThreadLocal
			.withInitial(Affine::new);

	public void drawTest()
	{
		this.draw((gc) -> {
			gc.setFill(Color.GREEN);
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
		});
	}

	public void draw(Consumer<GraphicsContext> action)
	{
		GraphicsContext gc = this.canvas.getGraphicsContext2D();
		Affine transform = gc.getTransform(drawShapes_transform_TL.get());
		gc.setTransform(IDENTITY);
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.WHITE);
		gc.fillRect(0, 0, gc.getCanvas().getWidth(),
				gc.getCanvas().getHeight());
		gc.setTransform(transform);
		action.accept(gc);
	}

	protected void setWidth(Number value)
	{
		this.setWidth(value.doubleValue());
	}

	protected void setHeight(Number value)
	{
		this.setHeight(value.doubleValue());
	}

	@Override
	protected void setWidth(double value)
	{
		super.setWidth(value);
	}

	@Override
	protected void setHeight(double value)
	{
		super.setHeight(value);
	}

}
