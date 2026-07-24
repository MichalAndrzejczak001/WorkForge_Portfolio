import { createApiClient } from './client'

export const authClient = createApiClient(import.meta.env.VITE_AUTH_API_URL)