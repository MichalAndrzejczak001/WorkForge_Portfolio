import { createApiClient } from './client';

export const aiClient = createApiClient(import.meta.env.VITE_AI_API_URL);
