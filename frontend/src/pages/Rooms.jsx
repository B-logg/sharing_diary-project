import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api';
import { useAuth } from '../contexts/AuthContext';

export default function Rooms() {
  const { user, logout } = useAuth();
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState('');

  const fetchRooms = async () => {
    setLoading(true); setErr('');
    try {
      const res = await api.get('/rooms');
      setRooms(res.data || []);
    } catch (e) {
      setErr(e?.response?.data?.message || '방 목록을 불러오지 못했어요.');
    } finally { setLoading(false); }
  };

  useEffect(() => { fetchRooms(); }, []);

  return (
    <div style={{maxWidth:720, margin:'24px auto', padding:16}}>
      <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
        <h2>내 방</h2>
        <div>
          <Link to="/friends" style={{marginRight:12}}>친구</Link>
          <button onClick={logout}>로그아웃</button>
        </div>
      </div>

      <p style={{color:'#667085'}}>로그인: {user?.username}</p>
      {err && <p style={{color:'#b91c1c'}}>{err}</p>}
      {loading ? <p>불러오는 중…</p> : (
        <ul style={{listStyle:'none', padding:0, display:'grid', gap:8}}>
          {rooms.map(r => (
            <li key={r.id} style={{border:'1px solid #eee', borderRadius:10, padding:12}}>
              <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
                <strong>{r.name}</strong>
                <Link to={`/rooms/${r.id}`}>입장</Link>
              </div>
              {r.direct && <small style={{color:'#667085'}}>1:1 룸</small>}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
