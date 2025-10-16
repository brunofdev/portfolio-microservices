package com.user_service.repository;

import com.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserNameIgnoreCase(String userName);
    List<User> findByUserNameInIgnoreCase(List<String> listOfUserNames);
    List<User> findByUserNameIn(List<String> listOfUserNames);
    boolean existsByUserNameIgnoreCase(String userName);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
}
