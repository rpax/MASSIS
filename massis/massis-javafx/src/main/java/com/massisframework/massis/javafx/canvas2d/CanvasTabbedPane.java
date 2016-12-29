package com.massisframework.massis.javafx.canvas2d;

import static com.massisframework.massis.javafx.util.Listeners.weakL;

import java.util.ArrayList;
import java.util.List;

import com.massisframework.massis.javafx.JFXController;

import javafx.animation.AnimationTimer;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class CanvasTabbedPane extends AnchorPane implements JFXController {

	@FXML
	private TabPane tabPane;
	private List<Canvas2D> canvases;
	private WeakChangeListener<? super Number> hL;
	private WeakChangeListener<? super Number> wL;

	public CanvasTabbedPane()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		this.canvases = new ArrayList<>();
		this.wL = weakL(n -> canvases.forEach(c -> c.setWidth(n)));
		this.hL = weakL(n -> canvases.forEach(c -> c.setHeight(n)));
		this.heightProperty().addListener(this.wL);
		this.widthProperty().addListener(this.hL);
		for (int i = 0; i < 10; i++)
		{
			addTab("Tab_" + i);
		}
		new AnimationTimer() {

			@Override
			public void handle(long now)
			{
				canvases.forEach(canvas -> {
					canvas.drawTest();
				});

			}
		}.start();
	}

	public void addTab(String text)
	{
		Tab tab = new Tab(text);
		Canvas2D canvas = new Canvas2D();
		tab.setContent(canvas);
		canvas.setWidth(this.getWidth());
		canvas.setHeight(this.getHeight());
		this.canvases.add(canvas);
		this.tabPane.getTabs().add(tab);
	}
}
