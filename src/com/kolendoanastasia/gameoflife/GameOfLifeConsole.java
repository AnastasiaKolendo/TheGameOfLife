package com.kolendoanastasia.gameoflife;

import java.util.Random;
import java.util.Scanner;

public class GameOfLifeConsole {

    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int numberOfRows = 10;
        int numberOfColumns = 20;

        System.out.println("The Game Of Life");
        System.out.println("Let's start!");

        Life life = new Life(numberOfRows, numberOfColumns);
        createFirstGeneration(life);
        System.out.println("The first generation: ");
        printCells(life);

        System.out.println();
        System.out.println("Press enter for next generation or print 'exit' to end simulation");
        String answer = scanner.nextLine();
        while (!answer.equalsIgnoreCase("exit")) {
            life.evolve();
            printCells(life);
            System.out.println();
            System.out.println("Press enter for next generation or print 'exit' to end simulation");
            answer = scanner.nextLine();
        }

        System.out.println();
        System.out.println("Game is over");
    }

    public static void createFirstGeneration(Life life) {
        Random random = new Random();
        double density = 0.25;
        for (int i = 0; i < life.getNumberOfRows(); i++) {
            for (int j = 0; j < life.getNumberOfColumns(); j++) {
                life.setAlive(i, j, random.nextDouble() < density);
            }
        }
    }

    private static void printCells(Life life) {
        for (int i = 0; i < life.getNumberOfRows(); i++) {
            for (int j = 0; j < life.getNumberOfColumns(); j++) {
                System.out.print(life.isAlive(i, j) ? '*' : ' ');
            }
            System.out.println();
        }
    }
}
