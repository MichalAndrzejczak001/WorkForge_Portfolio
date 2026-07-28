import { createApiClient } from './client';

export const applicationClient = createApiClient(import.meta.env.VITE_APPLICATION_API_URL);