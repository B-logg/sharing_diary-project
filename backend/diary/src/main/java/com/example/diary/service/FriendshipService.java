package com.example.diary.service;

import com.example.diary.model.Friendship;
import com.example.diary.model.User;
import com.example.diary.repository.FriendshipRepository;
import com.example.diary.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FriendshipService {
    private final FriendshipRepository friendships;
    private final UserRepository users;
    private final RoomService roomService;

    public FriendshipService(FriendshipRepository friendships, UserRepository users, RoomService roomService) {
        this.friendships = friendships; this.users = users; this.roomService = roomService;
    }

    /** 친구 요청 보내기 (중복/역방향 처리 단순화 버전) */
    public Friendship request(Long requesterId, Long toUserId) {
        if (requesterId.equals(toUserId)) throw new IllegalStateException("자기 자신에게는 요청할 수 없습니다.");
        User req = users.findById(requesterId).orElseThrow();
        User to  = users.findById(toUserId).orElseThrow();

        // 이미 존재하면 그대로 반환(적당히 가드)
        var existing = friendships.findByRequesterAndAddressee(req, to).orElse(null);
        if (existing != null) return existing;

        Friendship f = new Friendship();
        f.setRequester(req);
        f.setAddressee(to);
        f.setStatus(Friendship.Status.PENDING);
        return friendships.save(f);
    }

    /** 내가 관련된 요청들 */
    @Transactional(readOnly = true)
    public List<Friendship> myRequests(Long me) {
        User u = users.findById(me).orElseThrow();
        return friendships.findByRequesterOrAddressee(u, u);
    }

    /** 수락 → ACCEPTED로 바꾸고 1:1 룸 확보 */
    public void accept(Long me, Long friendshipId) {
        Friendship f = friendships.findById(friendshipId).orElseThrow();
        // 수락 권한(받은 사람만)
        if (!f.getAddressee().getId().equals(me)) {
            throw new IllegalStateException("수락 권한이 없습니다.");
        }
        f.setStatus(Friendship.Status.ACCEPTED);
        friendships.save(f);

        Long a = f.getRequester().getId();
        Long b = f.getAddressee().getId();
        roomService.ensureDirectRoom(a, b);
    }
}

