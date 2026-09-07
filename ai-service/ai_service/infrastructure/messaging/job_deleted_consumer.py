import json
import uuid

from aiokafka import AIOKafkaConsumer
from sqlalchemy import delete

from ai_service.config import settings
from ai_service.domain.models.job_analysis import JobAnalysis
from ai_service.infrastructure.persistence.database import AsyncSessionLocal

TOPIC = "job.deleted"


async def consume_job_deleted_events() -> None:
    consumer = AIOKafkaConsumer(
        TOPIC,
        bootstrap_servers=settings.kafka_bootstrap_servers,
        group_id="ai-service"
    )
    await consumer.start()
    try:
        async for message in consumer:
            payload = json.loads(message.value)
            job_id = uuid.UUID(payload["jobId"])

            async with AsyncSessionLocal() as db:
                await db.execute(delete(JobAnalysis).where(JobAnalysis.job_id == job_id))
                await db.commit()
    finally:
        await consumer.stop()

