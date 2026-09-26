import { BrowserRouter, Link, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth';
import Footer from './components/Footer';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import AthletePage from './pages/AthletePage';
import AthletesPage from './pages/AthletesPage';
import AuthPage from './pages/AuthPage';
import CompetitionPage from './pages/CompetitionPage';
import CompetitionsPage from './pages/CompetitionsPage';
import HomePage from './pages/HomePage';
import OrganizerCompetitionPage from './pages/OrganizerCompetitionPage';
import OrganizerPage from './pages/OrganizerPage';
import ProfilePage from './pages/ProfilePage';
import RatingPage from './pages/RatingPage';
import './App.css';

function NotFound() {
  return <main className="page"><div className="empty-state not-found"><strong>404</strong><h1>Страница не найдена</h1><p>Возможно, адрес изменился или страница была удалена.</p><Link className="btn-primary" to="/">На главную</Link></div></main>;
}

export default function App() {
  return <BrowserRouter><AuthProvider><div className="app-container"><Navbar /><Routes>
    <Route path="/" element={<HomePage />} /><Route path="/athletes" element={<AthletesPage />} /><Route path="/athletes/:id" element={<AthletePage />} />
    <Route path="/competitions" element={<CompetitionsPage />} /><Route path="/competitions/:id" element={<CompetitionPage />} /><Route path="/rating" element={<RatingPage />} />
    <Route path="/login" element={<AuthPage />} /><Route path="/register" element={<AuthPage register />} />
    <Route path="/profile" element={<ProtectedRoute role="ATHLETE"><ProfilePage /></ProtectedRoute>} />
    <Route path="/organizer" element={<ProtectedRoute role="ORGANIZER"><OrganizerPage /></ProtectedRoute>} />
    <Route path="/organizer/competitions/:id" element={<ProtectedRoute role="ORGANIZER"><OrganizerCompetitionPage /></ProtectedRoute>} />
    <Route path="*" element={<NotFound />} />
  </Routes><Footer /></div></AuthProvider></BrowserRouter>;
}
