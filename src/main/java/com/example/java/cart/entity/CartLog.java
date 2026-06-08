package com.example.java.cart.entity;

import com.example.java.member.entity.Member;
import com.example.java.product.entity.Options;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_log_seq")
    @SequenceGenerator(name = "cart_log_seq", sequenceName = "cart_log_seq", allocationSize = 1)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "options_seq")
    private Options options;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "action_date", nullable = false)
    private LocalDateTime actionDate;

}
