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
		//�̸��� �ߺ��� �� -1 ��ȯ
        if(videoRepository.findByName(videoDto.getName()).isPresent())
        	return new Long(-1);
        //���������� �����ͺ��̽� ���� �Ϸ� �� �ش� ���̵� ��ȯ
        System.out.println("videoDto.toEntity()");
        System.out.println(videoDto.toEntity()); 
        return videoRepository.save(videoDto.toEntity()).getId();
	}
}
