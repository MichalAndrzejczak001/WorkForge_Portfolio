git add application-service/src/main/java/com/workforge/applicationservice/domain/event/JobArchivedEvent.java \
        application-service/src/main/java/com/workforge/applicationservice/domain/event/JobDeletedEvent.java \
        application-service/src/main/java/com/workforge/applicationservice/domain/event/JobExpiredEvent.java \
        application-service/src/main/java/com/workforge/applicationservice/domain/event/JobPublishedEvent.java \
        application-service/src/main/java/com/workforge/applicationservice/domain/model/JobCacheStatus.java \
        application-service/src/main/java/com/workforge/applicationservice/domain/model/JobStatusCache.java \
        application-service/src/main/java/com/workforge/applicationservice/infrastructure/messaging/JobEventConsumer.java \
        application-service/src/main/java/com/workforge/applicationservice/infrastructure/persistence/JobStatusCacheRepository.java \
        application-service/src/main/resources/application.yaml \
        application-service/src/main/resources/db/migration/V4__create_job_status_cache_table.sql \
        application-service/src/main/resources/db/migration/V5__add_status_changed_at_to_job_status_cache.sql \
        job-service/src/main/java/com/workforge/jobservice/application/service/JobService.java \
        job-service/src/main/java/com/workforge/jobservice/domain/event/JobArchivedEvent.java \
        job-service/src/main/java/com/workforge/jobservice/domain/event/JobDeletedEvent.java \
        job-service/src/main/java/com/workforge/jobservice/domain/event/JobExpiredEvent.java \
        job-service/src/main/java/com/workforge/jobservice/domain/event/JobPublishedEvent.javapackage com.workforge.jobservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExpiredEvent {
    private UUID jobId;
    private LocalDateTime occurredAt;
}
