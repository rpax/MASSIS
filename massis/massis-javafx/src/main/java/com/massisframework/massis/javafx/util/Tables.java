package com.massisframework.massis.javafx.util;

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class Tables {

	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat(
			"application/x-java-serialized-object");

	public static <T> void setupEditableBooleanColumn(
			TableColumn<T, Boolean> column,
			final Function<T, Property<Boolean>> propFn)
	{
		column.getTableView().setEditable(true);
		column.setCellValueFactory(param -> propFn.apply(param.getValue()));
		column.setOnEditCommit(
				t -> propFn.apply(t.getRowValue()).setValue(t.getNewValue()));
		column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
		column.setEditable(true);
	}

	public static <Model> void setTableDraggable(
			TableView<Model> table)
	{

		table.setRowFactory(tv -> {
			TableRow<Model> row = new TableRow<>();
			row.setOnDragDetected(event -> {
				if (!row.isEmpty())
				{
					Integer index = row.getIndex();
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
					db.setDragView(row.snapshot(null, null));
					ClipboardContent cc = new ClipboardContent();
					cc.put(SERIALIZED_MIME_TYPE, index);
					db.setContent(cc);
					event.consume();
				}
			});
			row.setOnDragOver(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(SERIALIZED_MIME_TYPE))
				{
					if (row.getIndex() != ((Integer) db
							.getContent(SERIALIZED_MIME_TYPE)).intValue())
					{
						event.acceptTransferModes(
								TransferMode.COPY_OR_MOVE);
						event.consume();
					}
				}
			});
			row.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(SERIALIZED_MIME_TYPE))
				{
					int draggedIndex = (Integer) db
							.getContent(SERIALIZED_MIME_TYPE);
					Model draggedModel = table.getItems()
							.remove(draggedIndex);// layers.remove(draggedIndex);
					int dropIndex;
					if (row.isEmpty())
					{
						dropIndex = table.getItems().size();
					} else
					{
						dropIndex = row.getIndex();
					}
					table.getItems().add(dropIndex, draggedModel);
					event.setDropCompleted(true);
					table.getSelectionModel().select(dropIndex);
					event.consume();
				}
			});
			return row;
		});
	}
}
