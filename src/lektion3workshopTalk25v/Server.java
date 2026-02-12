package lektion3workshopTalk25v;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final int port;
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket), "client-handler").start();
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients.values()) {
            client.send(message);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String name;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Socket s = socket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 PrintWriter writer = new PrintWriter(s.getOutputStream(), true))
            {
                this.out = writer;

                while (true) {
                    out.println("Enter name:");
                    String proposed = in.readLine();
                    if (proposed == null) {
                        return;
                    }
                    proposed = proposed.trim();
                    if (proposed.isEmpty()) {
                        continue;
                    }
                    if (clients.putIfAbsent(proposed, this) == null) {
                        name = proposed;
                        out.println("OK");
                        broadcast("** " + name + " joined **");
                        break;
                    }
                    out.println("Name taken, try again");
                }

                String line;
                while ((line = in.readLine()) != null) {
                    if ("/quit".equalsIgnoreCase(line)) {
                        break;
                    }
                    if (!line.trim().isEmpty()) {
                        broadcast(name + ": " + line);
                    }
                }
            } catch (IOException ignored) {
            } finally {
                if (name != null) {
                    clients.remove(name);
                    broadcast("** " + name + " left **");
                }
            }
        }

        private void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 6789;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        new Server(port).start();
    }
}
