import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from ai_service.api.routers import skills, match
from ai_service.config import settings

app = FastAPI(title="AI Service", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:5174", "http://localhost:5175"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(skills.router, prefix="/api/ai")
app.include_router(match.router, prefix="/api/ai")

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=settings.app_port, reload=True)
