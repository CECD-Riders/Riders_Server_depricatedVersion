package com.study.springboot.dto;

import com.study.springboot.domain.VideoEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VideoDto {
	private Long id;
	private String name;
	private Long like;
	
	public VideoEntity toEntity() {
		return VideoEntity.builder()
				.id(id)
				.name(name)
				.like(like)
				.build();
	}
	
	@Builder
	public VideoDto(Long id, String name, Long like) {
		this.id = id;
		this.name = name;
		this.like = like;
	}
}
