import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { jobClient } from '../api/jobClient';

interface Job {
    id: string;
    title: string;
    description: string;
    location: string;
    status: string;
}

function MyJobsPage() {
    const [jobs, setJobs] = useState<Job[]>([]);
    const [error, setError] = useState('');

    useEffect(() => {
        async function fetchMyJobs() {
            const recruiterId = localStorage.getItem('id');
            const response = await jobClient.get(`/api/jobs/recruiter/${recruiterId}`);
            setJobs(response.data);
        }
        fetchMyJobs();
    }, []);

    async function handlePublish(jobId: string) {
        try {
            await jobClient.patch(`/api/jobs/${jobId}/status`, { status: 'PUBLISHED' });
            setJobs(jobs.map((job) => (job.id === jobId) ? {...job, status: 'PUBLISHED' } : job));
        } catch {
            setError('Nie udało się opublikować oferty.');
        }
    }

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Moje oferty</h1>
            {error && <p className="text-red-600">{error}</p>}
            <ul className="mt-4 flex flex-col gap-2">
                {jobs.map((job) => (
                    <li key={job.id} className="border p-3 rounded">
                        <Link to={`/jobs/${job.id}`} className="font-semibold">{job.title}</Link>
                        <p>{job.location} — {job.status}</p>
                        <Link to={`/jobs/${job.id}/applicants`}>Zobacz aplikacje</Link>
                        {job.status === 'DRAFT' && (
                            <button onClick={() => handlePublish(job.id)} className="bg-green-600 text-white p-1 rounded mt-1">
                                Publikuj
                            </button>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default MyJobsPage;