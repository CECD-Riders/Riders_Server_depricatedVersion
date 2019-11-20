package com.study.springboot.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

    // 메인 페이지
    @GetMapping("/")
    public String index(Model model) {
    	Calendar cal = Calendar.getInstance();
        String url = "https://sports.news.naver.com/basketball/schedule/index.nhn?"
        		+ "year=" + cal.get(Calendar.YEAR) 
        		+"&month=" + (cal.get(Calendar.MONTH) + 1) 
        		+"&category=nba#";

        Document doc = null;
        List<DayGame> gameList = new ArrayList<DayGame>();
        
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements todayGame = doc.select("div.sch_tb2.selected");
        //System.out.println(todayGame);
        for(Element eg: todayGame) {
        	List<Game> list = new ArrayList<Game>();
        	String date = eg.select("span.td_date").text();		//경기 날짜     	
        	for(Element g: eg.select("tr")) {
        		list.add(new Game(g.select("span.td_hour").text(),
        				g.select("span.team_lft").text(),
        				g.select("span.team_rgt").text(),
        				g.select("strong.td_score").text(),
        				g.select("span.td_stadium").text()));
        	}
        	gameList.add(new DayGame(date,list));
        }
        System.out.println(gameList.get(0).getGameList().size());
        model.addAttribute("gameSize", gameList.get(0).getGameList().size() + 1);
        model.addAttribute("todayGame", gameList.get(0));
        return "/index";
    }
    
    @RequestMapping("/watch")
    public String watchVideo() {
    	return "/watchVideo";
    }
    
    @RequestMapping("/gameList")
    public String gameList() {
    	return "/scheduleAndresults";
    }
    
    // 회원가입 페이지
    @GetMapping("/user/signup")
    public String dispSignup() {
        return "/signup";
    }

    // 회원가입 처리
    @PostMapping("/user/signup")
    public String execSignup(HttpServletRequest request, MemberDto memberDto, Model model) {
    	if(memberDto.getEmail().isEmpty()) {
    		model.addAttribute("errorMsg", "아이디를 입력해 주십시오.");
    		return "/signup";
    	}else if(memberDto.getPassword().isEmpty()) {
    		model.addAttribute("errorMsg", "비밀번호를 입력해 주십시오.");
    		return "/signup";
    	}
        Long id = memberService.joinUser(memberDto);
        if(id == -1) {//아이디 중복인 경우 => 다시 회원가입 창으로 보내주고 아이디 중복이라고 말해주자
        	model.addAttribute("errorMsg", "중복된 아이디가 있습니다!");
        	System.out.println("YES");
        	return "/signup";
        }
        else
        	return "redirect:/user/login";
    }

    // 로그인 페이지
	@RequestMapping(value = "/user/login"/* ,method = {RequestMethod.GET , RequestMethod.POST} */)
    public String dispLogin(HttpServletRequest request ,Model model) {
    	String error = request.getParameter("error");
    	if(error != null) {//로그인 실패 상황
    		model.addAttribute("loginFailureError", "로그인 실패");
    	}
        return "/login";
    }

    // 접근 거부 페이지
    @GetMapping("/user/denied")
    public String dispDenied() {
        return "/denied";
    }

    // 내 정보 페이지
    @GetMapping("/user/info")
    public String dispMyInfo() {
        return "/myinfo";
    }

    // 어드민 페이지
    @GetMapping("/admin")
    public String dispAdmin() {
        return "/admin";
    }
}