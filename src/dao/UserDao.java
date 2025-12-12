package dao;

import Model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user) throws SQLException;
    Optional<User> findById(Long id) throws SQLException;
    List<User> findByRole(String role) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    List<User> findAll() throws SQLException;
    User update(User user) throws SQLException;
    void delete(Long id) throws SQLException;

}
