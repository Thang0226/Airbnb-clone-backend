package com.codegym.service.role;

import com.codegym.model.auth.Role;
import com.codegym.service.IGenerateService;
import org.springframework.stereotype.Repository;

public interface IRoleService extends IGenerateService<Role> {
    Role findByName(String name);
}
