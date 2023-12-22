import java.io.*;
import java.net.*;


public class MyServer extends Thread {
    public static void main(String[] args) {
        MyServer thread = new MyServer();
        thread.start();
        System.out.println("Serveur en cours d'exécution");
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(6666);
            
            System.out.println("Server waiting for clients...");

            Socket client1 = ss.accept();
            //getInetAddress return IP address of the client
            //getHostAddress return the IP address in the correct x:x:x:x format
            System.out.println("Client 1 connected: " + client1.getInetAddress().getHostAddress());
            DataInputStream dis1 = new DataInputStream(client1.getInputStream());
            

            Socket client2 = ss.accept();
            System.out.println("Client 2 connected: " + client2.getInetAddress().getHostAddress());
            DataInputStream dis2 = new DataInputStream(client2.getInputStream());
            
            String clientName1 = dis1.readUTF();
            
            String clientName2 = dis2.readUTF();

            //ClientHandler will create an instance of the class using a thread for each client
            ClientHandler clientHandler1 = new ClientHandler(client1, client2, clientName1);
            ClientHandler clientHandler2 = new ClientHandler(client2, client1, clientName2);

            // Start the threads
            clientHandler1.start();
            clientHandler2.start();

            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    class ClientHandler extends Thread {
        private Socket clientSocket;
        private Socket otherClientSocket;
        private String clientname;
        //private boolean pastMessagesSent = false;

        public ClientHandler(Socket clientSocket, Socket otherClientSocket, String clientname) {
            this.clientSocket = clientSocket;
            this.otherClientSocket = otherClientSocket;
            this.clientname = clientname;
        }
    
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(otherClientSocket.getOutputStream());

                String message;
                while (true) {
                    message = dis.readUTF();

                    String[] fullmessage = message.split(": ");
                    //saveMsgToDB(clientname, fullmessage[1]);
                    if (fullmessage[1].equals("End")) {
                        dos.writeUTF(clientname+ " disconnected");
                        break;
                    }
                    
                    
                    if (!fullmessage[1].equals("End"))
                    {
                        // Forward the message to the other client
                        dos.writeUTF(message);
                        
                        dos.flush();
                    }
                }
                dos.flush();
                // Close the resources
                dis.close();
                dos.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(clientname + " disconnected");
            }
        }

        
     }
}