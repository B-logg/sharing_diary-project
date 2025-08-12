package com.example.diary.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

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

  @Column(nullable = false)
  private LocalDate lastEntryDate; // 마지막 일기 날짜(YYYY-MM-DD)

  @Column(nullable = false)
  private int streak; // 연속 기록 일수

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

  public LocalDate getLastEntryDate() { return lastEntryDate ;}
  public void setLastEntryDate(LocalDate d) { this.lastEntryDate = d; }
  public int getStreak() { return streak; }
  public void setStreak(int streak) { this.streak = streak; }
}
