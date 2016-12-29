package com.massisframework.massis.javafx.canvas2d;

import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class CanvasTabbedPane extends AnchorPane implements JFXController {

	@FXML
	private Canvas2D canvas2D;

	public CanvasTabbedPane()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		AnchorPane.setBottomAnchor(canvas2D, 0D);
		AnchorPane.setTopAnchor(canvas2D, 0D);
		AnchorPane.setLeftAnchor(canvas2D, 0D);
		AnchorPane.setRightAnchor(canvas2D, 0D);
		// // this.autosize();
		this.widthProperty().addListener((obs1, o, n) -> {
			canvas2D.setWidth(n.doubleValue());
		});
		this.heightProperty().addListener((obs1, o, n) -> {
			canvas2D.setHeight(n.doubleValue());
		});

	}
}
