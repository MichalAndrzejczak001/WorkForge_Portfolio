import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authClient } from '../api/authClient';

function RegisterPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('CANDIDATE');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        try {
            await authClient.post('/api/auth/register', { email, password, role });
            navigate('/login');
        } catch {
            setError('Rejestracja się nie powiodłą. Sprawdź dane i spróbuj ponownie.');
        }
    }

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Rejestracja</h1>
            <form onSubmit={handleSubmit} className="mt-4 flex flex-col gap-3 max-w-sm">
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="Email"
                    className="border p-2 rounded"
                />
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Hasło"
                    className="border p-2 rounded"
                />
                <select
                    value={role}
                    onChange={(e) => setRole(e.target.value)}
                    className="border p-2 rounded"
                >
                    <option value="CANDIDATE">Kandydat</option>
                    <option value="RECRUITER">Rekruter</option>
                </select>
                <button type="submit" className="bg-blue-600 text-white p-2 rounded">
                    Zarejestruj się
                </button>
                {error && <p className="text-red-600">{error}</p>}
            </form>
        </div>
    );
}

export default RegisterPage;