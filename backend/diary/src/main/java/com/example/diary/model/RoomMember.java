package com.example.diary.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@IdClass(RoomMemberId.class)
public class RoomMember {

  @Id
  @ManyToOne(optional = false)
  @JoinColumn(name = "room_id")
  private Room room;

  @Id
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String role = "MEMBER"; // OWNER/MEMBER 등

  @Column(nullable = false)
  private LocalDateTime joinedAt = LocalDateTime.now();

  public RoomMember() {}

  // getters & setters
  public Room getRoom() { return room; }
  public void setRoom(Room room) { this.room = room; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }

  public LocalDateTime getJoinedAt() { return joinedAt; }
  public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
