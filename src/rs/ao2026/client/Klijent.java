/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package rs.ao2026.client;

/**
 *
 * @author Ognjen
 */
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;
import rs.ao2026.shared.Korisnik;
import rs.ao2026.shared.Prijava;
import rs.ao2026.shared.Validator;

public class Klijent {
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static Scanner sc = new Scanner(System.in);
    private static Korisnik ulogovani = null;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234)) {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println((String) in.readObject());

            while (true) {
                prikaziMeni();
                int opcija = Integer.parseInt(sc.nextLine());
                switch (opcija) {
                    case 1: registracija(); break;
                    case 2: login(); break;
                    case 3: novaPrijava(); break;
                    case 4: pregledPrijava(); break;
                    case 5: izmenaPrijave(); break;
                    case 0: out.writeObject("EXIT"); return;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void prikaziMeni() {
        System.out.println("\n--- AUSTRALIAN OPEN 2026 VOLONTIRANJE ---");
        if (ulogovani == null) {
            System.out.println("1. Registracija\n2. Login");
        }
        System.out.println("3. Nova prijava\n4. Moje prijave\n5. Izmena prijave\n0. Izlaz");
        System.out.print("Izbor: ");
    }

    private static void registracija() throws Exception {
        out.writeObject("REGISTRACIJA");
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        System.out.print("Ime: "); String i = sc.nextLine();
        System.out.print("Prezime: "); String pr = sc.nextLine();
        
        String jmbg;
        while (true) {
            System.out.print("JMBG (13 cifara): ");
            jmbg = sc.nextLine();
            if (Validator.isValidJMBG(jmbg)) {
                break;
            }
            System.out.println("GRESKA: JMBG mora imati tacno 13 cifara!");
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine();
            if (Validator.isValidEmail(email)) {
                break;
            }
            System.out.println("GRESKA: Neispravan format email adrese (npr. ime@primer.com)!");
        }

        out.writeObject(new Korisnik(u, p, i, pr, jmbg, email));
        System.out.println((String) in.readObject());
    }

    private static void login() throws Exception {
        out.writeObject("LOGIN");
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        out.writeObject(u); out.writeObject(p);
        out.flush();

        String res = (String) in.readObject();
        if (res.equals("OK")) {
            ulogovani = (Korisnik) in.readObject();
            System.out.println("Dobrodošli, " + ulogovani.getIme() + "!");
        } else {
            System.out.println(res);
        }
    }

    private static void novaPrijava() throws Exception {
    String ime, prez, jmbg, email;

    if (ulogovani != null) {
        ime = ulogovani.getIme();
        prez = ulogovani.getPrezime();
        jmbg = ulogovani.getJmbg();
        email = ulogovani.getEmail();
    } else {
        System.out.print("Ime: "); ime = sc.nextLine();
        System.out.print("Prezime: "); prez = sc.nextLine();

        // Validacija JMBG-a (mora biti 13 cifara)
        while (true) {
            System.out.print("JMBG (13 cifara): ");
            jmbg = sc.nextLine();
            if (jmbg.matches("\\d{13}")) {
                break;
            }
            System.out.println("GRESKA: JMBG mora sadrzati tacno 13 cifara!");
        }

        // Validacija Email-a
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine();
            if (email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                break;
            }
            System.out.println("GRESKA: Neispravan format email adrese!");
        }
    }

        // 2. Unos i validacija datuma
        LocalDate datum = null;
        while (datum == null) {
            System.out.print("Datum volontiranja (YYYY-MM-DD): ");
            try {
                datum = LocalDate.parse(sc.nextLine());
                if (datum.isBefore(LocalDate.now().plusDays(1)) || datum.isAfter(LocalDate.of(2026, 2, 5))) {
                    System.out.println("GRESKA: Datum mora biti od sutra do 05.02.2026!");
                    datum = null; // Resetujemo da bi petlja trazila ponovo
                }
            } catch (Exception e) {
                System.out.println("GRESKA: Neispravan format datuma! Koristite YYYY-MM-DD.");
            }
        }

        // 3. Unos smene i pozicije
        System.out.print("Smena (jutarnja/popodnevna/vecernja): "); 
        String smena = sc.nextLine();
        System.out.print("Pozicija (informacije/redar/mediji/vip): "); 
        String poz = sc.nextLine();

        // 4. Slanje komande i objekta serveru
        out.writeObject("NOVA_PRIJAVA"); 
        Prijava nova = new Prijava(jmbg, email, ime, prez, datum, smena, poz);
        out.writeObject(nova);
        out.flush(); 

        
        Object odgovor = in.readObject();
        System.out.println("SERVER: " + odgovor.toString());
}

        private static void pregledPrijava() throws Exception {
            out.writeObject("PREGLED");

            String jmbgZaSlanje;
            String emailZaSlanje;

            // Ako je korisnik ulogovan, automatski uzimamo podatke sa njegovog profila
            if (ulogovani != null) {
                jmbgZaSlanje = ulogovani.getJmbg();
                emailZaSlanje = ulogovani.getEmail();
                System.out.println("\n--- PREGLED VAŠIH PRIJAVA (Ulogovani ste kao: " + ulogovani.getUsername() + ") ---");
            } else {
                // Ako nije ulogovan (osnovni zahtev), onda mora da unese rucno
                System.out.println("\n--- PREGLED PRIJAVA ---");
                System.out.print("Unesite Vaš JMBG: ");
                jmbgZaSlanje = sc.nextLine();
                System.out.print("Unesite Vaš E-mail: ");
                emailZaSlanje = sc.nextLine();
            }

            // saljemo parametre serveru
            out.writeObject(jmbgZaSlanje);
            out.writeObject(emailZaSlanje);
            out.flush();

            List<Prijava> lista = (List<Prijava>) in.readObject();

            if (lista.isEmpty()) {
                System.out.println("Nema pronađenih prijava.");
            } else {
                System.out.println("\nLista prijava:");
                System.out.println("--------------------------------------------------------------------------------");
                for (int i = 0; i < lista.size(); i++) {
                    System.out.println(i + ". " + lista.get(i));
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
        }

        private static void izmenaPrijave() throws Exception {
        String jmbg, email;

        
        if (ulogovani != null) {
            jmbg = ulogovani.getJmbg();
            email = ulogovani.getEmail();
            System.out.println("\n--- IZMENA PRIJAVA (Ulogovani ste) ---");
        } else {
            System.out.println("\n--- IZMENA PRIJAVA ---");
            System.out.print("Unesite JMBG: ");
            jmbg = sc.nextLine();
            System.out.print("Unesite Email: ");
            email = sc.nextLine();
        }

        
        out.writeObject("PREGLED");
        out.writeObject(jmbg);
        out.writeObject(email);
        out.flush();

        List<Prijava> lista = (List<Prijava>) in.readObject();

        if (lista.isEmpty()) {
            System.out.println("Nema pronađenih prijava za date podatke.");
            return;
        }

        
        for (int i = 0; i < lista.size(); i++) {
            System.out.println(i + ". " + lista.get(i));
        }

        System.out.print("Redni broj prijave za izmenu: ");
        int index = Integer.parseInt(sc.nextLine());

        if (index < 0 || index >= lista.size()) {
            System.out.println("Neispravan indeks!");
            return;
        }

        Prijava stara = lista.get(index);

     
        LocalDate noviDatum = null;
        while (noviDatum == null) {
            System.out.print("Novi datum (YYYY-MM-DD) [Trenutno: " + stara.getDatumVolontiranja() + "]: ");
            try {
                noviDatum = LocalDate.parse(sc.nextLine());
                if (noviDatum.isBefore(LocalDate.now().plusDays(1)) || noviDatum.isAfter(LocalDate.of(2026, 2, 5))) {
                    System.out.println("GRESKA: Datum mora biti od sutra do 05.02.2026!");
                    noviDatum = null;
                }
            } catch (Exception e) {
                System.out.println("GRESKA: Neispravan format datuma!");
            }
        }

        System.out.print("Nova smena [Trenutno: " + stara.getSmena() + "]: "); 
        String novaSmena = sc.nextLine();
        System.out.print("Nova pozicija [Trenutno: " + stara.getPozicija() + "]: "); 
        String novaPozicija = sc.nextLine();

        
        Prijava nova = new Prijava(
            stara.getJmbg(), 
            stara.getEmail(), 
            stara.getIme(), 
            stara.getPrezime(), 
            noviDatum, 
            novaSmena, 
            novaPozicija
        );

        
        out.writeObject("IZMENA");
        out.writeObject(index);
        out.writeObject(nova);
        out.writeObject(jmbg); 
        out.flush();

        
        String odgovor = (String) in.readObject();
        System.out.println("SERVER: " + odgovor);
    }
}
