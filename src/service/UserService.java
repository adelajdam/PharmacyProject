package service;

import dao.UserDao;
import Model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public User login(String email, String password) throws SQLException {

        Optional<User> userOpt = userDao.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Email nuk ekziston");
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Password i gabuar");
        }

        return user;
    }


    public KlientLoguar registerKlient(
            String emri,
            String mbiemri,
            String email,
            String password,
            String adresa,
            String nrTel
    ) throws SQLException {

        if (emri == null || emri.isBlank())
            throw new IllegalArgumentException("Emri nuk mund të jetë bosh");

        if (mbiemri == null || mbiemri.isBlank())
            throw new IllegalArgumentException("Mbiemri nuk mund të jetë bosh");

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email nuk mund të jetë bosh");

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password nuk mund të jetë bosh");

        if (userDao.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Ky email është i zënë");

        KlientLoguar klient = new KlientLoguar();
        klient.setEmri(emri);
        klient.setMbiemri(mbiemri);
        klient.setEmail(email);
        klient.setPassword(password);
        klient.setAdresa(adresa);
        klient.setNrTel(nrTel);
        klient.setRole("KLIENT_LOGUAR");
        klient.setDataRegjistrimit(LocalDate.now());

        return (KlientLoguar) userDao.create(klient);
    }


    public KlientGuest registerGuest(String email) throws SQLException {

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email nuk mund të jetë bosh");

        KlientGuest guest = new KlientGuest();
        guest.setEmail(email);
        guest.setRole("KLIENT_GUEST");
        guest.setDataRegjistrimit(LocalDate.now());

        return (KlientGuest) userDao.create(guest);
    }


    public Farmacist createFarmacist(
            String emri,
            String mbiemri,
            String email,
            String password,
            User admin
    ) throws SQLException {

        if (!isAdmin(admin))
            throw new SecurityException("Vetëm administratori mund të krijojë farmacistë");

        if (userDao.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Ky email është i zënë");

        Farmacist f = new Farmacist();
        f.setEmri(emri);
        f.setMbiemri(mbiemri);
        f.setEmail(email);
        f.setPassword(password);
        f.setRole("FARMACIST");
        f.setDataRegjistrimit(LocalDate.now());

        return (Farmacist) userDao.create(f);
    }


    public Administrator createAdmin(
            String emri,
            String mbiemri,
            String email,
            String password,
            User admin
    ) throws SQLException {

        if (!isAdmin(admin))
            throw new SecurityException("Vetëm administratori mund të krijojë administratorë");

        if (userDao.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Ky email është i zënë");

        Administrator a = new Administrator();
        a.setEmri(emri);
        a.setMbiemri(mbiemri);
        a.setEmail(email);
        a.setPassword(password);
        a.setRole("ADMINISTRATOR");
        a.setDataRegjistrimit(LocalDate.now());

        return (Administrator) userDao.create(a);
    }


    public User updateKlientPersonalData(
            Long userId,
            String email,
            String adresa,
            String nrTel
    ) throws SQLException {

        Optional<User> userOpt = userDao.findById(userId);

        if (userOpt.isEmpty())
            throw new IllegalArgumentException("Përdoruesi nuk u gjet");

        User user = userOpt.get();

        if (!isKlient(user))
            throw new SecurityException("Vetëm klientët mund të ndryshojnë të dhënat personale");

        if (email != null && !email.isBlank())
            user.setEmail(email);

        if (adresa != null && !adresa.isBlank() && user instanceof Klient klient)
            klient.setAdresa(adresa);

        if (nrTel != null && !nrTel.isBlank() && user instanceof Klient klient)
            klient.setNrTel(nrTel);

        return userDao.update(user);
    }


    public List<User> getUsersByRole(String role) throws SQLException {
        return userDao.findByRole(role);
    }


    private boolean isAdmin(User user) {
        return "ADMINISTRATOR".equals(user.getRole());
    }

    private boolean isFarmacist(User user) {
        return "FARMACIST".equals(user.getRole());
    }

    private boolean isKlient(User user) {
        return "KLIENT_LOGUAR".equals(user.getRole()) || "KLIENT_GUEST".equals(user.getRole());
    }
}
