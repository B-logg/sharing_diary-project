package com.example.diary.controller;

import com.example.diary.model.Room;
import com.example.diary.service.RoomService;
import com.example.diary.util.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // DTOs
    public record RoomRes(Long id, String name, boolean direct) {
        public static RoomRes of(Room r) { return new RoomRes(r.getId(), r.getName(), r.isDirect()); }
    }
    public record CreateRoomReq(String name, List<Long> memberIds) {}

    @GetMapping
    public List<RoomRes> myRooms() {
        Long me = Auth.currentUserId();
        return roomService.findRoomsOf(me).stream().map(RoomRes::of).toList();
    }

    @PostMapping
    public ResponseEntity<RoomRes> create(@RequestBody CreateRoomReq req) {
        Long me = Auth.currentUserId();
        Room r = roomService.createGroupRoom(me, req.name(), req.memberIds());
        return ResponseEntity.ok(RoomRes.of(r));
    }
}
