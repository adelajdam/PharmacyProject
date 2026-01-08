package service;

import dao.UserDao;
import Model.*;
import org.mindrot.jbcrypt.BCrypt;
import session.AppSession;

import java.sql.SQLException;
import java.time.LocalDate;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /* ================== SIGN UP ================== */
    // Vetëm klientë të pa-loguar

    public KlientLoguar signUpKlient(
            String emri,
            String mbiemri,
            String email,
            String password,
            String adresa,
            String nrTel
    ) throws SQLException {

        if (userDao.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Ky email është i zënë");

        KlientLoguar klient = new KlientLoguar();
        klient.setEmri(emri);
        klient.setMbiemri(mbiemri);
        klient.setEmail(email);
        klient.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        klient.setAdresa(adresa);
        klient.setNrTel(nrTel);
        klient.setRole("KLIENT_LOGUAR");
        klient.setDataRegjistrimit(LocalDate.now());

        return (KlientLoguar) userDao.create(klient);
    }

    /* ================== LOGIN KLIENT ================== */

    public KlientLoguar loginKlient(String email, String password) throws SQLException {

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email nuk ekziston"));

        if (!"KLIENT_LOGUAR".equals(user.getRole()))
            throw new SecurityException("Ky login është vetëm për klientë");

        if (!BCrypt.checkpw(password, user.getPassword()))
            throw new IllegalArgumentException("Password i gabuar");

        return (KlientLoguar) user;
    }

    /* ================== DASHBOARD LOGIN ================== */
    // Vetëm admin & farmacist

    public User loginDashboard(String email, String password) throws SQLException {

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email nuk ekziston"));

        if (!BCrypt.checkpw(password, user.getPassword()))
            throw new IllegalArgumentException("Password i gabuar");

        if (!user.getRole().equals("ADMINISTRATOR") &&
                !user.getRole().equals("FARMACIST"))
            throw new SecurityException("Nuk keni akses në dashboard");

        return user;
    }

    /* ================== LOGOUT ================== */

    public void logout() {
        AppSession.logout();
    }

}
