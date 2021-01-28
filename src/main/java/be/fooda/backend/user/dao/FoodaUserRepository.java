
package be.fooda.backend.user.dao;

import be.fooda.backend.user.model.entity.FoodaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodaUserRepository extends JpaRepository<FoodaUser, Long> {

    Optional<FoodaUser> findByLoginAndIsActive(String login, boolean isActive);

    boolean existsByLoginAndIsActive(String login, boolean isActive);

    boolean existsByLogin(String login);

}