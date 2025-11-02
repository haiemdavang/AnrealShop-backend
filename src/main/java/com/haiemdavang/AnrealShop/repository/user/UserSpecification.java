package com.haiemdavang.AnrealShop.repository.user;

import com.haiemdavang.AnrealShop.dto.user.AccountType;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.RoleName;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> filter(String search, LocalDateTime confirmSDTime, LocalDateTime confirmEDTime, AccountType accountType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            assert query != null;
            query.distinct(true);
            if (query.getResultType() != Long.class) {
                root.fetch("role");
            }

            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate emailPredicate = cb.like(cb.lower(root.get("email")), searchPattern);
                Predicate namePredicate = cb.like(cb.lower(root.get("fullName")), searchPattern);
                predicates.add(cb.or(emailPredicate, namePredicate));
            }
            if (confirmSDTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), confirmSDTime));
            }
            if (confirmEDTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), confirmEDTime));
            }
            if (accountType != null && !accountType.equals(AccountType.ALL)) {
                if (accountType.equals(AccountType.USER)) {
                    predicates.add(cb.equal(root.get("role").get("name"), RoleName.USER));
                } else if (accountType.equals(AccountType.ADMIN)) {
                    predicates.add(cb.equal(root.get("role").get("name"), RoleName.ADMIN));
                } else if (accountType.equals(AccountType.MY_SHOP)) {
                    predicates.add(cb.isNotEmpty(root.get("shops")));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
