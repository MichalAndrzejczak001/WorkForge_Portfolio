import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage.tsx';
import LoginPage from './pages/LoginPage.tsx';
import RegisterPage from './pages/RegisterPage.tsx';
import ProtectedRoute from './components/ProtectedRoute.tsx';
import Navigation from './components/Navigation.tsx';

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route element={<ProtectedRoute />}>
                <Route
                    path="/"
                    element={
                        <>
                            <Navigation />
                            <HomePage />
                        </>
                    }
                />
            </Route>
        </Routes>
    );
}


export default App;
