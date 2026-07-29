import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { jobClient } from '../api/jobClient';

interface Job {
    id: string;
    title: string;
    location: string;
    status: string;
}

function MyJobsPage() {
    const [jobs, setJobs] = useState<Job[]>([]);

    useEffect(() => {
        async function fetchMyJobs() {
            const recruiterId = localStorage.getItem('id');
            const response = await jobClient.get(`/api/jobs/recruiter/${recruiterId}`);
            setJobs(response.data);
        }
        fetchMyJobs();
    }, []);

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Moje oferty</h1>
            <ul className="mt-4 flex flex-col gap-2">
                {jobs.map((job) => (
                    <li key={job.id} className="border p-3 rounded">
                        <Link to={`/jobs/${job.id}`} className="font-semibold">{job.title}</Link>
                        <p>{job.location} — {job.status}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default MyJobsPage;