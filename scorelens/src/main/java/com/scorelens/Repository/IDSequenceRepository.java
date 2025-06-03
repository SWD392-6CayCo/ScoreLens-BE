package com.scorelens.Repository;

import com.scorelens.Entity.IDSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IDSequenceRepository extends JpaRepository<IDSequence, String> {
    //Dùng @Lock(LockModeType.PESSIMISTIC_WRITE) để lock row tương ứng khi nhiều thread gọi đồng thời – tránh trùng staffID.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM IDSequence s WHERE s.rolePrefix = :prefix")
    IDSequence findAndLockByRolePrefix(@Param("prefix") String prefix);
}