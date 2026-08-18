import axios from 'axios';

export function createApiClient(baseURL: string) {
    const client = axios.create({
        baseURL,
        headers: {
            'Content-Type': 'application/json',
        },
    });

    client.interceptors.request.use((config) => {
            const token = localStorage.getItem('token');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        });

    return client;
}
