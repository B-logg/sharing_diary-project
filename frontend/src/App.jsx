import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Rooms from './pages/Rooms';
import RoomDetail from './pages/RoomDetail';
import Friends from './pages/Friends';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/rooms" replace />} />
        <Route path="/login" element={<Login/>} />
        <Route path="/register" element={<Register/>} />

        <Route path="/rooms" element={
          <ProtectedRoute><Rooms/></ProtectedRoute>
        } />
        <Route path="/rooms/:roomId" element={
          <ProtectedRoute><RoomDetail/></ProtectedRoute>
        } />
        <Route path="/friends" element={
          <ProtectedRoute><Friends/></ProtectedRoute>
        } />

        <Route path="*" element={<div style={{padding:24}}>페이지를 찾을 수 없어요.</div>} />
      </Routes>
    </BrowserRouter>
  );
}
