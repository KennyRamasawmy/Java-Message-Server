import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MyClientTest extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;
    private Scanner scanner;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 6666);

            // Create an instance of MyClient1 and start the thread
            MyClientTest client = new MyClientTest(socket);
            client.start();
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }

    public MyClientTest(Socket socket) {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            scanner = new Scanner(System.in);
        } catch (IOException e) {
            System.out.println("Error creating input/output streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Enter your name");
            String clientname = scanner.nextLine();

            dos.writeUTF(clientname);
            dos.flush();

            // Use a thread per client as same IP and Port is used, server listen on 6666
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
                message = scanner.nextLine();
                dos.writeUTF(clientname + ": " + message);
                dos.flush();
            } while (!message.equals("End"));

        } catch (IOException e) {
            System.out.println("Error during communication: " + e.getMessage());
        } finally {
            try {
                // Close resources
                dis.close();
                dos.close();
            } catch (IOException e) {
                System.out.println("Error closing input/output streams: " + e.getMessage());
            }
        }
    }
}
