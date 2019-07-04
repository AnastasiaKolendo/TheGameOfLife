package com.kolendoanastasia.gameoflife;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class GameOfLifeGui extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Game Of Life");
        primaryStage.show();

        Label label1 = new Label("The Game Of Life!");
        Label label2 = new Label("Let's start!");
        Label label3 = new Label("");
        Label label4 = new Label("The first generation: ");
        Label label5 = new Label("");

        final int numberOfRows = 15;
        final int numberOfColumns = 15;

        Life life = new Life(numberOfRows, numberOfColumns);
        createFirstGeneration(life);

        GridPane gridPane = new GridPane();
        addCellLabels(life, gridPane, numberOfRows, numberOfColumns);

        Label labelRows = new Label("Select number of rows: ");
        Label labelColumns = new Label("Select numbr of columns: ");

        Spinner<Integer> spinner1 = new Spinner<>();
        Spinner<Integer> spinner2 = new Spinner<>();

        SpinnerValueFactory<Integer> valueFactoryRows =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20, numberOfRows);

        SpinnerValueFactory<Integer> valueFactoryColumns = new
                SpinnerValueFactory.IntegerSpinnerValueFactory(5,20, numberOfColumns);

        spinner1.setValueFactory(valueFactoryRows);
        spinner2.setValueFactory(valueFactoryColumns);

        ChangeListener<Integer> resizeOnValueChange = (obs, oldValue, newValue) -> {
            int newNumberOfRows = spinner1.getValue();
            int newNumberOfColumns = spinner2.getValue();
            life.resize(newNumberOfRows, newNumberOfColumns);
            gridPane.getChildren().clear();
            addCellLabels(life, gridPane, newNumberOfRows, newNumberOfColumns);
        };
        spinner1.valueProperty().addListener(resizeOnValueChange);
        spinner2.valueProperty().addListener(resizeOnValueChange);
        /*int newNumberOfColumns = (Integer) spinner2.getValue();
        int newNumberOfRows = (Integer) spinner1.getValue();*/
      /*  gridPane.getColumnConstraints().add(new ColumnConstraints(newNumberOfColumns));
        gridPane.getRowConstraints().add(new RowConstraints(newNumberOfRows));*/


        Label label6 = new Label("Press 'next generation' for next generation or 'exit' to end simulation");
        Button nextGenerationButton = new Button("next generation");
        Button exitButton = new Button("exit");
        exitButton.setOnAction(e -> Platform.exit());

        HBox hbox = new HBox(20, nextGenerationButton, exitButton);
        HBox hbox2 = new HBox(20, labelRows, spinner1, labelColumns, spinner2);
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
            }
            nextGenerationButton.setText("Stop");
            label4.setText(" ");
            label6.setText("Press 'stop' to stop simulation or 'exit' to end it");
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

        VBox vbox = new VBox(label1, label2, label3, label4, label5, gridPane, label6, hbox, hbox2);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 600, 700);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void addCellLabels(Life life, GridPane gridPane, int numberOfRows, int numberOfColumns) {
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
