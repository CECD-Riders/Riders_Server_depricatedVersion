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
		//�̸��� �ߺ��� �� -1 ��ȯ
        if(videoRepository.findByName(videoDto.getName()).isPresent())
        	return new Long(-1);
        //���������� �����ͺ��̽� ���� �Ϸ� �� �ش� ���̵� ��ȯ
        System.out.println("videoDto.toEntity()");
        System.out.println(videoDto.toEntity()); 
        return videoRepository.save(videoDto.toEntity()).getId();
	}
	
	//���� �̸����� �÷��ϳ� �����
	@Transactional
	public void DeleteSingleVideo(VideoDto videoDto) {
		System.out.println(videoDto); 
        System.out.println(videoDto.getName());
        //�̹� ������ �����ͺ��̽��� �������� �����
        if(videoRepository.findByName(videoDto.getName()).isPresent())
        	videoRepository.deleteByName(videoDto.getName());
	}
	
	@Transactional
	public int VideoOverlapCheck(String videoName) {
		Optional<VideoEntity> videoEntity = videoRepository.findByName(videoName);
		if(videoEntity.isPresent())
			return 1;	//������ 1
		else
			return -1;	//������ -1
			
	}
}
