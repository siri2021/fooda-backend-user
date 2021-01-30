
package be.fooda.backend.user.dao;

import be.fooda.backend.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByLoginAndIsActive(String login, boolean isActive);

    boolean existsByLoginAndIsActive(String login, boolean isActive);

    boolean existsByLogin(String login);

}