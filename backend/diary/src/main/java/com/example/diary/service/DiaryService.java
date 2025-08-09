package com.example.diary.service;

import com.example.diary.model.*;
import com.example.diary.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        Diary d = new Diary();
        d.setTitle(title);
        d.setContent(content);
        d.setDate(LocalDate.parse(date)); // YYYY-MM-DD
        d.setAuthor(author);
        d.setRoom(r);
        return diaries.save(d);
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




