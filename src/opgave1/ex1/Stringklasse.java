package opgave1.ex1;

public class Stringklasse {
    private String string = "";

    public synchronized String getString() {
        return string;
    }

    public synchronized void setString(String string) {
        this.string = string;
    }
}
