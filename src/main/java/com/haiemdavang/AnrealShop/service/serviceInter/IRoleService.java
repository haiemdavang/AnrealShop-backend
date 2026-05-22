package com.haiemdavang.AnrealShop.service.serviceInter;

import com.haiemdavang.AnrealShop.modal.entity.user.Role;
import com.haiemdavang.AnrealShop.modal.enums.RoleName;

public interface IRoleService {
    Role getRoleByName(RoleName roleName);
}
