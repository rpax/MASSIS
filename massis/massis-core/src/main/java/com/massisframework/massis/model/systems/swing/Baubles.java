package com.massisframework.massis.model.systems.swing;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @see http://stackoverflow.com/a/31761362/230513
 * @see http://stackoverflow.com/a/8616169/230513
 */

public class Baubles extends Application {

    private static final int MAX = 64;
    private static final double WIDTH = 640;
    private static final double HEIGHT = 480;
    private static final Random RND = new Random();
    private final Queue<Bauble> queue = new LinkedList<>();
    private Canvas canvas;

    @Override
    public void start(Stage stage) {
        CanvasPane canvasPane = new CanvasPane(WIDTH, HEIGHT);
        canvas = canvasPane.getCanvas();
        BorderPane root = new BorderPane(canvasPane);
        CheckBox cb = new CheckBox("Animate");
        cb.setSelected(true);
        root.setBottom(cb);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        for (int i = 0; i < MAX; i++) {
            queue.add(randomBauble());
        }
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GraphicsContext g = canvas.getGraphicsContext2D();
                g.setFill(Color.BLACK);
                g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                for (Bauble b : queue) {
                    g.setFill(b.c);
                    g.fillOval(b.x, b.y, b.d, b.d);
                }
                queue.add(randomBauble());
                queue.remove();
            }
        };
        loop.start();
        cb.selectedProperty().addListener((Observable o) -> {
            if (cb.isSelected()) {
                loop.start();
            } else {
                loop.stop();
            }
        });
    }

    private static class Bauble {

        private final double x, y, d;
        private final Color c;

        public Bauble(double x, double y, double r, Color c) {
            this.x = x - r;
            this.y = y - r;
            this.d = 2 * r;
            this.c = c;
        }
    }

    private Bauble randomBauble() {
        double x = RND.nextDouble() * canvas.getWidth();
        double y = RND.nextDouble() * canvas.getHeight();
        double r = RND.nextDouble() * MAX + MAX / 2;
        Color c = Color.hsb(RND.nextDouble() * 360, 1, 1, 0.75);
        return new Bauble(x, y, r, c);
    }

    private static class CanvasPane extends Pane {

        private final Canvas canvas;

        public CanvasPane(double width, double height) {
            canvas = new Canvas(width, height);
            getChildren().add(canvas);
        }

        public Canvas getCanvas() {
            return canvas;
        }

        @Override
        protected void layoutChildren() {
            final double x = snappedLeftInset();
            final double y = snappedTopInset();
            final double w = snapSize(getWidth()) - x - snappedRightInset();
            final double h = snapSize(getHeight()) - y - snappedBottomInset();
            canvas.setLayoutX(x);
            canvas.setLayoutY(y);
            canvas.setWidth(w);
            canvas.setHeight(h);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}