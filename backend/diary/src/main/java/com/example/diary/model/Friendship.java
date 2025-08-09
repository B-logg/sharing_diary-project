package com.example.diary.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Friendship {
  public enum Status { PENDING, ACCEPTED, BLOCKED }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 요청 보낸 사람
  @ManyToOne(optional = false)
  @JoinColumn(name = "requester_id")
  private User requester;

  // 요청 받은 사람
  @ManyToOne(optional = false)
  @JoinColumn(name = "addressee_id")
  private User addressee;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.PENDING;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public Friendship() {}

  // getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public User getRequester() { return requester; }
  public void setRequester(User requester) { this.requester = requester; }

  public User getAddressee() { return addressee; }
  public void setAddressee(User addressee) { this.addressee = addressee; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

