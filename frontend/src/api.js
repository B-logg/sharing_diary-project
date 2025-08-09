// src/api.js
import axios from 'axios';

// 프록시(package.json의 "proxy": "http://localhost:8080") 기준
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

// 요청 인터셉터: 토큰이 있으면 Authorization 헤더에 자동 추가
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;
