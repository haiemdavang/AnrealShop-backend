package com.haiemdavang.AnrealShop.repository.wallet;

import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.entity.wallet.Wallet;
import com.haiemdavang.AnrealShop.modal.enums.WalletOwnerType;
import com.haiemdavang.AnrealShop.modal.enums.WalletStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WalletSpecification {

    public static Specification<Wallet> filter(String searchUser, WalletStatus walletStatus) {
        return (Root<Wallet> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Chỉ lấy ví của người dùng
            predicates.add(cb.equal(root.get("ownerType"), WalletOwnerType.NGUOI_DUNG));

            // Lọc theo trạng thái ví
            if (walletStatus != null) {
                predicates.add(cb.equal(root.get("status"), walletStatus));
            }

            // Tìm kiếm theo thông tin user (email, fullName, phone)
            if (StringUtils.hasText(searchUser)) {
                String searchPattern = "%" + searchUser.toLowerCase() + "%";

                // Subquery để tìm user theo email/fullName/phone
                Subquery<String> userSubquery = query.subquery(String.class);
                Root<User> userRoot = userSubquery.from(User.class);
                userSubquery.select(userRoot.get("id"))
                        .where(cb.or(
                                cb.like(cb.lower(userRoot.get("email")), searchPattern),
                                cb.like(cb.lower(userRoot.get("fullName")), searchPattern),
                                cb.like(cb.lower(userRoot.get("phoneNumber")), searchPattern)
                        ));

                predicates.add(root.get("ownerId").in(userSubquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
