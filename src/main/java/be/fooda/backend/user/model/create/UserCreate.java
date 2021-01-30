package be.fooda.backend.user.model.create;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
public class UserCreate {

    @EqualsAndHashCode.Include
    private String login;

    private Boolean isActive = Boolean.TRUE;

    private Boolean isAuthenticated = Boolean.FALSE;

    private LocalDateTime registry;

    private LocalDateTime lastUpdated;

    private Set<RoleCreate> roles;

}
