// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { AuthProvider } from './contexts/AuthContext';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  // 개발 중 StrictMode가 useEffect를 두 번 실행해 API를 중복 호출할 수 있어요.
  // 필요하면 StrictMode를 빼고 사용하세요.
  // <React.StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  // </React.StrictMode>
);

reportWebVitals();
