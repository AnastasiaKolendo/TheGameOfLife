package com.kolendoanastasia.gameoflife;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.control.Slider;


public class GameOfLifeGui extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Game Of Life");
        primaryStage.show();

        int numberOfRows = 15;
        int numberOfColumns = 20;

        Label label1 = new Label("The Game Of Life!");
        Label label2 = new Label("Let's start!");
        Label label3 = new Label("");
        Label label4 = new Label("The first generation: ");
        Label label5 = new Label("");

        Life life = new Life(numberOfRows, numberOfColumns);
        createFirstGeneration(life);

        GridPane gridPane = new GridPane();
        for (int i = 0; i < numberOfColumns; i++) {
            for (int j = 0; j < numberOfRows; j++) {
                Label label = new Label();
                label.setMinWidth(20);
                label.setMinHeight(20);
                String color = life.isAlive(j, i) ? "000000" : "FFFFFF";
                label.setStyle("-fx-background-color: #" + color + ";");
                gridPane.add(label, i, j);
            }
        }

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));

        Label label6 = new Label("Press 'next generation' for next generation or 'exit' to end simulation");
        Button nextGenerationButton = new Button("next generation");
        Button exitButton = new Button("exit");
        exitButton.setOnAction(e -> Platform.exit());
        HBox hbox = new HBox(20, nextGenerationButton, exitButton);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(20));

        Slider slider = new Slider(0, 900, 100);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(10);
        slider.setMinorTickCount(10);
        slider.setBlockIncrement(10);
        hbox.getChildren().add(slider);
        AtomicInteger evolutionSpeed = new AtomicInteger(0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> evolutionSpeed.set(newValue.intValue()));

        Runnable updater = () -> {
            life.evolve();
            for (Node child : gridPane.getChildren()) {
                int i = GridPane.getColumnIndex(child);
                int j = GridPane.getRowIndex(child);
                String color = life.isAlive(j, i) ? "000000" : "FFFFFF";
                child.setStyle("-fx-background-color: #" + color + ";");
                nextGenerationButton.setText("Stop");
                label4.setText(" ");
                label6.setText("Press 'stop' to stop simulation or 'exit' to end it");
            }
        };
        Runnable evaluator = () -> {
            while (true) {
                try {
                    Thread.sleep(1000 - evolutionSpeed.get());
                } catch (InterruptedException ex) {
                    return;
                }

                // UI update is run on the Application thread
                Platform.runLater(updater);
            }
        };
        Thread[] thread = {null};

        nextGenerationButton.setOnAction(event -> {
            if (thread[0] == null) {
                Thread t = new Thread(evaluator);
                t.setDaemon(true);
                t.start();
                thread[0] = t;
            } else {
                thread[0].interrupt();
                thread[0] = null;
            }
        });

        VBox vbox = new VBox(label1, label2, label3, label4, label5, gridPane, label6, hbox);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 600, 700);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private static void createFirstGeneration(Life life) {
        Random random = new Random();
        double density = 0.25;
        for (int i = 0; i < life.getNumberOfRows(); i++) {
            for (int j = 0; j < life.getNumberOfColumns(); j++) {
                life.setAlive(i, j, random.nextDouble() < density);
            }
        }
    }
}
