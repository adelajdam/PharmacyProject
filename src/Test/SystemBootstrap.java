package Test;

import Model.User;
import dao.UserDao;
import Model.Administrator;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

public class SystemBootstrap {

    public static void createInitialAdmin(UserDao userDao) throws SQLException {

        List<User> admins = userDao.findByRole("ADMINISTRATOR");

        if (admins.isEmpty()) {

            Administrator admin = new Administrator();
            admin.setEmri("Super");
            admin.setMbiemri("Admin");
            admin.setEmail("admin@system.com");
            admin.setPassword(
                    BCrypt.hashpw("admin123", BCrypt.gensalt())
            );
            admin.setRole("ADMINISTRATOR");

            userDao.create(admin);

            System.out.println("ADMINISTRATORI I PARË U KRIJUA");
        } else {
            System.out.println("ℹ ADMINISTRATORI EKZISTON (" + admins.size() + ")");
        }
    }
}
