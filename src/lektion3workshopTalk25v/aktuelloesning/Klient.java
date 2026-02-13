package lektion3workshopTalk25v.aktuelloesning;

import java.io.IOException;
import java.io.*;
import java.net.Socket;

public class Klient {
    public static void main(String[] args) {
        String host = "10.10.131.117";
        int port = 6789;

        try {
            Socket socket = new Socket(host, port);

            BufferedReader inFromServer =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter outToServer =
                    new PrintWriter(socket.getOutputStream(), true); // auto flush

            BufferedReader inFromUser =
                    new BufferedReader(new InputStreamReader(System.in));

            // Læs navn først
            System.out.print("Enter name: ");
            String name = inFromUser.readLine();
            if (name == null || name.isEmpty()) {
                System.out.println("No name entered. Closing client.");
                socket.close();
                return;
            }

            outToServer.println(name);

            //  Tråd der læser fra serveren
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = inFromServer.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });

            readerThread.start();

            // Main tråd skriver til serveren
            String input;
            while ((input = inFromUser.readLine()) != null) {
                outToServer.println(input);

                if ("/quit".equalsIgnoreCase(input)) {
                    break;
                }
            }

            // Luk forbindelsen pænt
            socket.close();
            System.out.println("Client closed.");

        } catch (IOException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        }
    }
}
