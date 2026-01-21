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
        while(true) {
            System.out.print("JMBG (13 cifara): "); jmbg = sc.nextLine();
            if(jmbg.matches("\\d{13}")) break;
            System.out.println("Neispravan JMBG!");
        }

        String email;
        while(true) {
            System.out.print("Email: "); email = sc.nextLine();
            if(email.contains("@")) break;
            System.out.println("Neispravan email!");
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
            ulogovani = (Korisnik) in.readObject(); // Primi ceo objekat sa servera
            System.out.println("Dobrodošli, " + ulogovani.getIme() + "!");
        } else {
            System.out.println(res);
        }
    }

    private static void novaPrijava() throws Exception {
    out.writeObject("NOVA_PRIJAVA");
    
    String ime, prez, jmbg, email;
    
    // Ako je korisnik ulogovan, koristimo NJEGOVE podatke, a ne fiksne stringove
    if (ulogovani != null) {
        ime = ulogovani.getIme();
        prez = ulogovani.getPrezime();
        jmbg = ulogovani.getJmbg();
        email = ulogovani.getEmail();
    } else {
        System.out.print("Ime: "); ime = sc.nextLine();
        System.out.print("Prezime: "); prez = sc.nextLine();
        System.out.print("JMBG: "); jmbg = sc.nextLine();
        System.out.print("Email: "); email = sc.nextLine();
    }

    System.out.print("Datum (YYYY-MM-DD): ");
    LocalDate datum = LocalDate.parse(sc.nextLine());
    
    // Provera datuma
    if (datum.isBefore(LocalDate.now().plusDays(1)) || datum.isAfter(LocalDate.of(2026, 2, 1))) {
        System.out.println("Datum mora biti od sutra do 01.02.2026!");
        // Moramo poslati nesto serveru da ne bi on ostao da ceka objekat
        // Ili jednostavno rukovati ovim pre slanja komande
        return; 
    }

    System.out.print("Smena (jutarnja/popodnevna/vecernja): "); String smena = sc.nextLine();
    System.out.print("Pozicija (informacije/redar/mediji/vip): "); String poz = sc.nextLine();

    Prijava nova = new Prijava(jmbg, email, ime, prez, datum, smena, poz);
    out.writeObject(nova);
    out.flush(); // DODATO: Osigurava da podaci odu odmah

    Object odgovor = in.readObject();
    System.out.println(odgovor.toString());
}

    private static void pregledPrijava() throws Exception {
        out.writeObject("PREGLED");
        System.out.print("Unesite JMBG za proveru: ");
        String jmbg = sc.nextLine();
        out.writeObject(jmbg);
        
        List<Prijava> lista = (List<Prijava>) in.readObject();
        if (lista.isEmpty()) System.out.println("Nema prijava.");
        else lista.forEach(System.out::println);
    }

    private static void izmenaPrijave() throws Exception {
        out.writeObject("PREGLED");
        System.out.print("Unesite JMBG: ");
        String jmbg = sc.nextLine();
        out.writeObject(jmbg);
        List<Prijava> lista = (List<Prijava>) in.readObject();

        for (int i = 0; i < lista.size(); i++) System.out.println(i + ". " + lista.get(i));
        
        System.out.print("Redni broj prijave za izmenu: ");
        int index = Integer.parseInt(sc.nextLine());
        
        out.writeObject("IZMENA");
        out.writeObject(index);
        
        System.out.print("Novi datum (YYYY-MM-DD): ");
        LocalDate d = LocalDate.parse(sc.nextLine());
        System.out.print("Nova smena: "); String s = sc.nextLine();
        System.out.print("Nova pozicija: "); String p = sc.nextLine();
        
        Prijava stara = lista.get(index);
        Prijava nova = new Prijava(stara.getJmbg(), stara.getEmail(), "Ime", "Prez", d, s, p);
        
        out.writeObject(nova);
        out.writeObject(jmbg);
        System.out.println((String) in.readObject());
    }
}
