import React, { useEffect, useState } from 'react';
import api from '../api';
import { useAuth } from '../contexts/AuthContext';
import { Link } from 'react-router-dom';

export default function Friends() {
  const { user } = useAuth();
  const [requests, setRequests] = useState([]);
  const [toUserId, setToUserId] = useState('');
  const [msg, setMsg] = useState('');

  const load = async () => {
    const res = await api.get('/friends/requests');
    setRequests(res.data || []);
  };
  useEffect(() => { load(); }, []);

  const sendRequest = async (e) => {
    e.preventDefault();
    setMsg('');
    await api.post('/friends/requests', { toUserId: Number(toUserId) });
    setMsg('요청을 보냈습니다.');
    setToUserId('');
    load();
  };

  const accept = async (id) => {
    await api.post(`/friends/requests/${id}/accept`);
    setMsg('수락되었습니다. /rooms에서 새 1:1 룸을 확인하세요!');
    load();
  };

  return (
    <div style={{maxWidth:720, margin:'24px auto', padding:16}}>
      <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
        <h2>친구</h2>
        <Link to="/rooms">← 방 목록</Link>
      </div>

      <form onSubmit={sendRequest} style={{display:'flex', gap:8, marginBottom:12}}>
        <input placeholder="상대 사용자 ID" value={toUserId} onChange={e=>setToUserId(e.target.value)} />
        <button type="submit">친구 요청</button>
      </form>

      {msg && <p style={{color:'#155eef'}}>{msg}</p>}

      <h3>요청 목록(보낸/받은)</h3>
      <ul style={{listStyle:'none', padding:0, display:'grid', gap:8}}>
        {requests.map(r => (
          <li key={r.id} style={{border:'1px solid #eee', borderRadius:10, padding:12}}>
            <div>#{r.id} — {r.requesterId} ➜ {r.addresseeId} — <strong>{r.status}</strong></div>
            {(r.status === 'PENDING' && r.addresseeId === user?.id) && (
              <button onClick={()=>accept(r.id)} style={{marginTop:8}}>수락</button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
