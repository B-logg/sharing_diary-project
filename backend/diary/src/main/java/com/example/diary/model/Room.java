package com.example.diary.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  // 1:1 방인지 여부 (친구 수락 시 자동 생성되는 DM 방)
  @Column(nullable = false)
  private boolean isDirect = false;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public Room() {}

  // getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public boolean isDirect() { return isDirect; }
  public void setDirect(boolean direct) { isDirect = direct; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
