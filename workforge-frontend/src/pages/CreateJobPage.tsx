import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jobClient } from '../api/jobClient';
import { aiClient } from '../api/aiClient';

function CreateJobPage() {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [location, setLocation] = useState('');
    const [salaryMin, setSalaryMin] = useState('');
    const [salaryMax, setSalaryMax] = useState('');
    const [companyName, setCompanyName] = useState('');
    const [workMode, setWorkMode] = useState('REMOTE');
    const [experienceLevel, setExperienceLevel] = useState('JUNIOR');
    const [skills, setSkills] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();


    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        try {
            const response = await jobClient.post(
                '/api/jobs',
                {
                    title,
                    description,
                    location,
                    salaryMin,
                    salaryMax,
                    companyName,
                    workMode,
                    experienceLevel,
                    skills: skills.split(',').map((s) => s.trim()).filter((s) => s !== ''),
                },
            );

            await aiClient.post('/api/ai/skills/extract', {
               job_id: response.data.id,
               description: description,
            });

            navigate('/my-jobs');
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
                <input value={companyName} onChange={(e) => setCompanyName(e.target.value)} placeholder="Firma" className="border p-2 rounded" />
                <select value={workMode} onChange={(e) => setWorkMode(e.target.value)} className="border p-2 rounded">
                    <option value="REMOTE">Zdalnie</option>
                    <option value="HYBRID">Hybrydowo</option>
                    <option value="ONSITE">Stacjonarnie</option>
                </select>
                <select value={experienceLevel} onChange={(e) => setExperienceLevel(e.target.value)} className="border p-2 rounded">
                    <option value="JUNIOR">Junior</option>
                    <option value="MID">Mid</option>
                    <option value="SENIOR">Senior</option>
                </select>
                <input value={salaryMin} onChange={(e) => setSalaryMin(e.target.value)} placeholder="Widełki od" className="border p-2 rounded" />
                <input value={salaryMax} onChange={(e) => setSalaryMax(e.target.value)} placeholder="Widełki do" className="border p-2 rounded" />
                <input value={skills} onChange={(e) => setSkills(e.target.value)} placeholder="Umiejętności (oddzielone przecinkami)" className="border p-2 rounded" />
                <button type="submit" className="bg-blue-600 text-white p-2 rounded">
                    Utwórz ofertę
                </button>
                {error && <p className="text-red-600">{error}</p>}
            </form>
        </div>
    );
}

export default CreateJobPage;
