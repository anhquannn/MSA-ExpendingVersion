package com.market.MSA.models.others;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "device_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  Long userId;

  @Column(length = 256, unique = true, nullable = false)
  String token;

  @Enumerated(EnumType.STRING)
  Platform platform;

  LocalDateTime createdAt;
}
