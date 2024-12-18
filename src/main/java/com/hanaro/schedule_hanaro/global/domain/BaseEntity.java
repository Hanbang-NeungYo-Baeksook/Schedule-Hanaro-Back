package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseEntity {

	@Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
	@org.hibernate.annotations.Comment("등록일시")
	@ColumnDefault("CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = false)
	@org.hibernate.annotations.Comment("수정일시")
	@ColumnDefault("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;
}
