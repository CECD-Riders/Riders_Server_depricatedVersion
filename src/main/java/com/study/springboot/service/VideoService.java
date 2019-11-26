package com.study.springboot.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.springboot.domain.VideoEntity;
import com.study.springboot.domain.VideoRepository;
import com.study.springboot.dto.VideoDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VideoService {
	private VideoRepository videoRepository;

	@Transactional
	public Long SaveSingleVideo(VideoDto videoDto) {
        
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
	
	//비디오 이름으로 컬럼하나 지우기
	@Transactional
	public void DeleteSingleVideo(VideoDto videoDto) {
		System.out.println(videoDto); 
        System.out.println(videoDto.getName());
        //이미 비디오가 데이터베이스에 있을때만 지우기
        if(videoRepository.findByName(videoDto.getName()).isPresent())
        	videoRepository.deleteByName(videoDto.getName());
	}
	
	@Transactional
	public int VideoOverlapCheck(String videoName) {
		Optional<VideoEntity> videoEntity = videoRepository.findByName(videoName);
		if(videoEntity.isPresent())
			return 1;	//있으면 1
		else
			return -1;	//없으면 -1
			
	}
}
