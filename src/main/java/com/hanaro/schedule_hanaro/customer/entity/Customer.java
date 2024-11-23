package com.hanaro.schedule_hanaro.customer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "id")
    private String authId;
    private String password;
    private String name;
    private String phoneNum;

    public static Customer of(
        String name,
        String phoneNum
    ){
        return Customer.builder()
            .name(name)
            .phoneNum(phoneNum)
            .build();
    }
}
