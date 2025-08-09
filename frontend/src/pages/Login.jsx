import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import api from '../api';
import { useAuth } from '../contexts/AuthContext';

export default function Login() {
  const { setToken, setUser } = useAuth();
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [err, setErr] = useState('');
  const nav = useNavigate();
  const loc = useLocation();

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr('');
    try {
      const res = await api.post('/auth/login', { id, password });
      setToken(res.data.token);
      setUser(res.data.user);
      const to = loc.state?.from?.pathname || '/rooms';
      nav(to, { replace: true });
    } catch (error) {
      setErr(error?.response?.data?.message || '로그인 실패');
    }
  };

  return (
    <div style={box}>
      <h2>로그인</h2>
      {err && <p style={error}>{err}</p>}
      <form onSubmit={onSubmit} style={{display:'grid', gap:8}}>
        <input placeholder="이메일 또는 닉네임" value={id} onChange={e=>setId(e.target.value)} />
        <input placeholder="비밀번호" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        <button type="submit">로그인</button>
      </form>
      <p style={{marginTop:8}}>계정이 없나요? <Link to="/register">회원가입</Link></p>
    </div>
  );
}

const box = { maxWidth: 360, margin:'64px auto', padding:16, border:'1px solid #eee', borderRadius:12 };
const error = { color:'#b91c1c' };
