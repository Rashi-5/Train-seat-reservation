package com.company;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Scanner;

public class TrainStation extends Application {

    public static void main(String[] args) {

        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            //Select date and trip

            Stage stage = new Stage();
            stage.setTitle("Denuwara manike Train-Date & Trip");
            AnchorPane anchorPane = new AnchorPane();

            Label head = new Label("DENUWARA MANIKE TRAIN STATION \n");
            head.setLayoutX(90);
            head.setLayoutY(30);
            head.setStyle("-fx-text-fill:#bfdce7; -fx-font-size:18;");

            Label tripOn = new Label("Choose trip : ");
            tripOn.setLayoutX(30);
            tripOn.setLayoutY(80);
            tripOn.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");

            Label tripTo = new Label("Choose Date : ");
            tripTo.setLayoutX(30);
            tripTo.setLayoutY(120);
            tripTo.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");

            Label tripReq = new Label("* Choose your destination");
            tripReq.setLayoutX(300);
            tripReq.setLayoutY(80);
            tripReq.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");
            tripReq.setVisible(false);

            Label dateReq = new Label("* Date is missing");
            dateReq.setLayoutX(300);
            dateReq.setLayoutY(120);
            dateReq.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");
            dateReq.setVisible(false);

            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            //get items for the choice box
            choiceBox.getItems().add("Colombo to Badulla");
            choiceBox.getItems().add("Badulla to Colombo");
            choiceBox.getItems().add("--Choose the trip--");
            choiceBox.setValue("--Choose the trip--");
            choiceBox.setLayoutX(120);
            choiceBox.setLayoutY(80);

            Button next = new Button("Go");
            next.setLayoutX(250);
            next.setLayoutY(200);
            next.setStyle("-fx-background-color:#bbdce7; -fx-pref-height:40; -fx-pref-width:70;");

            DatePicker datePicker = new DatePicker();
            LocalDate today = LocalDate.now();
            LocalDate endDate = LocalDate.of(2025, 5, 1);
            datePicker.setDayCellFactory(d ->
                    new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            setDisable(item.isAfter(endDate) || item.isBefore(today));
                        }
                    });
            HBox hbox = new HBox(datePicker);
            hbox.setLayoutX(120);
            hbox.setLayoutY(120);

            final String[] destination = new String[1];         //get trip
            final LocalDate[] value = new LocalDate[1];         //get date
            next.setOnAction(event -> {
                if (choiceBox.getValue().equals("--Choose the trip--")) {
                    tripReq.setVisible(true);
                } else if (datePicker.getValue() == null) {
                    dateReq.setVisible(true);
                } else {
                    value[0] = datePicker.getValue();
                    destination[0] = choiceBox.getValue();
                    System.out.println("\nTrip to " + destination[0] + " On " + value[0] + "\n");
                    stage.close();
                }
            });

            anchorPane.setStyle("-fx-background-color:#12355b;");
            anchorPane.getChildren().addAll(hbox, tripOn, tripReq, choiceBox, next, tripTo, dateReq,head);
            Scene scene = new Scene(anchorPane, 500, 300);
            stage.setScene(scene);
            stage.showAndWait();

            String dateKey = value[0] + destination[0];

            //if null reopen the gui
        if (choiceBox.getValue().equals("--Choose the trip--") || (datePicker.getValue() == null)){
            System.out.println("Please choose date and the destination!");
            start(primaryStage);
        }else {

            System.out.println("|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
            System.out.println("|              D E N U W A R A    M A N I K E                  |");
            System.out.println("|                T R A I N    S T A T I O N                    |");
            System.out.println("|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");

            int count = 0;          //get count of how many times waiting room method has run
            int round = 0;          //get count of how many times add passengers to train queue method has run
            int store = 0;          //get count of how many times store train queue method has run

            while (true) {
                System.out.println("~~~ Enter 'W' to go to the waiting room.");
                System.out.println("~~~ Enter 'A' to add passenger to the train queue.");
                System.out.println("~~~ Enter 'V' to view the train queue.");
                System.out.println("~~~ Enter 'D' to delete passenger from the train queue.");
                System.out.println("~~~ Enter 'S' to store the train queue data.");
                System.out.println("~~~ Enter 'L' to load the train Queue data.");
                System.out.println("~~~ Enter 'R' to display the summary report, produce the text report and quit.");

                Scanner sc = new Scanner(System.in);
                System.out.print("\n Enter your option : ");
                String option = sc.nextLine().toUpperCase();

                PassengerQueue passengerQ = new PassengerQueue();
                Passenger passenger = new Passenger();

                switch (option) {
                    case "W":           //go to the waiting room
                        if (count == 0) {
                            passengerQ.waitingRoom(dateKey);
                            count++;
                        } else {
                            System.out.println("You have already run this option once!");
                            continue;
                        }
                        break;

                    case "A":           //add passengers to the waiting room
                        if (round == 0 && count > 0) {
                            passengerQ.addToTrainQueue();
                            round++;
                        } else if (count == 0) {
                            System.out.println("You haven`t added passengers to the waiting room yet!");
                        } else {
                            System.out.println("You have already added passengers to the train queue.");
                            continue;
                        }
                        break;
                    case "V":           //view the train queue
                        if (round == 0) {
                            System.out.println("Nothing to view!");
                            continue;
                        } else {
                            passengerQ.displayTrainQueue();
                        }
                        break;

                    case "D":           //delete passenger from the train queue
                        if (round == 0) {
                            System.out.println("Train queue is still empty!");
                            continue;
                        } else {
                            passengerQ.deleteFromTrainQueue();
                        }
                        break;

                    case "S":           //store train queue data to mongoDB
//                        if (round == 0) {
//                            System.out.println("Nothing to store. Train queue is empty!");
//                            continue;
//                        } else {
//                            passengerQ.storeTrainQueue();
//                            store++;
//                        }
                        break;

                    case "L":          //load data from mongoDB
//                        if (round == 0) {
//                            System.out.println("Nothing to load. Train queue is empty!");
//                            continue;
//                        } else {
//                            passengerQ.loadTrainQueue();
//                        }
                        break;

                    case "R":           //run the simulation program and create the text file
                        if (round == 0) {
                            System.out.println("Cannot run the simulation program.Train queue is empty!");
                            continue;
                        } else if (store == 0){
                            System.out.println("Store data before exiting the program!");
                            continue;
                        }else {
                            passenger.display();
                        }
                        System.out.println("You are exiting the program!");
                        System.exit(0);         //quit
                }
            }
        }
        } catch (Exception e) {
            System.out.println("Something went wrong! Please try again..");
        }
    }
}