package com.massisframework.massis.javafx.canvas2d.tabbedpane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.massisframework.massis.javafx.JFXController;
import com.massisframework.massis.javafx.util.Tables;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class AnchorPaneSceneGraph extends AnchorPane
		implements JFXSceneGraph, JFXController {

	final double SCALE_DELTA = 1.025;
	@FXML
	private Group innerGroup;
	@FXML
	private Group sceneGroup;
	@FXML
	private ContextMenu layersContextMenu;
	@FXML
	private AnchorPane groupContainer;
	@FXML
	private BorderPane mainPane;
	@FXML
	private TableView<FXSceneGroup> layerTable;
	@FXML
	private TableColumn<FXSceneGroup, String> layerNameCol;
	@FXML
	private TableColumn<FXSceneGroup, Boolean> enabledCol;
	@FXML
	private ScrollPane scrollContainer;

	private DragContext dragContext;

	private ObservableList<FXSceneGroup> layers;

	public AnchorPaneSceneGraph()
	{
		inject();
		// this.mainPane = new BorderPane();
		// this.getChildren().add(this.mainPane);
		//
		this.layersContextMenu = new ContextMenu();
		MenuItem zoomMI = new MenuItem("Reset zoom");
		zoomMI.setOnAction(evt -> {
			this.resetZoom();
		});
		this.layersContextMenu.getItems().add(zoomMI);
		this.setOnContextMenuRequested(this::onContextMenuRequested);

		this.dragContext = new DragContext();
		// this.widthProperty().addListener(this::onWidthChanged);
		// this.heightProperty().addListener(this::onHeightChanged);
		this.configureTable();
		groupContainer.setOnMousePressed(this::onMousePressed);
		groupContainer.setOnMouseDragged(this::onMouseDragged);
		groupContainer.setOnScroll(this::onScroll);
	}

	private void resetZoom()
	{

		Bounds containerBounds;
		Bounds bounds;

		this.innerGroup.setScaleX(1);
		this.innerGroup.setScaleY(1);

		containerBounds = scrollContainer.getBoundsInLocal();
		bounds = innerGroup.getBoundsInLocal();

		double rateW = containerBounds.getWidth() / bounds.getWidth();
		double rateH = containerBounds.getHeight() / bounds.getHeight();
		double rate = Math.min(rateW, rateH);
		this.innerGroup.setScaleX(rate);
		this.innerGroup.setScaleY(rate);
		this.innerGroup.setTranslateX(this.innerGroup.getTranslateX()
				- innerGroup.getBoundsInParent().getMinX());
		this.innerGroup.setTranslateY(this.innerGroup.getTranslateY()
				- innerGroup.getBoundsInParent().getMinY());

	}

	private void configureTable()
	{
		this.layers = FXCollections.observableArrayList(
				p -> new Observable[] { p.enabledProperty() });
		this.layerTable.setItems(this.layers);
		this.layerNameCol
				.setCellValueFactory(c -> c.getValue().layerNameProperty());
		Tables.setupEditableBooleanColumn(enabledCol,
				FXSceneGroup::enabledProperty);
		Tables.setTableDraggable(this.layerTable);
		layers.addListener(this::onLayersChange);
	}

	private void onLayersChange(Change<? extends FXSceneGroup> change)
	{
		Map<String, Integer> layerOrder = new HashMap<>();
		for (int i = 0; i < this.layers.size(); i++)
		{
			FXSceneGroup layer = this.layers.get(i);
			String layerName = layer.layerNameProperty().get();
			boolean visible = layer.enabledProperty().get();
			this.sceneGroup.getChildren()
					.stream()
					.filter(c -> getLayerName(c).equals(layerName))
					.forEach(c -> c.setVisible(visible));

			layerOrder.put(layerName, i);
		}
		List<Node> sorted = new ArrayList<>(sceneGroup.getChildren());
		sorted.sort((c1, c2) -> {
			String l1 = getLayerName(c1);
			String l2 = getLayerName(c2);
			Integer v1 = layerOrder.get(l1);
			Integer v2 = layerOrder.get(l2);
			if (v1 == null || v2 == null)
			{
				return 0;
			}
			return Integer.compare(
					layerOrder.get(l1),
					layerOrder.get(l2));
		});

		this.sceneGroup.getChildren().setAll(sorted);
	}

	private void onWidthChanged(ObservableValue<? extends Number> obs, Number o,
			Number n)
	{
		double rate = n.doubleValue() / o.doubleValue();
		if (Double.isFinite(rate) && isContainedInParent())
		{
			innerGroup.setTranslateX(innerGroup.getTranslateX()
					+ (n.doubleValue() - o.doubleValue()));
			innerGroup.setScaleX(innerGroup.getScaleX() * rate);
			innerGroup.setScaleY(innerGroup.getScaleY() * rate);
		}
	}

	private void onHeightChanged(ObservableValue<? extends Number> obs,
			Number o,
			Number n)
	{
		double rate = n.doubleValue() / o.doubleValue();
		if (Double.isFinite(rate) && isContainedInParent())
		{
			innerGroup.setTranslateY(innerGroup.getTranslateY()
					+ (n.doubleValue() - o.doubleValue()));
			innerGroup.setScaleX(innerGroup.getScaleX() * rate);
			innerGroup.setScaleY(innerGroup.getScaleY() * rate);
		}
	}

	private boolean isContainedInParent()
	{
		return (!innerGroup.getParent()
				.intersects(innerGroup.getBoundsInParent()));
	}

	private void onContextMenuRequested(ContextMenuEvent evt)
	{
		this.layersContextMenu.setX(evt.getScreenX());
		this.layersContextMenu.setY(evt.getScreenY());
		this.layersContextMenu.setAutoHide(true);
		this.layersContextMenu.show(this.getScene().getWindow());
	}

	private static final class DragContext {
		public double mouseAnchorX;
		public double mouseAnchorY;
	}

	public void onMousePressed(MouseEvent evt)
	{
		Point2D point = new Point2D(evt.getX(), evt.getY());
		dragContext.mouseAnchorX = point.getX();
		dragContext.mouseAnchorY = point.getY();
		// evt.consume();
	}

	public void onMouseDragged(MouseEvent evt)
	{
		Point2D point = new Point2D(evt.getX(), evt.getY());
		innerGroup.setTranslateX(innerGroup.getTranslateX() + point.getX()
				- dragContext.mouseAnchorX);
		innerGroup.setTranslateY(innerGroup.getTranslateY() + point.getY()
				- dragContext.mouseAnchorY);
		dragContext.mouseAnchorX = point.getX();
		dragContext.mouseAnchorY = point.getY();
		// evt.consume();
	}

	public void onScroll(ScrollEvent evt)
	{
		if (evt.getDeltaY() == 0)
		{
			return;
		}
		double scaleFactor = (evt.getDeltaY() > 0)
				? SCALE_DELTA
				: 1 / SCALE_DELTA;
		innerGroup.setScaleX(innerGroup.getScaleX() * scaleFactor);
		innerGroup.setScaleY(innerGroup.getScaleY() * scaleFactor);
		evt.consume();
	}

	public void addChild(Node node)
	{
		this.addLayerIfNotExists(
				String.valueOf(node.getProperties().get("LAYER")));
		this.sceneGroup.getChildren().add(node);
	}

	private void addLayerIfNotExists(String name)
	{
		boolean exists = this.layers.stream()
				.anyMatch(l -> l.layerNameProperty().get().equals(name));
		if (!exists)
		{
			this.layers.add(new FXSceneGroup(name, true));
		}
	}

	private String getLayerName(Node n)
	{
		return String.valueOf(n.getProperties().get("LAYER"));
	}

	public void removeChild(Node node)
	{
		this.sceneGroup.getChildren().remove(node);
	}

}
