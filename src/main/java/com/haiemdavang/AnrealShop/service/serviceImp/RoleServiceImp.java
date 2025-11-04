package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.modal.entity.user.Role;
import com.haiemdavang.AnrealShop.modal.enums.RoleName;
import com.haiemdavang.AnrealShop.repository.user.RoleRepository;
import com.haiemdavang.AnrealShop.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements IRoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new AnrealShopException("ROLE_NOT_FOUND"));
    }
}
