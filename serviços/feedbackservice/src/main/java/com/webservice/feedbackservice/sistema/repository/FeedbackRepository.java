package com.webservice.feedbackservice.sistema.repository;

import com.webservice.feedbackservice.sistema.entities.Feedback;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT DISTINCT f.userName FROM Feedback f")
    List<String> findDistinctUserNames();
    List<Feedback> findByUserNameIgnoreCase(String userName);
    @Transactional
    @Modifying
    @Query("delete from Feedback f where LOWER(f.userName) = LOWER(:userName)")
    void deleteUsersWithFeedback(@Param("userName") String userName);
}
