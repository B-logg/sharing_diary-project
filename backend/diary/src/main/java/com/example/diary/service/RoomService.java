package com.example.diary.service;

import com.example.diary.model.*;
import com.example.diary.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RoomService {
    private final RoomRepository rooms;
    private final RoomMemberRepository members;
    private final UserRepository users;

    public RoomService(RoomRepository rooms, RoomMemberRepository members, UserRepository users) {
        this.rooms = rooms; this.members = members; this.users = users;
    }

    /** 내가 속한 방 목록 */
    @Transactional(readOnly = true)
    public List<Room> findRoomsOf(Long userId) {
        User me = users.findById(userId).orElseThrow();
        return members.findByUser(me).stream().map(RoomMember::getRoom).toList();
    }

    /** 방 멤버 여부 강제 확인 (없으면 예외) */
    @Transactional(readOnly = true)
    public Room requireMember(Long roomId, Long userId) {
        Room r = rooms.findById(roomId).orElseThrow();
        if (members.findByRoomIdAndUserId(roomId, userId).isEmpty()) {
            throw new IllegalStateException("권한 없음: 방 멤버가 아닙니다.");
        }
        return r;
    }

    /** 그룹 방 생성 (나 + 주어진 멤버들) */
    public Room createGroupRoom(Long ownerId, String name, Collection<Long> memberIds) {
        Room r = new Room();
        r.setName((name == null || name.isBlank()) ? "새 룸" : name);
        r.setDirect(false);
        rooms.save(r);

        Set<Long> ids = new HashSet<>(memberIds == null ? List.of() : memberIds);
        ids.add(ownerId);

        for (Long uid : ids) {
            User u = users.findById(uid).orElseThrow();
            RoomMember rm = new RoomMember();
            rm.setRoom(r);
            rm.setUser(u);
            rm.setRole(Objects.equals(uid, ownerId) ? "OWNER" : "MEMBER");
            members.save(rm);
        }
        return r;
    }

    /** 두 사용자 사이의 1:1(direct) 룸을 찾아보고, 없으면 생성 후 반환 */
    public Room ensureDirectRoom(Long aId, Long bId) {
        Room existing = findDirectRoomBetween(aId, bId);
        if (existing != null) return existing;

        Room r = new Room();
        r.setName("DM:" + aId + "-" + bId);
        r.setDirect(true);
        rooms.save(r);

        for (Long uid : List.of(aId, bId)) {
            User u = users.findById(uid).orElseThrow();
            RoomMember rm = new RoomMember();
            rm.setRoom(r); rm.setUser(u); rm.setRole("MEMBER");
            members.save(rm);
        }
        return r;
    }

    /** 두 사용자 모두 멤버로 들어있는 direct 룸을 찾는다(없으면 null) */
    @Transactional(readOnly = true)
    public Room findDirectRoomBetween(Long aId, Long bId) {
        // a의 룸들 중 direct인 것만 모으고, 그 중 b도 멤버인 룸이 있는지 확인
        var aRooms = members.findByUserId(aId).stream().map(RoomMember::getRoom).toList();
        for (Room r : aRooms) {
            if (!r.isDirect()) continue;
            if (members.findByRoomIdAndUserId(r.getId(), bId).isPresent()) return r;
        }
        return null;
    }
}

