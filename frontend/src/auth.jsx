import { createContext, useContext, useEffect, useMemo, useState } from 'react';

const AuthContext = createContext(null);

function readUser() {
  const token = localStorage.getItem('token');
  if (!token) return null;
  return { token, userId: Number(localStorage.getItem('userId')), email: localStorage.getItem('email'), role: localStorage.getItem('role') };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUser);
  useEffect(() => {
    const sync = () => setUser(readUser());
    const expired = () => {
      ['token', 'userId', 'email', 'role'].forEach((key) => localStorage.removeItem(key));
      setUser(null);
    };
    window.addEventListener('storage', sync);
    window.addEventListener('auth-expired', expired);
    return () => { window.removeEventListener('storage', sync); window.removeEventListener('auth-expired', expired); };
  }, []);

  const value = useMemo(() => ({
    user,
    signIn(data) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('userId', data.userId);
      localStorage.setItem('email', data.email);
      localStorage.setItem('role', data.role);
      setUser(readUser());
    },
    signOut() {
      ['token', 'userId', 'email', 'role'].forEach((key) => localStorage.removeItem(key));
      setUser(null);
    },
  }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() { return useContext(AuthContext); }
