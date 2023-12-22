import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MyClient2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            Socket socket = new Socket("localhost", 6666);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            System.out.println("Enter your name");
            String clientname = scanner.nextLine();

            dos.writeUTF(clientname);
            dos.flush();
            fetchPastMessages(clientname, dis);
            // Start a separate thread for receiving messages
            new Thread(() -> {
                try {
                    String receivedMessage;
                    while (true) {
                        receivedMessage = dis.readUTF();
                        System.out.println(receivedMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            }).start();

            // Start sending messages to the other client
            String message;
            System.out.print("Enter a message : ");
            System.out.println(" 'End' to stop");
            do {
                //System.out.print(clientname + ": ");
                message = scanner.nextLine();
                dos.writeUTF(clientname + ": " + message);
                dos.flush();
                saveMsgToDB(clientname, message);
            } while (!message.equals("End"));

            // Close resources
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        scanner.close();
    }
    private static void fetchPastMessages(String clientname, DataInputStream dis) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/messageserver", "root", "");

            // Retrieve past messages from the database
            String selectQuery = "SELECT Sender, Message FROM messages";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String sender = resultSet.getString("Sender");
                    String message = resultSet.getString("Message");

                    // Display past messages to the user
                    System.out.println(sender + ": " + message);
                }
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error fetching past messages for " + clientname + ": " + e.getMessage());
        }
    }
    private static void saveMsgToDB(String clientname, String message) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/messageserver", "root", "");

            // Insert the message into the database
            String insertQuery = "INSERT INTO messages (Sender, Message) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, clientname);
                preparedStatement.setString(2, message);
                preparedStatement.executeUpdate();
            }

            connection.close();
        } catch (SQLException e) {
            System.out.println("Error saving message to database: " + e.getMessage());
        }
    }
}
