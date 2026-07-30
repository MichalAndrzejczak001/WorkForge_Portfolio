import { useState } from 'react';
import { Link } from 'react-router-dom';
import { searchClient } from '../api/searchCLient';

interface JobResult {
    id: string;
    title: string;
    location: string;
}

function SearchPage() {
    const [keyword, setKeyword] = useState('');
    const [results, setResults] = useState<JobResult[]>([]);

    async function handleSearch(e: React.FormEvent) {
        e.preventDefault();
        const response = await searchClient.get('/api/search', { params: { keyword } });
        setResults(response.data);
    }

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Szukaj ofert</h1>
            <form onSubmit={handleSearch} className="mt-4 flex gap-2 max-w-sm">
                <input
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                    placeholder="Słowo kluczowe"
                    className="border p-2 rounded flex-1"
                />
                <button type="submit" className="bg-blue-600 text-white p-2 rounded">
                    Szukaj
                </button>
            </form>
            <ul className="mt-4 flex flex-col gap-2">
                {results.map((job) => (
                    <li key={job.id} className="border p-3 rounded">
                        <Link to={`/jobs/${job.id}`} className="font-semibold">{job.title}</Link>
                        <p>{job.location}</p>
                    </li>
                ))}
            </ul>
        </div>
    );

}

export default SearchPage