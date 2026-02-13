package lektion3workshopTalk25v;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UDPServer {
    private static final int PORT = 9876;
    private static final int MAX_PACKET_SIZE = 2048;
    private static final Map<String, InetSocketAddress> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            socket.setSoTimeout(200);
            String hostName = InetAddress.getLocalHost().getHostName();
            int dotIndex = hostName.indexOf('.');
            String serverDnsName = dotIndex > 0 ? hostName.substring(0, dotIndex) : hostName;
            String serverUser = "server@" + serverDnsName;
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            byte[] receiveData = new byte[MAX_PACKET_SIZE];

            System.out.println("UDP server listening on port " + PORT);
            System.out.println("Server user: " + serverUser);
            System.out.println("Commands: /list, /quit");

            while (true) {
                if (inFromUser.ready()) {
                    String input = inFromUser.readLine();
                    if (input != null) {
                        if ("/quit".equalsIgnoreCase(input.trim())) {
                            byte[] msgData = ("MSG|" + serverUser + "|Server shutting down").getBytes(StandardCharsets.UTF_8);
                            for (InetSocketAddress target : clients.values()) {
                                socket.send(new DatagramPacket(msgData, msgData.length, target.getAddress(), target.getPort()));
                            }
                            break;
                        }
                        if ("/list".equalsIgnoreCase(input.trim())) {
                            StringBuilder list = new StringBuilder(serverUser);
                            for (String client : clients.keySet()) {
                                list.append(", ").append(client);
                            }
                            System.out.println("Participants: " + list);
                        } else {
                            byte[] msgData = ("MSG|" + serverUser + "|" + input).getBytes(StandardCharsets.UTF_8);
                            for (InetSocketAddress target : clients.values()) {
                                socket.send(new DatagramPacket(msgData, msgData.length, target.getAddress(), target.getPort()));
                            }
                        }
                    }
                }

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    socket.receive(receivePacket);
                    String payload = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8).trim();
                    String[] parts = payload.split("\\|", 3);
                    String command = parts[0];

                    if ("JOIN".equalsIgnoreCase(command) && parts.length >= 2) {
                        String clientName = parts[1].trim();
                        String clientHost = receivePacket.getAddress().getHostName();
                        int clientDot = clientHost.indexOf('.');
                        String clientDns = clientDot > 0 ? clientHost.substring(0, clientDot) : clientHost;
                        String clientId = clientName + "@" + clientDns;
                        clients.put(clientId, new InetSocketAddress(receivePacket.getAddress(), receivePacket.getPort()));

                        byte[] connectedData = ("SYS|Connected as " + clientId).getBytes(StandardCharsets.UTF_8);
                        socket.send(new DatagramPacket(connectedData, connectedData.length, receivePacket.getAddress(), receivePacket.getPort()));

                        byte[] joinedData = ("SYS|" + clientId + " joined chat").getBytes(StandardCharsets.UTF_8);
                        for (InetSocketAddress target : clients.values()) {
                            socket.send(new DatagramPacket(joinedData, joinedData.length, target.getAddress(), target.getPort()));
                        }
                        continue;
                    }

                    if ("MSG".equalsIgnoreCase(command) && parts.length >= 3) {
                        byte[] msgData = ("MSG|" + parts[1].trim() + "|" + parts[2]).getBytes(StandardCharsets.UTF_8);
                        for (InetSocketAddress target : clients.values()) {
                            socket.send(new DatagramPacket(msgData, msgData.length, target.getAddress(), target.getPort()));
                        }
                        continue;
                    }

                    if ("LIST".equalsIgnoreCase(command)) {
                        StringBuilder list = new StringBuilder(serverUser);
                        for (String client : clients.keySet()) {
                            list.append(", ").append(client);
                        }
                        byte[] listData = ("LIST|" + list).getBytes(StandardCharsets.UTF_8);
                        socket.send(new DatagramPacket(listData, listData.length, receivePacket.getAddress(), receivePacket.getPort()));
                        continue;
                    }

                    if ("LEAVE".equalsIgnoreCase(command) && parts.length >= 2) {
                        String leaving = parts[1].trim();
                        clients.remove(leaving);
                        byte[] leftData = ("SYS|" + leaving + " left chat").getBytes(StandardCharsets.UTF_8);
                        for (InetSocketAddress target : clients.values()) {
                            socket.send(new DatagramPacket(leftData, leftData.length, target.getAddress(), target.getPort()));
                        }
                        continue;
                    }

                    byte[] errorData = "SYS|Unknown command".getBytes(StandardCharsets.UTF_8);
                    socket.send(new DatagramPacket(errorData, errorData.length, receivePacket.getAddress(), receivePacket.getPort()));
                } catch (SocketTimeoutException ignored) {
                }
            }
        }
    }
}
