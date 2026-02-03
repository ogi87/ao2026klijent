/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ao2026.shared;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Ognjen
 */
public class Prijava implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String jmbg, email, ime, prezime;
    private LocalDate datumVolontiranja;
    private String smena; // jutarnja, popodnevna, vecernja
    private String pozicija;
    private LocalDateTime datumKreiranja;

    public Prijava(String jmbg, String email, String ime, String prezime, LocalDate datumVolontiranja, String smena, String pozicija) {
        this.id = jmbg + "_" + datumVolontiranja.toString();
        this.jmbg = jmbg; this.email = email;
        this.ime = ime; this.prezime = prezime;
        this.datumVolontiranja = datumVolontiranja;
        this.smena = smena; this.pozicija = pozicija;
        this.datumKreiranja = LocalDateTime.now();
    }

    // Getteri i Setteri
    public String getJmbg() { return jmbg; }

    public String getIme() {
        return ime;
    }

    public String getEmail() {
        return email;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public LocalDateTime getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(LocalDateTime datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
    
    
    
    public LocalDate getDatumVolontiranja() { return datumVolontiranja; }
    public String getSmena() { return smena; }
    public String getPozicija() { return pozicija; }
    public void setSmena(String smena) { this.smena = smena; }
    public void setPozicija(String pozicija) { this.pozicija = pozicija; }
    public void setDatumVolontiranja(LocalDate datum) { this.datumVolontiranja = datum; }

    public String getStatus() {
        if (datumVolontiranja.isBefore(LocalDate.now())) return "ZAVRŠENA";
        if (datumVolontiranja.atStartOfDay().minusHours(24).isBefore(LocalDateTime.now())) return "ZAKLJUČANA";
        return "U OBRADI";
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Datum: %s | Smena: %s | Pozicija: %s | Status: %s", 
                id, datumVolontiranja, smena, pozicija, getStatus());
    }
}
