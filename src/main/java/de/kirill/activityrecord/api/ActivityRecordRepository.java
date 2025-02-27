package de.kirill.activityrecord.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for ActivityRecord entities. Extends JpaRepository to provide CRUD operations. */
@Repository
public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {
}