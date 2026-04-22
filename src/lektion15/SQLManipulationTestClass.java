package lektion15;

import java.sql.*;
import java.util.Scanner;

/*
Brugervejledning til test af samtidigheds-fejl og isolation levels

1. Start programmet i to separate konsolvinduer.
2. I begge programmer vælges menuvalg 2 for at overføre penge.
3. Brug samme fra-konto i begge programmer og indtast et beloeb.
4. Begge programmer stopper ved beskeden:
   "Begge saldi er læst. Tryk Enter for at fortsætte til opdateringerne."

Sådan fremprovokeres fejlen:
- Sæt isolation level til "read committed" i begge programmer.
- Tryk Enter i det ene program og derefter i det andet.
- Resultatet kan blive forkert, fordi begge programmer når at læse den gamle saldo,
  før de opdaterer kontoen.

Ekstra test:
- "read uncommitted" er endnu svagere og kan også give forkerte resultater.

Sådan fixes problemet:
- Sæt isolation level til "serializable".
- Kør samme test igen med to programmer.
- Nu bliver transaktionerne låst korrekt, så resultatet bliver rigtigt.

Observation:
- "repeatable read" giver bedre beskyttelse end "read committed",
  men det er typisk "serializable" der helt læser problemet i denne opgave.
*/

public class SQLManipulationTestClass {
    private static final String ISOLATION_READ_UNCOMMITTED = "read uncommitted";
    private static final String ISOLATION_READ_COMMITTED = "read committed";
    private static final String ISOLATION_REPEATABLE_READ = "repeatable read";
    private static final String ISOLATION_SERIALIZABLE = "serializable";

    public static void main(String[] args) {
        String url = "jdbc:sqlserver://VALLENTINHOE\\SQLExpress;databaseName=transactiondb;user=sa;password=admin123;";
        Scanner scanner = new Scanner(System.in);
        Connection con = null;
        Statement stmt = null;
        String isolationLevel = ISOLATION_READ_COMMITTED;

        try {
            con = DriverManager.getConnection(url);
            stmt = con.createStatement();
            boolean koerer = true;

            while (koerer) {
                visAktueltIsolationLevel(isolationLevel);
                visMenu();
                String valg = scanner.nextLine();

                switch (valg) {
                    case "1":
                        visAlleKonti(con);
                        break;
                    case "2":
                        overfoerBeloeb(con, stmt, scanner, isolationLevel);
                        break;
                    case "3":
                        visSpecifikKonto(con, scanner);
                        break;
                    case "4":
                        isolationLevel = vaelgIsolationLevel(scanner);
                        System.out.println("Isolation level er nu sat til: " + isolationLevel);
                        break;
                    case "5":
                        koerer = false;
                        System.out.println("Programmet afsluttes.");
                        break;
                    default:
                        System.out.println("Ugyldigt valg. Proev igen.");
                }
            }
        } catch (SQLException e) {
            rollbackQuietly(stmt);
            System.out.println("Databasefejl: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            rollbackQuietly(stmt);
            System.out.println("Beloeb skal vaere et gyldigt tal.");
        } finally {
            closeQuietly(stmt);
            closeQuietly(con);
            scanner.close();
        }
    }

    private static void visMenu() {
        System.out.println();
        System.out.println("1. Vis alle konti");
        System.out.println("2. Overfoer beloeb mellem to konti");
        System.out.println("3. Vis en specifik konto");
        System.out.println("4. Vaelg isolation level");
        System.out.println("5. Afslut");
        System.out.print("Vaelg operation: ");
    }

    private static String vaelgIsolationLevel(Scanner scanner) {
        System.out.println("1. Read Uncommitted");
        System.out.println("2. Read Committed");
        System.out.println("3. Repeatable Read");
        System.out.println("4. Serializable");
        System.out.print("Vaelg isolation level: ");

        String valg = scanner.nextLine();
        if ("1".equals(valg)) {
            return ISOLATION_READ_UNCOMMITTED;
        }
        if ("3".equals(valg)) {
            return ISOLATION_REPEATABLE_READ;
        }
        if ("4".equals(valg)) {
            return ISOLATION_SERIALIZABLE;
        }

        return ISOLATION_READ_COMMITTED;
    }

    private static void visAktueltIsolationLevel(String isolationLevel) {
        System.out.println("Aktuelt isolation level: " + isolationLevel);
    }

    private static void visAlleKonti(Connection con) throws SQLException {
        String sql = "SELECT regNr, ktoNr, tekst, saldo, renteIndlån, renteUdlån FROM Konto";

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("regNr") + " "
                                + rs.getString("ktoNr") + " "
                                + rs.getString("tekst") + " "
                                + rs.getDouble("saldo") + " "
                                + rs.getDouble("renteIndlån") + " "
                                + rs.getDouble("renteUdlån"));
            }
        }
    }

    private static void visSpecifikKonto(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Indtast registreringsnummer: ");
        String regNr = scanner.nextLine();

        System.out.print("Indtast kontonummer: ");
        String ktoNr = scanner.nextLine();

        String sql = "SELECT regNr, ktoNr, tekst, saldo, renteIndlån, renteUdlån FROM Konto WHERE regNr = ? AND ktoNr = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, regNr);
            pstmt.setString(2, ktoNr);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Konto fundet:");
                    System.out.println(
                            rs.getString("regNr") + " "
                                    + rs.getString("ktoNr") + " "
                                    + rs.getString("tekst") + " "
                                    + rs.getDouble("saldo") + " "
                                    + rs.getDouble("renteIndlån") + " "
                                    + rs.getDouble("renteUdlån"));
                } else {
                    System.out.println("Kontoen findes ikke.");
                }
            }
        }
    }

    private static void overfoerBeloeb(Connection con, Statement stmt, Scanner scanner, String isolationLevel) throws SQLException {
        System.out.print("Indtast registreringsnummer for fra-konto: ");
        String fraRegNr = scanner.nextLine();

        System.out.print("Indtast kontonummer for fra-konto: ");
        String fraKtoNr = scanner.nextLine();

        System.out.print("Indtast registreringsnummer for til-konto: ");
        String tilRegNr = scanner.nextLine();

        System.out.print("Indtast kontonummer for til-konto: ");
        String tilKtoNr = scanner.nextLine();

        System.out.print("Indtast beloeb der skal overfoeres: ");
        double beloeb = Double.parseDouble(scanner.nextLine());

        stmt.execute("set transaction isolation level " + isolationLevel);
        stmt.execute("begin tran");

        if (beloeb <= 0) {
            System.out.println("Beloeb skal vaere stoerre end 0.");
            rollbackQuietly(stmt);
            return;
        }

        double fraSaldo = hentSaldo(con, fraRegNr, fraKtoNr);
        if (fraSaldo < 0) {
            System.out.println("Fra-konto findes ikke.");
            rollbackQuietly(stmt);
            return;
        }

        if (fraSaldo < beloeb) {
            System.out.println("Der er ikke penge nok paa fra-kontoen.");
            rollbackQuietly(stmt);
            return;
        }

        double tilSaldo = hentSaldo(con, tilRegNr, tilKtoNr);
        if (tilSaldo < 0) {
            System.out.println("Til-konto findes ikke.");
            rollbackQuietly(stmt);
            return;
        }

        double nyFraSaldo = fraSaldo - beloeb;
        double nyTilSaldo = tilSaldo + beloeb;

        try {
            System.out.println("Begge saldi er laest. Tryk Enter for at fortsaette til opdateringerne.");
            scanner.nextLine();

            opdaterSaldo(con, fraRegNr, fraKtoNr, nyFraSaldo);
            opdaterSaldo(con, tilRegNr, tilKtoNr, nyTilSaldo);
            stmt.execute("commit tran");

            System.out.println("Overfoersel gennemfoert.");
            System.out.println("Ny saldo paa fra-konto: " + nyFraSaldo);
            System.out.println("Ny saldo paa til-konto: " + nyTilSaldo);
        } catch (SQLException e) {
            rollbackQuietly(stmt);
            throw e;
        }
    }

    private static double hentSaldo(Connection con, String regNr, String ktoNr) throws SQLException {
        String sql = "SELECT saldo FROM Konto WHERE regNr = ? AND ktoNr = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, regNr);
            pstmt.setString(2, ktoNr);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        }

        return -1;
    }

    private static void opdaterSaldo(Connection con, String regNr, String ktoNr, double nySaldo) throws SQLException {
        String sql = "UPDATE Konto SET saldo = ? WHERE regNr = ? AND ktoNr = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDouble(1, nySaldo);
            pstmt.setString(2, regNr);
            pstmt.setString(3, ktoNr);
            pstmt.executeUpdate();
        }
    }

    private static void rollbackQuietly(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.execute("rollback tran");
            } catch (SQLException ignored) {
            }
        }
    }

    private static void closeQuietly(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    private static void closeQuietly(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
