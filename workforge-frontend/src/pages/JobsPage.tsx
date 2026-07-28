import { useState, useEffect } from 'react';
import { jobClient } from '../api/jobClient';
import { Link } from 'react-router-dom';

interface Job {
    id: string;
    title: string;
    description: string;
    location: string;
}

function JobsPage() {
    const [jobs, setJobs] = useState<Job[]>([]);

    useEffect(() => {
        async function fetchJobs() {
            const response = await jobClient.get('/api/jobs');
            setJobs(response.data);
        }
        fetchJobs();
    }, []);

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Oferty pracy</h1>
            <ul className="mt-4 flex flex-col gap-2">
                {jobs.map((job) => (
                    <li key={job.id} className="border p-3 rounded">
                        <h2 className="font-semibold">
                            <Link to={`/jobs/${job.id}`}>{job.title}</Link>
                        </h2>
                        <p>{job.location}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default JobsPage;