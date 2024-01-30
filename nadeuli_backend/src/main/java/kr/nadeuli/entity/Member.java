package kr.nadeuli.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import kr.nadeuli.category.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString(exclude = {"blocks", "bankAccounts", "reports", "oriScheMemChatFavs",
    "sellerProducts", "buyerProducts", "tradeReviews", "nadeuliPayHistories", "posts",
    "comments", "nadeuliDeliveries","nadeulibuyers"})
@Table(name="member")
public class Member implements UserDetails {
  //1. Security 유저정보를 구현한 회원 Entity

  @Id
  @Column(name = "tag", length = 20, nullable = false)
  private String tag;

  @Column(name = "cellphone", length = 20, nullable = false)
  private String cellphone;

  @Column(name = "nickname", length = 40, nullable = false)
  private String nickname;

  @Column(name = "affinity", columnDefinition = "BIGINT DEFAULT 0")
  private Long affinity;

  @Column(name = "email")
  private String email;

  @Column(name = "dong_ne", nullable = false)
  private String dongNe;

  @Column(name = "picture", columnDefinition = "VARCHAR(10000) DEFAULT 'https://kr.object.ncloudstorage.com/nadeuli/image/nadeuli20231221213746683.png'")

  private String picture;

  @Column(name = "nadeuli_pay_balance", columnDefinition = "BIGINT DEFAULT 0")
  private Long nadeuliPayBalance;

  @Column(name = "is_activate", columnDefinition = "BIT DEFAULT 0")
  private boolean isActivate;

  @Column(name = "is_nadeuli_delivery", columnDefinition = "BIT DEFAULT 0")
  private boolean isNadeuliDelivery;

  @Column(name = "role",columnDefinition = "BIGINT DEFAULT 0 CHECK (role BETWEEN 0 AND 1)")
  private Role role;

  @Column(name = "gu", nullable = false)
  private String gu;

  @Column(name = "bank_name")
  private String bankName;

  @Column(name = "bank_account_num")
  private String bankAccountNum;

  @Column(name = "block_reason", length = 1000)
  private String blockReason;

  @Column(name = "block_end")
  private LocalDateTime blockEnd;

  @Column(name = "block_day")
  private Long blockDay;

  // 소셜로그인 식별자 값 (자체 로그인인 경우 null   )
  @Column(name = "social_id")
  private String socialId;

  //정지
  @OneToMany(mappedBy = "blockMember", fetch = FetchType.LAZY)
  private List<Block> blocks;

  //계좌
  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
  private List<BankAccount> bankAccounts;

  //신고
  @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY)
  private List<Report> reports;

  //오리스케멤챗페브
  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
  private List<OriScheMemChatFav> oriScheMemChatFavs;

  //상품
  @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
  private List<Product> sellerProducts;

  @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY)
  private List<Product> buyerProducts;

  //거래후기
  @OneToMany(mappedBy = "trader", fetch = FetchType.LAZY)
  private List<TradeReview> tradeReviews;

  //나드리페이 내역
  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
  private List<NadeuliPayHistory> nadeuliPayHistories;

  //게시물
  @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
  private List<Post> posts;

  //댓글
  @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
  private List<Comment> comments;

  //나드리 부름
  @OneToMany(mappedBy = "deliveryPerson", fetch = FetchType.LAZY)
  private List<NadeuliDelivery> nadeuliDeliveries;

  @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY)
  private List<NadeuliDelivery> nadeulibuyers;

  //JWT UserDetails OverRiding
  //권한을 가져올 메소드
  //단순권한부여를 리스트로 반환
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  //비밀번호는 없기때문에 null로 설정
  @Override
  public String getPassword() {
    return null;
  }

  //식별자를 가져올 메소드
  @Override
  public String getUsername() {
    return tag;
  }

  //만료되지않은 계정, true로해야 계정이 만료안됨
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  //계정이 잠겨있지않아야한다, true유지
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  //자격증명이 만료되지않았음을 뜻함, true유지
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  //사용가능한계정을 뜻함, true 유지
  @Override
  public boolean isEnabled() {
    return true;
  }

}
