package com.massisframework.massis.javafx.canvas2d;

import java.util.function.Consumer;

import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class Canvas2D extends AnchorPane implements JFXController {

	@FXML
	private Canvas canvas;
	@FXML
	private GridPane gridPane;
	@FXML
	private AnchorPane anchorPane;
	private double pressedX, pressedY;
	private boolean panning = false;
	private Affine transform;
	private static final Affine IDENTITY = new Affine();

	public Canvas2D()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		this.transform = new Affine();
		anchorPane.widthProperty().addListener((obs, o, n) -> {
			canvas.setWidth(n.doubleValue());
			redraw();
		});
		anchorPane.heightProperty().addListener((obs, o, n) -> {
			canvas.setHeight(n.doubleValue());
			redraw();
		});
		this.enablePan();
		this.enableZoom();
		this.setZoom(1);
		this.redraw();
	}

	public void setZoom(double percentage)
	{
		double original = percentage;
		if (percentage < 0)
			percentage = 0.0001;
		if (percentage > 100)
			percentage = 99;
		double scale = percentage;
		System.out.println("Original: " + original + ".Percentage: "
				+ percentage + ". Scale: " + scale);
		this.transform.setMxx(scale);
		this.transform.setMyy(scale);
		this.canvas.getGraphicsContext2D().setTransform(transform);
		redraw();
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
		redraw();
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
			final double inc = Math.signum(evt.getDeltaY()) > 0 ? 0.1
					: -0.1;
			this.setZoom(getZoom() + inc);
			evt.consume();
		});
	}

	private Consumer<GraphicsContext> drawable;

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

	public void redraw()
	{
		if (this.drawable != null)
		{
			this.draw(this.drawable);
		}
	}

	private void draw(Consumer<GraphicsContext> action)
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

	public void setDrawHandler(Consumer<GraphicsContext> action)
	{
		this.drawable = action;
		this.setZoom(1);
	}
}
