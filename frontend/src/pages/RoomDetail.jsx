// src/pages/RoomDetail.jsx
import React, { useEffect, useMemo, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api';

export default function RoomDetail() {
  const { roomId } = useParams();

  const [diaries, setDiaries] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errMsg, setErrMsg] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState({ title: '', content: '', date: '' }); // YYYY-MM-DD

  const sortedDiaries = useMemo(
    () => [...diaries].sort((a, b) => (b?.date ?? '').localeCompare(a?.date ?? '')),
    [diaries]
  );

  const showError = (e, fallback = '문제가 발생했어요. 잠시 후 다시 시도해주세요.') => {
    const message = e?.response?.data?.message || e?.message || fallback;
    setErrMsg(message);
    console.error(e);
  };

  const formatDate = (yyyyMMdd) => {
    if (!yyyyMMdd) return '';
    try {
      const d = new Date(yyyyMMdd);
      if (isNaN(d.getTime())) return yyyyMMdd;
      return d.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
    } catch {
      return yyyyMMdd;
    }
  };

  const fetchDiaries = async () => {
    setLoading(true);
    setErrMsg('');
    try {
      const res = await api.get(`/rooms/${roomId}/diaries`);
      setDiaries(res.data ?? []);
    } catch (e) {
      showError(e, '일기 목록을 불러오지 못했어요.');
    } finally {
      setLoading(false);
    }
  };

  // 방이 바뀔 때 폼/편집 상태 초기화 후 목록 재요청
  useEffect(() => {
    setEditingId(null);
    setForm({ title: '', content: '', date: '' });
    setErrMsg('');
    fetchDiaries();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [roomId]);

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const validateForm = () => {
    if (!form.title.trim()) {
      setErrMsg('제목은 필수입니다.');
      return false;
    }
    if (!form.date) {
      setErrMsg('날짜는 필수입니다.');
      return false;
    }
    return true;
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setErrMsg('');
    if (!validateForm()) return;

    try {
      setLoading(true);
      if (editingId) {
        await api.put(`/rooms/${roomId}/diaries/${editingId}`, form);
      } else {
        await api.post(`/rooms/${roomId}/diaries`, form);
      }
      setForm({ title: '', content: '', date: '' });
      setEditingId(null);
      await fetchDiaries();
    } catch (e2) {
      showError(e2, editingId ? '수정에 실패했어요.' : '작성에 실패했어요.');
    } finally {
      setLoading(false);
    }
  };

  const onDelete = async (id) => {
    if (!window.confirm('정말 삭제하시겠어요? 되돌릴 수 없어요.')) return;
    setErrMsg('');
    try {
      setLoading(true);
      await api.delete(`/rooms/${roomId}/diaries/${id}`);
      setDiaries((list) => list.filter((d) => d.id !== id));
      if (editingId === id) {
        setEditingId(null);
        setForm({ title: '', content: '', date: '' });
      }
    } catch (e) {
      showError(e, '삭제에 실패했어요.');
    } finally {
      setLoading(false);
    }
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setForm({ title: item.title ?? '', content: item.content ?? '', date: item.date ?? '' });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm({ title: '', content: '', date: '' });
    setErrMsg('');
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>🗂️ 방 #{roomId}</h2>
        <Link to="/rooms">← 방 목록</Link>
      </div>

      {errMsg && (
        <div style={styles.error}>
          {errMsg}
          <button onClick={() => setErrMsg('')} style={styles.closeBtn} title="닫기" aria-label="닫기">✕</button>
        </div>
      )}

      <form onSubmit={onSubmit} style={styles.form}>
        <div style={styles.row}>
          <label style={styles.label}>제목</label>
          <input
            name="title"
            placeholder="제목"
            value={form.title}
            onChange={onChange}
            style={styles.input}
            maxLength={100}
            required
          />
        </div>

        <div style={styles.row}>
          <label style={styles.label}>내용</label>
          <textarea
            name="content"
            placeholder="내용"
            rows={6}
            value={form.content}
            onChange={onChange}
            style={{ ...styles.input, resize: 'vertical' }}
          />
        </div>

        <div style={styles.row}>
          <label style={styles.label}>날짜</label>
          <input
            name="date"
            type="date"
            value={form.date}
            onChange={onChange}
            style={styles.input}
            required
          />
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          <button type="submit" disabled={loading} style={styles.primaryBtn}>
            {editingId ? '수정 완료' : '작성하기'}
          </button>
          {editingId && (
            <button type="button" onClick={cancelEdit} disabled={loading} style={styles.ghostBtn}>
              취소
            </button>
          )}
        </div>
      </form>

      <hr style={{ margin: '24px 0' }} />

      <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
        <h3 style={{ margin: '8px 0' }}>📚 일기 목록</h3>
        {loading && <span style={{ fontSize: 14 }}>로딩 중…</span>}
      </div>

      {sortedDiaries.length === 0 && !loading ? (
        <div style={{ color: '#666' }}>아직 작성된 일기가 없어요.</div>
      ) : (
        <ul style={styles.list}>
          {sortedDiaries.map((d) => (
            <li key={d.id} style={styles.card}>
              <div style={styles.cardHeader}>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                  <strong style={{ fontSize: 16 }}>{d.title}</strong>
                  <small style={{ color: '#667085' }}>{formatDate(d.date)}</small>
                </div>
                <div style={{ display: 'flex', gap: 8 }}>
                  <button onClick={() => startEdit(d)} disabled={loading} style={styles.secondaryBtn}>수정</button>
                  <button onClick={() => onDelete(d.id)} disabled={loading} style={styles.dangerBtn}>삭제</button>
                </div>
              </div>
              {d.content && <p style={{ margin: '8px 0 0', whiteSpace: 'pre-wrap' }}>{d.content}</p>}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

const styles = {
  container: { padding: '24px', maxWidth: 760, margin: '0 auto', fontFamily: '-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica,Arial,Apple SD Gothic Neo,Noto Sans KR,sans-serif' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  form: { display: 'grid', gap: 12, padding: 16, border: '1px solid #e5e7eb', borderRadius: 12, background: '#fafafa' },
  row: { display: 'grid', gap: 6 },
  label: { fontSize: 12, color: '#667085' },
  input: { padding: '10px 12px', borderRadius: 10, border: '1px solid #d0d5dd', outline: 'none' },
  list: { listStyle: 'none', padding: 0, display: 'grid', gap: 12 },
  card: { border: '1px solid #e5e7eb', borderRadius: 12, padding: 12, background: 'white' },
  cardHeader: { display: 'flex', justifyContent: 'space-between', gap: 12, alignItems: 'center' },
  primaryBtn: { padding: '10px 12px', borderRadius: 10, border: '1px solid #155eef', background: '#155eef', color: '#fff', cursor: 'pointer' },
  secondaryBtn: { padding: '8px 10px', borderRadius: 10, border: '1px solid #d0d5dd', background: '#fff', cursor: 'pointer' },
  ghostBtn: { padding: '10px 12px', borderRadius: 10, border: '1px solid #d0d5dd', background: 'transparent', cursor: 'pointer' },
  dangerBtn: { padding: '8px 10px', borderRadius: 10, border: '1px solid #ef4444', background: '#fee2e2', color: '#b91c1c', cursor: 'pointer' },
  error: { background: '#FEF3F2', color: '#B42318', border: '1px solid #FEE2E2', borderRadius: 12, padding: '10px 12px', marginBottom: 12, display: 'inline-flex', alignItems: 'center' },
  closeBtn: { marginLeft: 8, border: 'none', background: 'transparent', cursor: 'pointer' },
};
