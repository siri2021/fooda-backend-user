package be.fooda.backend.user.model.update;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
public class UserUpdate {

    @EqualsAndHashCode.Include
    private String login;

    @JsonIgnore
    private String password;

    private Boolean isActive;

    private Set<RoleUpdate> roles;
}
