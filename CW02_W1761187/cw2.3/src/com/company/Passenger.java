package com.company;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Passenger {

    private String name;
    private Integer seatNumber;
    private Integer secondsInQueue;

    ArrayList <Integer> delayTime = new ArrayList<>(42);

    public String getName(int passengerIndex){
        this.name = PassengerQueue.waitingList.get(passengerIndex);
        return name;
    }

    public String getFirstName(int passengerIndex){
        String names = PassengerQueue.waitingList.get(passengerIndex);
        String[] name = names.split(" ");
        return name[0];
    }
    public int getSeatNumber(int seatNumber){
            this.seatNumber = seatNumber;
            return seatNumber;
        }

    public void setNameAndSeat(int seatNumber, String passengerName){
        PassengerQueue.trainQ.set(seatNumber, passengerName);
    }

    public int getSeconds(){

        int max = 6;
        int min = 1;

        for (int i = 0; i < PassengerQueue.trainQ.size(); i++){
            //getting random numbers
            int first = (int) (Math.random() * ((max - min) + 1) + min);
            int second = (int) (Math.random() * ((max - min) + 1) + min);
            int third = (int) (Math.random() * ((max - min) + 1) + min);

            this.secondsInQueue = first + second + third;
        }
        return secondsInQueue;
    }

    public void setSecondsInQueue(){

        for (int i = 0; i < PassengerQueue.trainQ.size(); i++){

            if (!PassengerQueue.trainQ.get(i).equals("")){
                delayTime.add(i, getSeconds());
            }else {
                delayTime.add(i,null);
            }
        }
    }

    public void display(){

        PassengerQueue passengerQueue = new PassengerQueue();

        Stage newStage = new Stage();
        newStage.setTitle("Denuwara manike - Train Queue");
        AnchorPane newAnchorPane = new AnchorPane();
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(30, 20, 10, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.setStyle("-fx-background-color:#12355b;");
        Button[] detail = new Button[42];

        setSecondsInQueue();

        for (int i = 0; i < 42; i++) {
            detail[i] = new Button("SEAT " + (i + 1));

            detail[i].setStyle(" -fx-background-color:#12355b;\n" +
                    "     -fx-pref-height:70;\n" +
                    "     -fx-pref-width:120;\n" +
                    "     -fx-text-fill:#9a5052;\n" +
                    "     -fx-font-weight:bold;\n" +
                    "     -fx-border-width:2;\n" +
                    "     -fx-border-color:#9a5052;");

            if (!PassengerQueue.trainQ.get(i).equals("")){
                detail[i].setText("SEAT " + (i + 1) + "\nNAME " + PassengerQueue.trainQ.get(i) + "\nDELAY " + delayTime.get(i));
                detail[i].setStyle(" -fx-background-color:#12355b;\n" +
                        "     -fx-pref-height:70;\n" +
                        "     -fx-pref-width:120;\n" +
                        "     -fx-text-fill:#bbdce7;\n" +
                        "     -fx-font-weight:bold;\n" +
                        "     -fx-border-width:2;\n" +
                        "     -fx-border-color:#bbdce7;");
            }
        }

        int index = 0;               //set button index
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 6; y++) {
                gridPane.add(detail[index++], x, y);
            }
        }

        int max = 0;
        int min = 0;
        double sum = 0;
        int length = 0;

        for (int x = 0; x < delayTime.size(); x++) {
            if (delayTime.get(x) != null) {

                length++;

                    if (delayTime.get(x) > max) {
                        max = delayTime.get(x);
                    }
                    if (length == 1) {      //if there is only one passenger
                        min = max;
                    }
                    if (delayTime.get(x) < min) {
                        min = delayTime.get(x);
                    }

                sum += delayTime.get(x);
            }
        }
        double average = sum / length;

        Label maximum = new Label("Maximum delay  : " + max);
        maximum.setLayoutX(50);
        maximum.setLayoutY(550);
        maximum.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");

        Label minimum = new Label("Minimum delay  : " + min);
        minimum.setLayoutX(50);
        minimum.setLayoutY(580);
        minimum.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");

        Label avg = new Label("Average delay  : " + average);
        avg.setLayoutX(50);
        avg.setLayoutY(610);
        avg.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");

        Label maxLength = new Label("Maximum length : " + length);
        maxLength.setLayoutX(50);
        maxLength.setLayoutY(640);
        maxLength.setStyle("-fx-text-fill:#fffcf9; -fx-font-size:14;");

        newAnchorPane.setStyle("-fx-background-color:#12355b;");
        newAnchorPane.getChildren().addAll(gridPane,maximum,minimum,avg,maxLength);
        Scene scene = new Scene(newAnchorPane, 980, 680);
        newStage.setScene(scene);
        newStage.showAndWait();

        try {
            print();
            System.out.println("Successfully produced the text report!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        removeTrainQueue();
        System.out.println("Successfully removed passengers from the train queue!\n");
    }

    public void print() throws IOException {

        File passengerDetails = new File("Passengers.txt");
        FileWriter fileWriter = new FileWriter(passengerDetails);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (int i = 0; i < PassengerQueue.trainQ.size(); i++) {
            if (!PassengerQueue.trainQ.get(i).equals("")){
                String names = PassengerQueue.trainQ.get(i);
                int seat = PassengerQueue.trainQ.indexOf(names);
                String[] name = names.split(" ");       //getting first name and second name separately
                String firstName = name[0];     //first name
                String lastName = name[1];      //second name

                int seconds = delayTime.get(i);

                printWriter.println("Seat Number : " + (seat + 1) + "\nFirst Name : " + firstName + "\nLast name : " + lastName + "\nSeconds in queue : " + seconds + "s\n");
            }
        }
        printWriter.close();
    }

    public void removeTrainQueue(){

        for (int i = 0; i < PassengerQueue.trainQ.size(); i++){
            String name = PassengerQueue.trainQ.get(i);
            PassengerQueue.trainQ.remove(name);         //remove all the passengers
        }
    }

}
