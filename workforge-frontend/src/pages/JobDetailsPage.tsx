import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { jobClient } from '../api/jobClient';
import { applicationClient } from '../api/applicationClient';


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
    const [applyMessage, setApplyMessage] = useState('');

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
            <button onClick={handleApply} className="mt-4 bg-blue-600 text-white p-2 rounded">
                Aplikuj
            </button>
            {applyMessage && <p className="mt-2">{applyMessage}</p>}
        </div>
    );
}

export default JobDetailsPage;