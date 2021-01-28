package be.fooda.backend.user.model.create;

import be.fooda.backend.user.model.FoodaRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
public class FoodaUserCreate {

    private String login;

    private Boolean isActive = Boolean.TRUE;

    private Boolean isAuthenticated = Boolean.FALSE;

    private LocalDateTime registry;

    private LocalDateTime lastUpdated;

    private Set<FoodaRole> roles;

}
