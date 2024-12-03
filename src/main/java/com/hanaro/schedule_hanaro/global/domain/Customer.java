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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "auth_id")
    private String authId;
    private String password;
    private String name;
    private String phoneNum;
    private LocalDate birth;
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
