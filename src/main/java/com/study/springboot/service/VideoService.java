package com.study.springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.springboot.domain.VideoRepository;
import com.study.springboot.dto.VideoDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VideoService {
	private VideoRepository videoRepository;
	
	@Transactional
	public Long SaveSingeVideo(VideoDto videoDto) {
        
        System.out.println(videoDto); 
        System.out.println(videoDto.getName());
		//이름이 중복될 시 -1 반환
        if(videoRepository.findByName(videoDto.getName()).isPresent())
        	return new Long(-1);
        //성공적으로 데이터베이스 저장 완료 시 해당 아이디 반환
        System.out.println("videoDto.toEntity()");
        System.out.println(videoDto.toEntity()); 
        return videoRepository.save(videoDto.toEntity()).getId();
	}
}
