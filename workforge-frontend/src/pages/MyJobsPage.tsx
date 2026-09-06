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
            await jobClient.post(`/api/jobs/${jobId}/publish`);
            setJobs(jobs.map((job) => (job.id === jobId) ? {...job, status: 'PUBLISHED' } : job));
        } catch {
            setError('Nie udało się opublikować oferty.');
        }
    }

    async function handleArchive(jobId: string) {
        try {
            await jobClient.post(`/api/jobs/${jobId}/archive`);
            setJobs(jobs.map((job) => (job.id === jobId) ? {...job, status: 'ARCHIVED' } : job));
        } catch {
            setError('Nie udało się zarchiwizować oferty.');
        }
    }

    async function handleDelete(jobId: string) {
        try {
            await jobClient.delete(`/api/jobs/${jobId}`);
            setJobs(jobs.filter((job) => job.id !== jobId));
        } catch {
            setError('Nie udało się usunąć oferty.');
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
                        {job.status !== 'ARCHIVED' && (
                            <button onClick={() => handleArchive(job.id)} className="bg-gray-600 text-white p-1 rounded mt-1 ml-2">
                                Archiwizuj
                            </button>
                        )}
                        {job.status === 'DRAFT' && (
                            <button onClick={() => handleDelete(job.id)} className="bg-red-600 text-white p-1 rounded mt-1 ml-2">
                                Usuń
                            </button>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default MyJobsPage;