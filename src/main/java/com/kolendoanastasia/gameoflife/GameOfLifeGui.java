package com.kolendoanastasia.gameoflife;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class GameOfLifeGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("The Game Of Life");
        primaryStage.show();

        final int initialNumberOfRows = 30;
        final int initialNumberOfColumns = 45;

        Life life = new Life(initialNumberOfRows, initialNumberOfColumns);
        createFirstGeneration(life);

        GridPane gridPane = new GridPane();
        addCellLabels(life, gridPane);
        gridPane.setPadding(new Insets(10));
        gridPane.setMinSize(300, 525);
        gridPane.setAlignment(Pos.CENTER);

        Label labelRows = new Label("Number of rows:");
        Label labelColumns = new Label("Number of columns:");
        Spinner<Integer> spinner1 = new Spinner<>();
        Spinner<Integer> spinner2 = new Spinner<>();
        spinner1.setMaxWidth(75);
        spinner2.setMaxWidth(75);
        SpinnerValueFactory<Integer> valueFactoryRows =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, initialNumberOfRows);
        SpinnerValueFactory<Integer> valueFactoryColumns = new
                SpinnerValueFactory.IntegerSpinnerValueFactory(5, 100, initialNumberOfColumns);
        spinner1.setValueFactory(valueFactoryRows);
        spinner2.setValueFactory(valueFactoryColumns);
        ChangeListener<Integer> resizeOnValueChange = (obs, oldValue, newValue) -> {
            int newNumberOfRows = spinner1.getValue();
            int newNumberOfColumns = spinner2.getValue();
            life.resize(newNumberOfRows, newNumberOfColumns);
            gridPane.getChildren().clear();
            addCellLabels(life, gridPane);
        };
        spinner1.valueProperty().addListener(resizeOnValueChange);
        spinner2.valueProperty().addListener(resizeOnValueChange);
        HBox hBoxSpinner = new HBox(20, labelRows, spinner1, labelColumns, spinner2);

        Button nextGenerationButton = new Button("Start");

        Button clearButton = new Button("Reset");

        Button buttonExport = new Button("Export");
        Button buttonImport = new Button("Import");
        HBox hBoxImportExport = new HBox(20, clearButton, buttonExport, buttonImport);

        Slider slider = new Slider(0, 900, 5);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(100);
        slider.setMinorTickCount(4);
        slider.setMinWidth(400);
        AtomicInteger evolutionSpeed = new AtomicInteger(0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> evolutionSpeed.set(newValue.intValue()));

        HBox hbox = new HBox(20, nextGenerationButton, slider);

        Alert a = new Alert(Alert.AlertType.NONE);

        Runnable updater = () -> {
            life.evolve();
            for (Node child : gridPane.getChildren()) {
                int i = GridPane.getColumnIndex(child);
                int j = GridPane.getRowIndex(child);
                String color = life.isAlive(j, i) ? "000000" : "FFFFFF";
                child.setStyle("-fx-background-color: #" + color + ";");
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

        buttonExport.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(life.getNumberOfRows() + System.lineSeparator());
                    fileWriter.write(life.getNumberOfColumns() + System.lineSeparator());
                    for (int i = 0; i < life.getNumberOfRows(); i++) {
                        for (int j = 0; j < life.getNumberOfColumns(); j++) {
                            String str = Boolean.toString(life.isAlive(i, j));
                            fileWriter.write(str + System.lineSeparator());
                        }
                    }
                    fileWriter.close();
                } catch (IOException ex) {
                    a.setTitle("IOException");
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("IO Exception occurred");
                    a.show();
                }
            }
        });

        buttonImport.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String stringLabel;
                    int numberOfRowsRead = Integer.parseInt(bufferedReader.readLine());
                    int numberOfColumnsRead = Integer.parseInt(bufferedReader.readLine());
                    life.resize(numberOfRowsRead, numberOfColumnsRead);
                    gridPane.getChildren().clear();
                    for (int i = 0; i < numberOfRowsRead; i++) {
                        for (int j = 0; j < numberOfColumnsRead; j++) {
                            stringLabel = bufferedReader.readLine();
                            boolean label = Boolean.parseBoolean(stringLabel);
                            life.setAlive(i, j, label);
                        }
                    }
                } catch (FileNotFoundException e) {
                    a.setTitle("FileNotFoundException");
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("FileNotFoundException occurred");
                    a.show();
                } catch (IOException e) {
                    a.setTitle("IOException");
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("IOException occurred");
                    a.show();
                } catch (Exception e) {
                    a.setTitle("Error");
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("An error occurred");
                    a.show();
                }
            }
            addCellLabels(life, gridPane);
        });

        nextGenerationButton.setOnAction(event -> {
            if (thread[0] == null) {
                Thread t = new Thread(evaluator);
                t.setDaemon(true);
                t.start();
                thread[0] = t;
                nextGenerationButton.setText("Stop");
            } else {
                thread[0].interrupt();
                thread[0] = null;
                nextGenerationButton.setText("Start");
            }
        });

        clearButton.setOnAction((ActionEvent event) -> {
            life.resize(life.getNumberOfRows(), life.getNumberOfColumns());
            createFirstGeneration(life);
            addCellLabels(life, gridPane);
        });

        VBox vBoxFinal = new VBox(10, gridPane, hbox, hBoxSpinner, hBoxImportExport);
        vBoxFinal.setPadding(new Insets(10));
        Scene scene = new Scene(vBoxFinal, 600, 680);
        scene.setFill(Color.BLUE);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void addCellLabels(Life life, GridPane gridPane) {
        for (int i = 0; i < life.getNumberOfColumns(); i++) {
            for (int j = 0; j < life.getNumberOfRows(); j++) {
                Label label = new Label();
                label.setMinWidth(10);
                label.setMaxWidth(10);
                label.setMinHeight(10);
                label.setMaxHeight(10);
                String color = life.isAlive(j, i) ? "000000" : "FFFFFF";
                label.setStyle("-fx-background-color: #" + color + ";");
                int row = j;
                int column = i;
                label.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        life.setAlive(row, column, true);
                        label.setStyle("-fx-background-color: #000000;");
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        life.setAlive(row, column, false);
                        label.setStyle("-fx-background-color: #FFFFFF;");
                    }
                });

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
