package com.haiemdavang.AnrealShop.repository;

import com.haiemdavang.AnrealShop.modal.entity.Favorite;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {

    @EntityGraph(attributePaths = {"product", "product.shop"})
    Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Optional<Favorite> findByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);

    int countByUser(User user);

    int countByProduct(Product product);

    @Query("SELECT f.product.id FROM Favorite f WHERE f.user = :user")
    Set<String> findProductIdsByUser(User user);
}
