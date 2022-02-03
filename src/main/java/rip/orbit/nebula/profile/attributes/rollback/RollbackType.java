package rip.orbit.nebula.profile.attributes.rollback;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RollbackType {

    GRANT("grants"),
    PUNISHMENT("punishments");

    @Getter private String readable;
}
