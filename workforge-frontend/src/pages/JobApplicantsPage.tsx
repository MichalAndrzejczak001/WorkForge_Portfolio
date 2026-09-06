import {useState, useEffect} from 'react';
import {useParams} from 'react-router-dom';
import {applicationClient} from '../api/applicationClient';

interface Application {
    id: string;
    applicantId: string;
    status: string;
    appliedAt: string;
}

function JobApplicantsPage() {
    const {id} = useParams();
    const [applications, setApplications] = useState<Application[]>([]);
    const [error, setError] = useState('');

    useEffect(() => {
        async function fetchApplications() {
            const response = await applicationClient.get(`/api/application/job/${id}`);
            setApplications(response.data);
        }

        fetchApplications();
    }, [id]);

    async function handleChangeStatus(applicationId: string, newStatus: string) {
        try {
            await applicationClient.patch(`/api/application/${applicationId}/status`, {status: newStatus});
            setApplications(
                applications.map((app) =>
                    app.id === applicationId ? {...app, status: newStatus} : app
                )
            );
        } catch {
            setError('Nie udało się zmienić statusu aplikacji.');
        }
    }

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold">Aplikacje</h1>
            {error && <p className="text-red-600">{error}</p>}
            <ul className="mt-4 flex flex-col gap-2">
                {applications.map((app) => (
                    <li key={app.id} className="border p-3 rounded">
                        <p>Kandydat: {app.applicantId}</p>
                        <p>Status: {app.status}</p>
                        {(app.status === 'PENDING' || app.status === 'REVIEWED') && (
                            <>
                                <button onClick={() => handleChangeStatus(app.id, 'ACCEPTED')}
                                        className="bg-green-600 text-white p-1 rounded mt-1">
                                    Akceptuj
                                </button>
                                <button onClick={() => handleChangeStatus(app.id, 'REJECTED')}
                                        className="bg-red-600 text-white p-1 rounded mt-1 ml-2">
                                    Odrzuć
                                </button>
                            </>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default JobApplicantsPage;