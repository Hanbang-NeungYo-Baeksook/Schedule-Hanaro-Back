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

    private String customerName;
    private String phoneNum;

    public static Customer of(
        String customerName,
        String phoneNum
    ){
        return Customer.builder()
            .customerName(customerName)
            .phoneNum(phoneNum)
            .build();
    }
}
