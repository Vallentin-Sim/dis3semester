package lektion3workshopTalk25v.GustavLoesning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

class UDPClient {
	public static void main(String[] args) throws Exception {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		clientSocket.setSoTimeout(50);
		InetAddress IPAddress = InetAddress.getByName("10.10.131.85");

		byte[] receiveData = new byte[1024];

		System.out.println("UDP Client started. Type /quit to exit.");

		while (true) {
			String sentence = inFromUser.readLine();
			if (sentence == null) {
				break;
			}
			byte[] sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			clientSocket.send(sendPacket);

			if ("/quit".equalsIgnoreCase(sentence)) {
				break;
			}
			// Blokerer indtil server svarer
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				clientSocket.receive(receivePacket);
				String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
				System.out.println("FROM CLIENT: " + modifiedSentence);
			} catch (SocketTimeoutException e) {
				continue;
			}
		}
		clientSocket.close();
		System.out.println("Client closed.");
	}
}