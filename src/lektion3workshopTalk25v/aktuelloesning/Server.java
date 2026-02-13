package lektion3workshopTalk25v.aktuelloesning;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private static final int port = 6789;
    // Key = Name, Value = Socket
    private static HashMap<String, String> klienter = new HashMap<>();

    public static void main(String[] args) throws IOException {
        getKlienter().put("10.10.131.85", "Gustav");
        getKlienter().put("Simon1", "127.0.0.1");
        getKlienter().put("Simon2", "localhost");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for connection...");
            while(true){
                Socket connSocket = serverSocket.accept();
                System.out.println("Connected to " + connSocket.getInetAddress());

                // Streams til netværk
                BufferedReader inFromClient = new BufferedReader
                        (new InputStreamReader(connSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream
                        (connSocket.getOutputStream());

                // Læs input fra serverens tastatur
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

                // "Bordtennis"-chat
                Thread readerThread = new Thread(() -> {
                    try {
                        String msgFromClient;
                        while ((msgFromClient = inFromClient.readLine()) != null) {
                            System.out.println("Klient: " + msgFromClient);
                            if ("exit".equals(msgFromClient)) {
                                break;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }, "server-reader");
                readerThread.start();

                while (true) {
                    System.out.print("ServerHost> ");
                    String msgToClient = inFromUser.readLine();
                    String msgFromClient =  inFromClient.readLine();
                    if (msgToClient == null) {
                        break;
                    }
                    outToClient.writeBytes(msgToClient + "\n");
                    if ("/quit".equals(msgFromClient)) {
                        break;
                    }
                    if("/list".equals(msgToClient) || "/list".equals(msgFromClient)) {
                        outToClient.writeBytes(klienter.toString()+ "\n");
                        System.out.println(klienter.toString()+ "\n");
                    }
                }
                connSocket.close();
                serverSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static HashMap<String, String> getKlienter() {
        return klienter;
    }
}
