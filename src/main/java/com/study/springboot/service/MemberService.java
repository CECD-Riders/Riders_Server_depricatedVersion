package com.study.springboot.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.springboot.domain.MemberEntity;
import com.study.springboot.domain.MemberRepository;
import com.study.springboot.domain.Role;
import com.study.springboot.dto.MemberDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    private MemberRepository memberRepository;

    @Transactional
    public Long overlapCheck(String email) {
    	if(memberRepository.findByEmail(email).isPresent())
        	return new Long(-1);
    	else
    		return new Long(1);
    }
    
    //회원가입
    @Transactional
    public Long joinUser(MemberDto memberDto) {
        // 비밀번호 암호화
               BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        
        System.out.println(memberDto); 
        //아이디 중복시  -1반환
        if(memberRepository.findByEmail(memberDto.getEmail()).isPresent())
        	return new Long(-1);
        
        //성공적으로 회원가입 완료시 그 회원의 아이디 반환(>=1)
        return memberRepository.save(memberDto.toEntity()).getId();
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    	System.out.println("userEmail");
    	System.out.println(userEmail);
        Optional<MemberEntity> userEntityWrapper = memberRepository.findByEmail(userEmail);
        System.out.println("userEntityWrapper");
        System.out.println(userEntityWrapper);
        if(!userEntityWrapper.isPresent())
        	throw new UsernameNotFoundException("접속자 정보를 찾을 수 없습니다.");
        MemberEntity userEntity = userEntityWrapper.get();
        System.out.println("userEntity");
        System.out.println(userEntity);
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (("admin@example.com").equals(userEmail)||("admin").equals(userEmail)||("admin@1").equals(userEmail)) {	//비밀번호 admin으로 저장
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
        }

        return new User(userEntity.getEmail(), userEntity.getPassword(), authorities);
    }
}