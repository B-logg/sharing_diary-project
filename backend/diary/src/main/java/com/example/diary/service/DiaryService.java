package com.example.diary.service;

import com.example.diary.model.*;
import com.example.diary.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
 // import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DiaryService {
    private final DiaryRepository diaries;
    private final RoomRepository rooms;
    private final RoomMemberRepository members;
    private final UserRepository users;

    public DiaryService(DiaryRepository diaries, RoomRepository rooms,
                        RoomMemberRepository members, UserRepository users) {
        this.diaries = diaries; this.rooms = rooms; this.members = members; this.users = users;
    }

    @Transactional(readOnly = true)
    public List<Diary> listInRoom(Long roomId, Long me) {
        requireMember(roomId, me);
        Room r = rooms.findById(roomId).orElseThrow();
        return diaries.findByRoomOrderByDateDesc(r);
    }

    public Diary createInRoom(Long roomId, Long me, String title, String content, String date) {
        Room r = requireMember(roomId, me);
        User author = users.findById(me).orElseThrow();

        LocalDate d = LocalDate.parse(date);

        Diary diary = new Diary();
        diary.setTitle(title);
        diary.setContent(content);
        diary.setDate(d); // YYYY-MM-DD
        diary.setAuthor(author);
        diary.setRoom(r);

        LocalDate today = LocalDate.now();
        LocalDate prev = r.getLastEntryDate();

        // lastEntryDate는 가장 최근 날짜로 갱신
        if (prev == null || d.isAfter(prev)) {
            r.setLastEntryDate(d);
        }
        
        // streak 갱신(오늘 쓴 경우에만 재계산)
        if (d.isEqual(today)) {
            if (prev != null && prev.isEqual(today.minusDays(1))) {
                r.setStreak(Math.max(1, r.getStreak()) + 1);
            } 
            else if (prev != null && prev.isEqual(today)) {
                // 오늘 이미 쓴 게 있으면 그대로
            }
            else {
                r.setStreak(1);
            }
        }


        return diary;
    }

    public void deleteInRoom(Long roomId, Long me, Long diaryId) {
        requireMember(roomId, me);
        Diary d = diaries.findById(diaryId).orElseThrow();
        if (!Objects.equals(d.getAuthor().getId(), me)) {
            throw new IllegalStateException("작성자만 삭제할 수 있습니다.");
        }
        diaries.delete(d);
    }

    private Room requireMember(Long roomId, Long userId) {
        Room r = rooms.findById(roomId).orElseThrow();
        if (members.findByRoomIdAndUserId(roomId, userId).isEmpty()) {
            throw new IllegalStateException("권한 없음: 방 멤버가 아닙니다.");
        }
        return r;
    }

    public Diary updateInRoom(Long roomId, Long me, Long diaryId, String title, String content, String date) {
      requireMember(roomId, me);
      Diary d = diaries.findById(diaryId).orElseThrow();
      if (!Objects.equals(d.getAuthor().getId(), me)) {
          throw new IllegalStateException("작성자만 수정할 수 있습니다.");
      }
      if (title != null)   d.setTitle(title);
      if (content != null) d.setContent(content);
      if (date != null)    d.setDate(LocalDate.parse(date)); // YYYY-MM-DD
      return diaries.save(d);
    }
    
}




