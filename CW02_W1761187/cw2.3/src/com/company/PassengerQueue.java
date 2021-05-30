package com.company;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.bson.Document;
import java.util.ArrayList;
import java.util.Scanner;

public class PassengerQueue {

    private static int passengerIndex = 0;
    static ArrayList<String> waitingList = new ArrayList(42);       //waiting room circular arraylist
    static ArrayList<String> trainQ = new ArrayList<>(42);          //trainqueue arraylist
    static ArrayList<String> getIndex= new ArrayList<>(42);         //temporary arraylist

    public void waitingRoom(String dateKey) {

        ArrayList<String> customerDetails = null;

        //connecting mongoDB
        MongoClient train = new MongoClient("localhost", 27017);
        MongoDatabase database = train.getDatabase("Train_Reservation");
        MongoCollection<Document> collection = database.getCollection("CustomerDetails");

        for (Document details : collection.find()) {       //find each data in document
            customerDetails = (ArrayList<String>) details.get(dateKey);
        }
        //waiting room
        try {
            Scanner sc = new Scanner(System.in);
            if (customerDetails == null) {
                System.out.println("\nThere are no any data related to this date or trip!\nPress 'E' to exit..\n");
            } else {
                System.out.println("Successfully loaded the data related to " + dateKey + "\n");
            }
            String input = "";
            System.out.println("|--------------------------------------------------------------|");
            System.out.println("|              W A I T I N G      R O O M                      |");
            System.out.println("|--------------------------------------------------------------|\n");
            System.out.println("!!! PLEASE NOTE THAT ONCE YOU HAVE EXIT THE WAITING ROOM YOU CANNOT ADD ANY PASSENGERS AGAIN !!!\n");
            System.out.println("~~~ Press 'A' to add passenger to the waiting room");
            System.out.println("~~~ Press 'D' to display the waiting room");
            System.out.println("~~~ Press 'R' to remove passenger from the waiting room");
            System.out.println("~~~ Press 'E' to go to the trainQueue");

            for (int z = 0; z < 42; z++) {
                getIndex.add(z, "");
            }

            do {
                System.out.print("Your option: ");
                input = sc.nextLine().toLowerCase();

                if (input.equals(String.valueOf('a'))) {
                    System.out.print("Enter passenger name to check the bookings.");
                    String custName = sc.nextLine().toLowerCase();

                    if (customerDetails.contains(custName)) {
                        int index = customerDetails.indexOf(custName);          //getting the data to a new temporary array
                        getIndex.set(index, custName);
                        customerDetails.set(index, "");                         //remove added passengers
                        addToWaitingRoom(custName);
                        System.out.println("Passenger added successfully!");
                    } else {
                        System.out.println("There is no any name as " + custName + " or " + custName + " is already in the waiting room.");
                    }

                } else if (input.equals(String.valueOf('r'))) {         //remove passenger from the waiting room
                    System.out.println("Enter name to remove : ");
                    String removeName = sc.nextLine().toLowerCase();
                    remove(removeName);

                } else if (input.equals(String.valueOf('d'))) {         //display waiting room
                    display();

                } else if (input.equals(String.valueOf('e'))) {
                    System.out.println("You are exiting the waiting room!\n");

                } else {
                    System.out.println("Please enter valid option!");
                }

            } while (!input.equals(String.valueOf('e')));       //exit waiting room

        } catch (Exception e) {
            System.out.println("Something went wrong!");
        }
    }
    public void addToTrainQueue() {

        try {
            //creating array for the train queue
            for (int m = 0; m < 42; m++) {
                trainQ.add(m, "");
            }
            //train queue GUI
            Stage newStage = new Stage();
            newStage.setTitle("Denuwara manike - Train Queue");
            AnchorPane newAnchorPane = new AnchorPane();
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(30, 20, 10, 20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            gridPane.setStyle("-fx-background-color:#12355b;");
            Button[] queue = new Button[42];

            Button addTo = new Button("Add to train queue");
            addTo.setLayoutY(600);
            addTo.setLayoutX(150);
            addTo.setStyle(" -fx-background-color:#12355b;\n" +
                    "     -fx-pref-height:50;\n" +
                    "     -fx-pref-width:200;\n" +
                    "     -fx-text-fill:#bbdce7;\n" +
                    "     -fx-font-weight:bold;\n" +
                    "     -fx-border-width:2;\n" +
                    "     -fx-border-color:#bbdce7;");

            for (int i = 0; i < 42; i++) {


                queue[i] = new Button("SEAT " + (i + 1) + " - EMPTY");

                queue[i].setStyle(" -fx-background-color:#bbdce7;\n" +
                        "     -fx-pref-height:30;\n" +
                        "     -fx-pref-width:150;\n" +
                        "     -fx-text-fill:#272d2d;\n" +
                        "     -fx-font-weight:bold;");

                addTo.setOnAction(event -> {
                    int max = 6;
                    int min = 1;
                    int dice = (int) (Math.random() * ((max - min) + 1) + min);     //getting random numbers

                    for (int j = 0; j < dice; j++) {
                        if (passengerIndex >= getLength()) {        //check whether the all passengers have added to the train queue
                            System.out.println("Waiting room is empty!");
                            addTo.setDisable(true);
                            break;
                        } else {
                            Passenger passengerObj = new Passenger();       //creating passenger objects
                            String name = passengerObj.getName(passengerIndex);     //get full name from the trainQ
                            int seatNumber = passengerObj.getSeatNumber(getIndex.indexOf(name));        //get seat number from the trainQ
                            passengerObj.setNameAndSeat(seatNumber, name);      //set name with the seat number
                            String firstName = passengerObj.getFirstName(passengerIndex);       //get first name
                            queue[seatNumber].setText(firstName);           //adding texts to the buttons
                            passengerIndex++;
                        }
                    }
                });
            }

            int sIndex = 0;            //set button index
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 14; y++) {
                    gridPane.add(queue[sIndex++], x, y);
                }
            }
            newAnchorPane.setStyle("-fx-background-color:#12355b;");
            newAnchorPane.getChildren().addAll(gridPane, addTo);
            Scene scene = new Scene(newAnchorPane, 500, 680);
            newStage.setScene(scene);
            newStage.showAndWait();

        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    public void displayTrainQueue(){

        try {

            Stage newStage = new Stage();
            newStage.setTitle("Denuwara manike - Train Queue");
            AnchorPane newAnchorPane = new AnchorPane();
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(30, 20, 10, 20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            gridPane.setStyle("-fx-background-color:#12355b;");
            Button[] queue = new Button[42];

            for (int i = 0; i < trainQ.size(); i++) {
                queue[i] = new Button("SEAT " + (i + 1) + " - EMPTY");

                queue[i].setStyle(" -fx-background-color:#bbdce7;\n" +
                        "     -fx-pref-height:30;\n" +
                        "     -fx-pref-width:150;\n" +
                        "     -fx-text-fill:#272d2d;\n" +
                        "     -fx-font-weight:bold;");

                if (!(trainQ.get(i).equals(""))) {

                    queue[i].setText(trainQ.get(i));
                    queue[i].setStyle(" -fx-background-color:#9a5052;\n" +
                            "     -fx-pref-height:30;\n" +
                            "     -fx-pref-width:150;\n" +
                            "     -fx-text-fill:#272d2d;\n" +
                            "     -fx-font-weight:bold;");
                }
            }

            int sIndex = 0;            //set button index
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 14; y++) {
                    gridPane.add(queue[sIndex++], x, y);
                }
            }
            newAnchorPane.setStyle("-fx-background-color:#12355b;");
            newAnchorPane.getChildren().addAll(gridPane);
            Scene scene = new Scene(newAnchorPane, 500, 680);
            newStage.setScene(scene);
            newStage.showAndWait();
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    public void deleteFromTrainQueue(){

        try {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter full name to remove from the train queue : ");
            String removeName = scanner.nextLine().toLowerCase();

            if (trainQ.contains(removeName)) {
                int removeIndex = trainQ.indexOf(removeName);
                trainQ.set(removeIndex,"");
                System.out.println("Successfully removed the passenger.\n");

            } else {
                System.out.println("Cannot find any name like " + removeName + " Please check and try again!\n");
            }
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    public void storeTrainQueue(){

        try {

            MongoClient board = new MongoClient("localhost", 27017);
            MongoDatabase database = board.getDatabase("TrainStation");
            MongoCollection<Document> boardings = database.getCollection("BoardingDetails");

            boardings.drop();          //remove previous stored data

            for (int k = 0; k < trainQ.size(); k++) { //append data to the database
                Document document = new Document()
                        .append(trainQ.get(k), (k + 1));
                boardings.insertOne(document);
            }
            System.out.println("Successfully stored to the database!\n");
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    public void loadTrainQueue(){

        try {
            MongoClient board = new MongoClient("localhost", 27017);
            MongoDatabase database = board.getDatabase("TrainStation");
            MongoCollection<Document> boardings = database.getCollection("BoardingDetails");

            FindIterable<Document> details = boardings.find();       //find each data in document

            //iteration of data
            for (Document detail : details) {
                System.out.println(detail);
            }
        }catch (Exception e){
            System.out.println("Something went wrong!");
        }
    }

    int capacity = 42;
    int length = 0;
    int first = -1;
    int last =-1;

    public void addToWaitingRoom(String temp){
        if (isFull()){
            System.out.println("Sorry maximum reached!\n");
        }else{
            last = (last + 1) % capacity;
            waitingList.add(last,temp);
            length++;
        }
    }

    public void remove(String temp){

        if ((first == -1 && last == -1)){
            isEmpty();
            System.out.println("Queue is empty !");
        }else if(first == last){        //if removing passenger is the last
            System.out.println("Removed passenger is " + waitingList.get(first));
            waitingList.remove(first);
            length--;
            isEmpty();
        }else {
            for (int i = 0; i < waitingList.size(); i++) {
                if(waitingList.get(i).equals(temp)){
                      waitingList.remove(i);
                }
            }

            System.out.println("Passenger has been removed.");
            first = first + 1 % capacity;
        }
    }
    public void display(){
        int i = 0;
        if (length == 0){
            isEmpty();
            System.out.println("Nothing to display. Queue is empty!\n");
        }else{
            System.out.println("Queue values are:");
           while (i != last){
                System.out.println(waitingList.get(i));
                i = (i + 1) % capacity;
            }System.out.println(waitingList.get(last));
        }
    }
        public boolean isEmpty(){
            first = 0;
            last = 0;
            return true;
        }
         public boolean isFull(){
        return ((last + 1) % capacity == first);
        }

        public int getLength(){
        int queueLength = 0;
            for (int q = 0; q < waitingList.size(); q++){
                if (waitingList.get(q) != null){
                    queueLength++;
                }
            }
            return queueLength;
        }
}

