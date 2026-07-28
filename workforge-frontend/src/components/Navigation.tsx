import { Link, useNavigate } from 'react-router-dom';

function Navigation (){
    const role = localStorage.getItem('role');
    const navigate = useNavigate();

    function handleLogout() {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        navigate('/login');
    }

    return (
        <nav className="p-4 bg-gray-100 flex gap-4">
            {role === 'CANDIDATE' && <Link to="/jobs">Przeglądaj oferty</Link>}
            {role === 'RECRUITER' && <span>Panel rekrutera</span>}
            <button onClick={handleLogout}>Wyloguj</button>
        </nav>
    );
}

export default Navigation;

