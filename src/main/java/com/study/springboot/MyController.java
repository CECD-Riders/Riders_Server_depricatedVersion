package com.study.springboot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.study.springboot.crawling.DayGame;
import com.study.springboot.crawling.Game;
import com.study.springboot.dto.MemberDto;
import com.study.springboot.service.MemberService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MyController {
    private MemberService memberService;

    // ���� ������
    @GetMapping("/")
    public String index(Model model) {
        String url = "https://sports.news.naver.com/basketball/schedule/index.nhn?year=2019&month=11&category=nba#";
        Document doc = null;
        List<DayGame> gameList = new ArrayList<DayGame>();
        
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements evenGames = doc.select("div.sch_tb2");
        for(Element eg: evenGames) {
        	List<Game> list = new ArrayList<Game>();
        	String date = eg.select("span.td_date").text();		//��� ��¥     	
        	System.out.println("---------------------------<��¥ : " + date+ ">---------------------------");
        	for(Element g: eg.select("tr")) {
        		String gameInfo = "��� �ð� : " 
        				+g.select("span.td_hour").text() +" ===> "
        				+ g.select("span.team_lft").text() + " " 
        				+ g.select("strong.td_score").text() + " " 
        				+g.select("span.team_rgt").text();
        		System.out.println(gameInfo);
        		
        		list.add(new Game(g.select("span.td_hour").text(),
        				g.select("span.team_lft").text(),
        				g.select("span.team_rgt").text(),
        				g.select("strong.td_score").text()));
        	}
        	gameList.add(new DayGame(date,list));
        }
        model.addAttribute("MonthGame", gameList);
        return "/index";
    }
    
    @RequestMapping("/watch")
    public String watchVideo() {
    	return "/watchVideo";
    }
    
    // ȸ������ ������
    @GetMapping("/user/signup")
    public String dispSignup() {
        return "/signup";
    }

    // ȸ������ ó��
    @PostMapping("/user/signup")
    public String execSignup(MemberDto memberDto, Model model) {
        Long id = memberService.joinUser(memberDto);
        System.out.println(id);
        if(id == -1) {//���̵� �ߺ��� ��� => �ٽ� ȸ������ â���� �����ְ� ���̵� �ߺ��̶�� ��������
        	model.addAttribute("idOverlap", "�ߺ��� ���̵� �ֽ��ϴ�!");
        	System.out.println("YES");
        	return "/signup";
        }
        else
        	return "redirect:/user/login";
    }

    // �α��� ������
	@RequestMapping(value = "/user/login"/* ,method = {RequestMethod.GET , RequestMethod.POST} */)
    public String dispLogin(HttpServletRequest request ,Model model) {
    	String error = request.getParameter("error");
    	if(error != null) {//�α��� ���� ��Ȳ
    		model.addAttribute("loginFailureError", "�α��� ����");
    	}
        return "/security/login";
    }

    // �α��� ��� ������
    @GetMapping("/user/login/result")
    public String dispLoginResult() {
        return "/loginSuccess";
    }

    // �α׾ƿ� ��� ������
    @GetMapping("/user/logout/result")
    public String dispLogout() {
        return "/logout";
    }

    // ���� �ź� ������
    @GetMapping("/user/denied")
    public String dispDenied() {
        return "/denied";
    }

    // �� ���� ������
    @GetMapping("/user/info")
    public String dispMyInfo() {
        return "/myinfo";
    }

    // ���� ������
    @GetMapping("/admin")
    public String dispAdmin() {
        return "/admin";
    }
}