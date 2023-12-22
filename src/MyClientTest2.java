import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MyClientTest2 extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;
    private Scanner scanner;

    public static void main(String[] args) {
        MyClientTest2 client = new MyClientTest2();
        client.start();
    }
    
    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 6666);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            scanner = new Scanner(System.in);

            System.out.println("Enter your name");
            String clientname = scanner.nextLine();

            dos.writeUTF(clientname);
            dos.flush();

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
                message = scanner.nextLine();
                dos.writeUTF(clientname + ": " + message);
                dos.flush();
            } while (!message.equals("End"));
            socket.close();
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
