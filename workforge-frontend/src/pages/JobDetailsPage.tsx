import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { jobClient } from '../api/jobClient';
import { applicationClient } from '../api/applicationClient';
import { aiClient } from '../api/aiClient';

interface JobDetails {
    id: string;
    title: string;
    description: string;
    location: string;
    salaryMin: number;
    salaryMax: number;
}

interface MatchResult {
    score: number;
    matched_skills: string[];
    missing_skills: string[];
    summary: string;
}

function JobDetailsPage() {
    const { id } = useParams();
    const [job, setJob] = useState<JobDetails | null>(null);
    const [applyMessage, setApplyMessage] = useState('');
    const [skillsInput, setSkillsInput] = useState('');
    const [matchResult, setMatchResult] = useState<MatchResult | null>(null);
    const [matchError, setMatchError] = useState('');

    async function handleMatch() {
        setMatchError('');
        try {
            const candidateSkills = skillsInput.split(',').map((s) => s.trim()).filter((s) => s !== '');
            const response = await aiClient.post('/api/ai/match', {
                job_id: id,
                candidate_id: localStorage.getItem('id'),
                candidate_skills: candidateSkills,
            });
            setMatchResult(response.data);
        } catch {
            setMatchError('Nie udało się sprawdzić dopasowania.');
        }
    }

    async function handleApply() {
        try {
            await applicationClient.post('/api/application', {
                jobId: id,
                applicantId: localStorage.getItem('id'),
            });
            setApplyMessage('Aplikacja wysłana!');
        } catch {
            setApplyMessage('Nie udało się wysłąć aplikacji.');
        }
    }

    useEffect(() => {
        async function fetchJob() {
            const response = await jobClient.get(`/api/jobs/${id}`);
            setJob(response.data);
        }
        fetchJob();
    }, [id]);

    if (!job) {
        return <div className="p-8">Ładowanie...</div>;
    }

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">{job.title}</h1>
            <p className="mt-2">{job.location}</p>
            <p className="mt-2">{job.salaryMin} - {job.salaryMax}</p>
            <p className="mt-4">{job.description}</p>
            {localStorage.getItem('role') === 'CANDIDATE' && (
            <button onClick={handleApply} className="mt-4 bg-blue-600 text-white p-2 rounded">
                Aplikuj
            </button>
            )}
            {applyMessage && <p className="mt-2">{applyMessage}</p>}
            {localStorage.getItem('role') === 'CANDIDATE' && (
                <div className="mt-6">
                    <h2 className="text-xl font-bold">Sprawdź dopasowanie</h2>
                    <input
                        value={skillsInput}
                        onChange={(e) => setSkillsInput(e.target.value)}
                        placeholder="Twoje umiejętności, oddzielone przecinkami"
                        className="border p-2 rounded mt-2 max-w-sm"
                    />
                    <button onClick={handleMatch} className="ml-2 bg-purple-600 text-white p-2 rounded">
                        Sprawdź dopasowanie
                    </button>
                    {matchError && <p className="text-red-600 mt-2">{matchError}</p>}
                    {matchResult && (
                        <div className="mt-2">
                            <p>Dopasowanie: {Math.round(matchResult.score * 100)}%</p>
                            <p>Pasujące: {matchResult.matched_skills.join(', ')}</p>
                            <p>Brakujące: {matchResult.missing_skills.join(', ')}</p>
                            <p>{matchResult.summary}</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default JobDetailsPage;