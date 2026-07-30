import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { applicationClient } from '../api/applicationClient';

interface Application {
    id: string;
    applicantId: string;
    status: string;
    appliedAt: string;
}

function JobApplicantsPage() {
    const { id } = useParams();
    const [applications, setApplications] = useState<Application[]>([]);

    useEffect(() => {
        async function fetchApplications() {
            const response = await applicationClient.get(`/api/application/job/${id}`);
            setApplications(response.data);
        }
        fetchApplications();
    }, [id]);

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Aplikacje</h1>
            <ul className="mt-4 flex flex-col gap-2">
                {applications.map((app) => (
                    <li key={app.id} className="border p-3 rounded">
                        <p>Kandydat: {app.applicantId}</p>
                        <p>Status: {app.status}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default JobApplicantsPage;