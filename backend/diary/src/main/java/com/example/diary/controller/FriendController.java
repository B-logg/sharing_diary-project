package com.example.diary.controller;

import com.example.diary.model.Friendship;
import com.example.diary.service.FriendshipService;
import com.example.diary.util.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    private final FriendshipService service;

    public FriendController(FriendshipService service) { this.service = service; }

    // DTO
    public record FriendshipRes(Long id, Long requesterId, Long addresseeId, String status) {
        static FriendshipRes of(Friendship f) {
            return new FriendshipRes(f.getId(), f.getRequester().getId(), f.getAddressee().getId(), f.getStatus().name());
        }
    }
    public record RequestReq(Long toUserId) {}

    /** 친구 요청 보내기 */
    @PostMapping("/requests")
    public ResponseEntity<FriendshipRes> request(@RequestBody RequestReq req) {
        Long me = Auth.currentUserId();
        var f = service.request(me, req.toUserId());
        return ResponseEntity.ok(FriendshipRes.of(f));
    }

    /** 내가 관련된 요청 목록(보낸/받은) */
    @GetMapping("/requests")
    public List<FriendshipRes> myRequests() {
        Long me = Auth.currentUserId();
        return service.myRequests(me).stream().map(FriendshipRes::of).toList();
    }

    /** 수락(받은 사람만 가능) → 1:1 룸 자동 생성 */
    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        Long me = Auth.currentUserId();
        service.accept(me, id);
        return ResponseEntity.ok(Map.of("message", "친구가 되었습니다."));
    }
}

