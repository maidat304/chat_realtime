import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String serverAddress;
    private int serverPort;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start() {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + socket);

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            Thread serverThread = new Thread(new ServerHandler(socket));
            serverThread.start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                writer.println(message);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerHandler implements Runnable {
        private Socket socket;
        private Scanner reader;

        public ServerHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new Scanner(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                if (reader.hasNextLine()) {
                    String message = reader.nextLine();
                    System.out.println("Received message from server: " + message);
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 8080);
        client.start();
    }
}
