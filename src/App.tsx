// src/App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { routeConstants } from './constants/routeConstants';
import LoginPage from './pages/LoginPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path={routeConstants.HOME} element={<LoginPage />} />
      </Routes>
    </Router>
  );
}

export default App;