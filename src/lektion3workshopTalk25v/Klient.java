package lektion3workshopTalk25v;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Klient {
    public static void main(String[] args) throws IOException {
        String host = "192.168.0.83";
        int port = 6789;
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        try (Socket socket = new Socket(host, port);
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)))
        {

            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = inFromServer.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {
                }
            }, "chat-reader");
            readerThread.start();

            System.out.print("Enter name: ");
            String name = inFromUser.readLine();
            if (name == null) {
                return;
            }
            outToServer.println(name);

            String input;
            while ((input = inFromUser.readLine()) != null) {
                outToServer.println(input);
                if ("/quit".equalsIgnoreCase(input)) {
                    break;
                }
            }
        }
    }
}
