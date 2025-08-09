package com.example.diary.repository;

import com.example.diary.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoomMemberRepository extends JpaRepository<RoomMember, RoomMemberId> {
    List<RoomMember> findByUser(User user);
    List<RoomMember> findByRoom(Room room);
    boolean existsByRoomAndUser(Room room, User user);
    Optional<RoomMember> findByRoomIdAndUserId(Long roomId, Long userId);
    List<RoomMember> findByUserId(Long userId);
}
