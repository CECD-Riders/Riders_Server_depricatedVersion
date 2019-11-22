package com.study.springboot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SequenceGenerator(name = "VIDEO_SEQ_GENERATOR", sequenceName = "VIDEO_SEQ", initialValue = 1, allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "video")
public class VideoEntity {
	@Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE , generator="VIDEO_SEQ_GENERATOR")
    private Long id;
	
	@Column(name = "videoname", length = 100, nullable = false)
    private String name;
	
	@Column(name = "videolike",nullable = false)
    private Long like;
	
	@Builder
	public VideoEntity(Long id, String name, Long like) {
		this.id = id;
		this.name = name;
		this.like = like;
	}
}






