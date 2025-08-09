package com.example.diary.repository;

import com.example.diary.model.Diary;
import com.example.diary.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByRoomOrderByDateDesc(Room room);
}
