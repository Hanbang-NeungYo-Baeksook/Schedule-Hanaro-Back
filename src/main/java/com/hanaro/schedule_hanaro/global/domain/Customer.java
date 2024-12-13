package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDate;

import com.hanaro.schedule_hanaro.global.domain.enums.Gender;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Customer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 100, name = "customer_id")
    private Long id;

    @Column(name = "auth_id", nullable = false)
    private String authId;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 11, name = "phone",nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder
    public Customer (
        String authId,
        String password,
        String name,
        String phoneNum,
        LocalDate birth,
        Gender gender
    ){
        this.authId = authId;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
    }
}
