package com.example.diary.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Diary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private LocalDate date;

  // 작성자
  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  // 소속 룸
  @ManyToOne(optional = false)
  @JoinColumn(name = "room_id")
  private Room room;

  public Diary() {}

  // getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }

  public LocalDate getDate() { return date; }
  public void setDate(LocalDate date) { this.date = date; }

  public User getAuthor() { return author; }
  public void setAuthor(User author) { this.author = author; }

  public Room getRoom() { return room; }
  public void setRoom(Room room) { this.room = room; }
}
