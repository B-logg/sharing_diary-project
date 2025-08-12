// controller/RoomPlantController.java
package com.example.diary.controller;

import com.example.diary.model.Room;
import com.example.diary.repository.RoomRepository;
import com.example.diary.repository.RoomMemberRepository;
import com.example.diary.util.Auth;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/{roomId}/plant")
public class RoomPlantController {
  private final RoomRepository rooms;
  private final RoomMemberRepository members;

  public RoomPlantController(RoomRepository rooms, RoomMemberRepository members) {
    this.rooms = rooms; this.members = members;
  }

  @GetMapping
  public Map<String, Object> getPlant(@PathVariable Long roomId) {
    Long me = Auth.currentUserId();
    if (members.findByRoomIdAndUserId(roomId, me).isEmpty())
      throw new IllegalStateException("권한 없음");

    Room r = rooms.findById(roomId).orElseThrow();
    LocalDate today = LocalDate.now();
    LocalDate last = r.getLastEntryDate();

    long daysMissed = (last == null) ? 999 : Math.max(0, ChronoUnit.DAYS.between(last, today));
    int health = (int)Math.max(0, 100 - daysMissed * 20); // 하루 20씩 감소

    String status = health >= 70 ? "THRIVING"
                  : health >= 40 ? "OK"
                  : health >= 10 ? "WILTING"
                  : "DEAD";

    return Map.of(
      "health", health,                // 0~100
      "status", status,                // 문자열 상태
      "daysMissed", daysMissed,        // 마지막 작성 이후 며칠 지났는지
      "lastEntryDate", last == null ? null : last.toString(),
      "streak", r.getStreak()
    );
  }
}
