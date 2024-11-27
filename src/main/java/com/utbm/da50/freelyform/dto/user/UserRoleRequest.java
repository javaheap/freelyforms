package com.utbm.da50.freelyform.dto.user;

import com.utbm.da50.freelyform.enums.UserRole;
import lombok.Data;

import java.util.HashSet;

@Data
public class UserRoleRequest {

    private HashSet<UserRole> roles;

}
