package lektion3workshopTalk25v.GustavLoesning;

import java.net.*;
import java.util.ArrayList;

class UDPServer {

	// Liste over aktive klienter
	static ArrayList<InetSocketAddress> clients = new ArrayList<>();
	public static void main(String[] args) throws Exception {

		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[1024];

		System.out.println("UDP Chat Server started...");

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			serverSocket.receive(receivePacket);

			String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
			InetAddress address = receivePacket.getAddress();
			int port = receivePacket.getPort();

			InetSocketAddress client = new InetSocketAddress(address, port);

			// Hvis ny klient → tilføj til listen
			if (!clients.contains(client)) {
				clients.add(client);
				System.out.println("New client joined.");
			}

			// Hvis quit → fjern klient
			if (message.equalsIgnoreCase("/quit")) {
				clients.remove(client);
				System.out.println("Client left.");
				continue;
			}
			System.out.println("Message: " + message);

			// Send besked til alle klienter
			for (InetSocketAddress c : clients) {
				byte[] sendData = message.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						c.getAddress(), c.getPort());
				serverSocket.send(sendPacket);
			}
		}
	}
}