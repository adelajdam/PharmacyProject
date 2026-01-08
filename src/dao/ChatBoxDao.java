package dao;

import Model.Mesazh;

import java.sql.SQLException;
import java.util.List;

public interface ChatBoxDao {

    Mesazh create(Mesazh mesazh) throws SQLException;

    List<Mesazh> findByUsers(Long user1Id, Long user2Id) throws SQLException;

    List<Mesazh> findAllForUser(Long userId) throws SQLException;
}

