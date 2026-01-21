/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ao2026.shared;

import java.io.Serializable;

/**
 *
 * @author Ognjen
 */
public class Korisnik implements Serializable {
    private String username, password, ime, prezime, jmbg, email;

    public Korisnik(String username, String password, String ime, String prezime, String jmbg, String email) {
        this.username = username; this.password = password;
        this.ime = ime; this.prezime = prezime;
        this.jmbg = jmbg; this.email = email;
    }
    // Getteri
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getIme() { return ime; }
    public String getPrezime() { return prezime; }
    public String getJmbg() { return jmbg; }
    public String getEmail() { return email; }
}
