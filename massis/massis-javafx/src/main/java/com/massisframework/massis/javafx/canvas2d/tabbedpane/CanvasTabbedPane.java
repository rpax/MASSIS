package com.massisframework.massis.javafx.canvas2d.tabbedpane;

import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class CanvasTabbedPane extends AnchorPane implements JFXController {
	

	@FXML
	private TabPane tabPane;


	public CanvasTabbedPane()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		
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
		this.tabPane.getTabs().removeIf(t -> t.getContent() == sg);
	}

	

}
