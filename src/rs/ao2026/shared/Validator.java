/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ao2026.shared;

/**
 *
 * @author Ognjen
 */
public class Validator {
    // Proveravanje da li JMBG ima tacno 13 cifara
    public static boolean isValidJMBG(String jmbg) {
        return jmbg != null && jmbg.matches("\\d{13}");
    }

    // Proveravanje osnovnog formata email adrese
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && email.matches(emailRegex);
    }
}
