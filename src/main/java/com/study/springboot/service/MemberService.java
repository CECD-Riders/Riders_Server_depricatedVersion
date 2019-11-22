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
    
    //ȸ������
    @Transactional
    public Long joinUser(MemberDto memberDto) {
        // ��й�ȣ ��ȣȭ
               BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        
        System.out.println(memberDto); 
        //���̵� �ߺ���  -1��ȯ
        if(memberRepository.findByEmail(memberDto.getEmail()).isPresent())
        	return new Long(-1);
        
        //���������� ȸ������ �Ϸ�� �� ȸ���� ���̵� ��ȯ(>=1)
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
        	throw new UsernameNotFoundException("������ ������ ã�� �� �����ϴ�.");
        MemberEntity userEntity = userEntityWrapper.get();
        System.out.println("userEntity");
        System.out.println(userEntity);
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (("admin@example.com").equals(userEmail)||("admin").equals(userEmail)||("admin@1").equals(userEmail)) {	//��й�ȣ admin���� ����
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
        }

        return new User(userEntity.getEmail(), userEntity.getPassword(), authorities);
    }
}