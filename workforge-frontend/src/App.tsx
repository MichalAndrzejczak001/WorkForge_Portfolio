import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProtectedRoute from './components/ProtectedRoute';
import JobsPage from './pages/JobsPage';
import JobDetailsPage from './pages/JobDetailsPage';
import CreateJobPage from './pages/CreateJobPage';
import MyJobsPage from './pages/MyJobsPage';
import JobApplicantsPage from './pages/JobApplicantsPage';
import SearchPage from './pages/SearchPage';

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route element={<ProtectedRoute />}>
                <Route path="/" element={<HomePage />} />
                <Route path="/jobs" element={<JobsPage />} />
                <Route path="/jobs/:id" element={<JobDetailsPage />} />
                <Route path="/jobs/new" element={<CreateJobPage />} />
                <Route path="/my-jobs" element={<MyJobsPage />} />
                <Route path="/jobs/:id/applicants" element={<JobApplicantsPage />} />
                <Route path="/search" element={<SearchPage />} />
            </Route>
        </Routes>
    );
}


export default App;
