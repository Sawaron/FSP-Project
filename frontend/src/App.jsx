import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import AthletesPage from './pages/AthletesPage';
import CompetitionsPage from './pages/CompetitionsPage';
import RatingPage from './pages/RatingPage';
import RegisterPage from './pages/RegisterPage';
import LoginPage from './pages/LoginPage';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Navbar />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/athletes" element={<AthletesPage />} />
          <Route path="/competitions" element={<CompetitionsPage />} />
          <Route path="/rating" element={<RatingPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
