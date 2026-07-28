import { createApiClient } from './client';

export const jobClient = createApiClient(import.meta.env.VITE_JOB_API_URL);


