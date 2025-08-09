package com.example.diary.controller;

import com.example.diary.model.Diary;
import com.example.diary.service.DiaryService;
import com.example.diary.util.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomId}/diaries")
public class DiaryInRoomController {

    private final DiaryService diaryService;
    public DiaryInRoomController(DiaryService diaryService) { this.diaryService = diaryService; }

    public record CreateReq(String title, String content, String date) {}
    public record DiaryRes(Long id, String title, String content, String date, Long authorId) {
        public static DiaryRes of(Diary d) {
            return new DiaryRes(
                d.getId(), d.getTitle(), d.getContent(),
                d.getDate()==null?null:d.getDate().toString(),
                d.getAuthor()==null?null:d.getAuthor().getId()
            );
        }
    }

    @GetMapping
    public List<DiaryRes> list(@PathVariable Long roomId) {
        Long me = Auth.currentUserId();
        return diaryService.listInRoom(roomId, me).stream().map(DiaryRes::of).toList();
    }

    @PostMapping
    public ResponseEntity<DiaryRes> create(@PathVariable Long roomId, @RequestBody CreateReq req) {
        Long me = Auth.currentUserId();
        var d = diaryService.createInRoom(roomId, me, req.title(), req.content(), req.date());
        return ResponseEntity.ok(DiaryRes.of(d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long roomId, @PathVariable Long id) {
        Long me = Auth.currentUserId();
        diaryService.deleteInRoom(roomId, me, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaryRes> update(@PathVariable Long roomId, @PathVariable Long id,
                                       @RequestBody CreateReq req) {
        Long me = Auth.currentUserId();
        var d = diaryService.updateInRoom(roomId, me, id, req.title(), req.content(), req.date());
        return ResponseEntity.ok(DiaryRes.of(d));
    }

}
