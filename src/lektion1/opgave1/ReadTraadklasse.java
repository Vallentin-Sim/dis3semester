package lektion1.opgave1;

import java.util.Objects;

public class ReadTraadklasse extends Thread{
    private Stringklasse stringklasse;

    public ReadTraadklasse(Stringklasse stringklasse) {
        this.stringklasse = stringklasse;
    }

    @Override
    public void run() {
        try {
            boolean running = true;
            while (running) {
                System.out.println("Current value: " + stringklasse.getString());
                Thread.sleep(3000);
                if (Objects.equals(stringklasse.getString(), "EXIT")){
                    running = false;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Read thread stopped.");
        }
    }
}
