from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    database_url: str = "postgresql+asyncpg://postgres:password@localhost:5432/ai_db"
    kafka_bootstrap_servers: str = "localhost:9092"
    app_port: int = 8085
    openai_api_key: str = ""
    openai_model: str = "gpt-4o-mini"

    class Config:
        env_file = ".env"


settings = Settings()
