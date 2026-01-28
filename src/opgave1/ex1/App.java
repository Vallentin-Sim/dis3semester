package opgave1.ex1;

public class App {
    public static void main(String[] args) {
        Stringklasse stringklasse = new Stringklasse();

        ReadTraadklasse readTraadklasse = new ReadTraadklasse(stringklasse);
        WriteTraadKlasse writeTraadKlasse = new WriteTraadKlasse(stringklasse);

        readTraadklasse.start();
        writeTraadKlasse.start();
    }
}
