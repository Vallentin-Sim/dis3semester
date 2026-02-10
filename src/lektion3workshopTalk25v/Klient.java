package lektion3workshopTalk25v;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Klient {
    public static void main(String[] args) throws IOException {
        String sentence;
        String modifiedSentence;
        Socket clientSocket= new Socket("localhost",6789);
        try {
            while(true){
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("Welcome to the server\nPlease write input for server");
                sentence =  inFromUser.readLine();
                if (sentence.equals("exit")){
                    outToServer.writeBytes(sentence + "\n");
                    break;
                } else {
                    outToServer.writeBytes(sentence + "\n");
                    modifiedSentence = inFromServer.readLine();
                    System.out.println(modifiedSentence + "\n");
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            clientSocket.close();
        }
    }
}
