package lektion3workshopTalk25v.aktuelloesning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UDPKlient {
    private static final int SERVER_PORT = 6789;
    private static final int MAX_PACKET_SIZE = 2048;

    public static void main(String[] args) throws Exception {
        String serverHost = args.length > 0 ? args[0] : "localhost";
        InetAddress serverAddress = InetAddress.getByName(serverHost);
        String hostName = InetAddress.getLocalHost().getHostName();
        int dotIndex = hostName.indexOf('.');
        String dnsName = dotIndex > 0 ? hostName.substring(0, dotIndex) : hostName;

        try (DatagramSocket clientSocket = new DatagramSocket();
             BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in))) {
            clientSocket.setSoTimeout(200);
            byte[] receiveData = new byte[MAX_PACKET_SIZE];

            System.out.print("Enter user name: ");
            String userName = inFromUser.readLine();
            if (userName == null || userName.isBlank()) {
                return;
            }

            String userId = userName.trim() + "@" + dnsName;
            byte[] joinData = ("JOIN|" + userName.trim()).getBytes(StandardCharsets.UTF_8);
            clientSocket.send(new DatagramPacket(joinData, joinData.length, serverAddress, SERVER_PORT));
            System.out.println("Connected as " + userId);
            System.out.println("Commands: /list, /quit");

            while (true) {
                if (inFromUser.ready()) {
                    String input = inFromUser.readLine();
                    if (input != null) {
                        if ("/quit".equalsIgnoreCase(input.trim())) {
                            byte[] leaveData = ("LEAVE|" + userId).getBytes(StandardCharsets.UTF_8);
                            clientSocket.send(new DatagramPacket(leaveData, leaveData.length, serverAddress, SERVER_PORT));
                            break;
                        }
                        if ("/list".equalsIgnoreCase(input.trim())) {
                            byte[] listData = "LIST".getBytes(StandardCharsets.UTF_8);
                            clientSocket.send(new DatagramPacket(listData, listData.length, serverAddress, SERVER_PORT));
                        } else {
                            byte[] msgData = ("MSG|" + userId + "|" + input).getBytes(StandardCharsets.UTF_8);
                            clientSocket.send(new DatagramPacket(msgData, msgData.length, serverAddress, SERVER_PORT));
                        }
                    }
                }

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    clientSocket.receive(receivePacket);
                    String payload = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8).trim();
                    String[] parts = payload.split("\\|", 3);
                    String command = parts[0];

                    if ("MSG".equalsIgnoreCase(command) && parts.length >= 3) {
                        System.out.println(parts[1] + ": " + parts[2]);
                        continue;
                    }
                    if ("SYS".equalsIgnoreCase(command) && parts.length >= 2) {
                        System.out.println("[SYS] " + parts[1]);
                        continue;
                    }
                    if ("LIST".equalsIgnoreCase(command) && parts.length >= 2) {
                        System.out.println("[LIST] " + parts[1]);
                        continue;
                    }
                    System.out.println(payload);
                } catch (SocketTimeoutException ignored) {
                }
            }
        }
    }
}
