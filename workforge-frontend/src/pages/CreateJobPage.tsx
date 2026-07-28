import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jobClient } from '../api/jobClient';

function CreateJobPage() {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [location, setLocation] = useState('');
    const [salaryMin, setSalaryMin] = useState('');
    const [salaryMax, setSalaryMax] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        try {
            await jobClient.post(
                '/api/jobs',
                { title, description, location, salaryMin, salaryMax },
                { headers: { 'X-User-Id': localStorage.getItem('id') } },
            );
            navigate('/jobs');
        } catch {
            setError('Nie udało się utworzyć oferty.');
        }
    }

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Nowa oferta</h1>
            <form onSubmit={handleSubmit} className="mt-4 flex flex-col gap-3 max-w-sm">
                <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="Tytuł" className="border p-2 rounded" />
                <input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Opis" className="border p-2 rounded" />
                <input value={location} onChange={(e) => setLocation(e.target.value)} placeholder="Lokalizacja" className="border p-2 rounded" />
                <input value={salaryMin} onChange={(e) => setSalaryMin(e.target.value)} placeholder="Widełki od" className="border p-2 rounded" />
                <input value={salaryMax} onChange={(e) => setSalaryMax(e.target.value)} placeholder="Widełki do" className="border p-2 rounded" />
                <button type="submit" className="bg-blue-600 text-white p-2 rounded">
                    Utwórz ofertę
                </button>
                {error && <p className="text-red-600">{error}</p>}
            </form>
        </div>
    );
}

export default CreateJobPage;
