package kr.nadeuli.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "block")
public class Block {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "block_id", nullable = false)
  private Long id;

  @Column(name = "block_reason", length = 1000, nullable = false)
  private String blockReason;

  @Column(name = "block_end", nullable = false)
  private LocalDateTime blockEnd;

  //영구정지 -1
  @Column(name = "block_day", length = 20, nullable = false)
  private Long blockDay;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "block_member_tag", nullable = false)
  private Member blockMember;
}