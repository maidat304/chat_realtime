import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private List<PrintWriter> clients;

    public Server() {
        clients = new ArrayList<>();
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clients.add(writer);

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (PrintWriter client : clients) {
            client.println(message);
            client.flush();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Scanner reader;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                reader = new Scanner(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                if (reader.hasNextLine()) {
                    String message = reader.nextLine();
                    System.out.println("Received message: " + message);
                    broadcastMessage(message);
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(8080);
    }
}
