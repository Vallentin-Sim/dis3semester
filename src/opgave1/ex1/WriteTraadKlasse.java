package opgave1.ex1;

import java.util.Scanner;

public class WriteTraadKlasse extends Thread{
    private Stringklasse stringklasse;
    private static final String EXIT = "EXIT";

    public WriteTraadKlasse(Stringklasse stringklasse) {
        this.stringklasse = stringklasse;
    }

    @Override
    public void run() {
        System.out.println("Type EXIT to stop.");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase(EXIT)) {
                stringklasse.setString(EXIT);
                System.out.println("Write thread stopped.");
                break;
            }
            stringklasse.setString(input);
        }
        scanner.close();
    }
}
