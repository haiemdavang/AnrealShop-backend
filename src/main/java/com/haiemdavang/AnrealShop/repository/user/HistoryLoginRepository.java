package com.haiemdavang.AnrealShop.repository.user;

import com.haiemdavang.AnrealShop.modal.entity.user.HistoryLogin;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryLoginRepository extends JpaRepository<HistoryLogin, String> {
    HistoryLogin findByUserAndDevice(User user, String device);

    List<HistoryLogin> findByUserOrderByLoginAtDesc(User user);
}
