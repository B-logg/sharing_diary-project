package com.example.diary.repository;

import com.example.diary.model.Friendship;
import com.example.diary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByRequesterOrAddressee(User requester, User addressee);
    Optional<Friendship> findByRequesterAndAddressee(User r, User a);
}
