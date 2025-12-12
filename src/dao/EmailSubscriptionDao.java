package dao;

import Model.EmailSubscription;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmailSubscriptionDao {
    EmailSubscription create(EmailSubscription sub) throws SQLException;
    Optional<EmailSubscription> findById(Long id) throws SQLException;
    List<EmailSubscription> findByProduktId(Long produktId) throws SQLException;
    List<EmailSubscription> findAll() throws SQLException;
    void delete(Long id) throws SQLException;
}

