import asyncio
from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI

from ai_service.api.routers import skills, match
from ai_service.config import settings
from ai_service.infrastructure.messaging.job_deleted_consumer import consume_job_deleted_events


@asynccontextmanager
async def lifespan(app: FastAPI):
    task = asyncio.create_task(consume_job_deleted_events())
    yield
    task.cancel()


app = FastAPI(title="AI Service", version="1.0.0", lifespan=lifespan)

app.include_router(skills.router, prefix="/api/ai")
app.include_router(match.router, prefix="/api/ai")

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=settings.app_port, reload=True)

