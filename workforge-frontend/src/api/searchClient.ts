import { createApiClient } from './client';

export const searchClient = createApiClient(import.meta.env.VITE_SEARCH_API_URL);

