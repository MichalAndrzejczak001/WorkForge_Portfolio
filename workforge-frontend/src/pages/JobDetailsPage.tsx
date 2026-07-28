import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { jobClient } from '../api/jobClient';

interface JobDetails {
    id: string;
    title: string;
    description: string;
    location: string;
    salaryMin: number;
    salaryMax: number;
}

function JobDetailsPage() {
    const { id } = useParams();
    const [job, setJob] = useState<JobDetails | null>(null);

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
        </div>
    );
}

export default JobDetailsPage;