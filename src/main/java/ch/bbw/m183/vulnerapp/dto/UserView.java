package ch.bbw.m183.vulnerapp.dto;

import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.enums.Role;

public record UserView(String username, String fullname, Role role) {
    public static UserView from(UserEntity userEntity) {
        return new UserView(userEntity.getUsername(), userEntity.getFullname(), userEntity.getRole());
    }
}
