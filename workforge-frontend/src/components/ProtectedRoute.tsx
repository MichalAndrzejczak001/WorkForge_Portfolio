import { Navigate, Outlet } from 'react-router-dom';
import Navigation from './Navigation.tsx';

function ProtectedRoute() {
    const token = localStorage.getItem('token');

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    return (
            <>
                <Navigation />
                <Outlet />
            </>
    );
}

export default ProtectedRoute;
