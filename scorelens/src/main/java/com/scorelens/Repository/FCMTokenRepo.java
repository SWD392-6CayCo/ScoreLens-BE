package com.scorelens.Repository;

import com.scorelens.Entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenRepo extends JpaRepository<FCMToken, Integer> {
}
