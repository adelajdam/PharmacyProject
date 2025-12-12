package service;

public class EmailService {

    public void dergoEmail(String email, String subject, String body) {
        System.out.println("----- EMAIL I DERGUAR -----");
        System.out.println("Drejt: " + email);
        System.out.println("Subjekti: " + subject);
        System.out.println("Mesazhi:");
        System.out.println(body);
        System.out.println("----------------------------");
    }
}

