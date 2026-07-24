import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authClient } from '../api/authClient'

function LoginPage() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const navigate = useNavigate()

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault()
        setError('')

        try {
            const response = await authClient.post('/api/auth/login', {email, password})
            localStorage.setItem('token', response.data.token)
            navigate('/')
        } catch {
            setError('Nieprawidłowy email lub hasło.')
        }
    }

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Logowanie</h1>
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
                <button type="submit" className="bg-blue-600 text-white p-2 rounded">
                    Zaloguj się
                </button>
                {error && <p className="text-red-600">{error}</p>}
            </form>
        </div>
    )
}

export default LoginPage