import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api';
import { useAuth } from '../contexts/AuthContext';

export default function Register() {
  const { setToken, setUser } = useAuth();
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [err, setErr] = useState('');
  const nav = useNavigate();

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr('');
    try {
      const res = await api.post('/auth/register', { email, username, password });
      setToken(res.data.token);
      setUser(res.data.user);
      nav('/rooms', { replace: true });
    } catch (error) {
      setErr(error?.response?.data?.message || '회원가입 실패');
    }
  };

  return (
    <div style={box}>
      <h2>회원가입</h2>
      {err && <p style={error}>{err}</p>}
      <form onSubmit={onSubmit} style={{display:'grid', gap:8}}>
        <input placeholder="이메일" value={email} onChange={e=>setEmail(e.target.value)} />
        <input placeholder="닉네임" value={username} onChange={e=>setUsername(e.target.value)} />
        <input placeholder="비밀번호(6자 이상)" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        <button type="submit">가입하기</button>
      </form>
      <p style={{marginTop:8}}><Link to="/login">로그인으로</Link></p>
    </div>
  );
}
const box = { maxWidth: 360, margin:'64px auto', padding:16, border:'1px solid #eee', borderRadius:12 };
const error = { color:'#b91c1c' };
