package com.study.springboot.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import com.study.springboot.FTP.FTPHostInfo;
import com.study.springboot.FTP.FTPUploader;
import com.study.springboot.crawling.DayGame;
import com.study.springboot.crawling.Game;
import com.study.springboot.dto.MemberDto;
import com.study.springboot.dto.VideoDto;
import com.study.springboot.service.MemberService;
import com.study.springboot.service.VideoService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MyController {
    private MemberService memberService;
    private VideoService videoService;
    
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
        Elements todayGame = doc.select("div.selected");
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
        model.addAttribute("gameSize", gameList.get(0).getGameList().size() + 1);
        model.addAttribute("todayGame", gameList.get(0));
        return "/index";
    }
    
    @RequestMapping("/watch")
    public String watchVideo(HttpServletRequest request, Model model) {
    	Calendar cal = Calendar.getInstance();
    	String useDate = Integer.toString(cal.get(Calendar.YEAR));
    	String date = request.getParameter("date");
    	String dateP1[] = date.split("\\s+");
    	String dateP2[] = dateP1[0].split("\\.");
    	
    	String month = dateP2[0]; 
    	String day = dateP2[1];
    	if(month.length()==1) 
    		month = "0"+month;
    	if(day.length()==1)
    		day = "0"+day;
    	useDate = useDate + month + day;
    	
    	String leftTeam = request.getParameter("leftTeam");
    	String rightTeam = request.getParameter("rightTeam");
    	model.addAttribute("date", useDate);
    	try {
        	model.addAttribute("leftTeam", URLEncoder.encode(leftTeam, "UTF-8"));
			model.addAttribute("rightTeam", URLEncoder.encode(rightTeam, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println(useDate + "-" +leftTeam + "-" + rightTeam + ".mp4");
    	return "/watchVideo";
    }
    
    @RequestMapping("/gameList")
    public String gameList(Model model) {
    	Calendar cal = Calendar.getInstance();
        String url = "https://sports.news.naver.com/basketball/schedule/index.nhn?"
        		+ "year=" + cal.get(Calendar.YEAR) 
        		+"&month=" + (cal.get(Calendar.MONTH) + 1) 
        		+"&category=nba#";
        System.out.println(url);
        Document doc = null;
        List<DayGame> gameList = new ArrayList<DayGame>();     
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements oddGame = doc.select("div.sch_tb");
        Elements evenGame = doc.select("div.sch_tb2");
        for(int i = 0; i <oddGame.size() + evenGame.size();i++ ) {
        	List<Game> list = new ArrayList<Game>();
        	if(i%2 == 0) {	//even
        		String date = oddGame.get(i/2).select("span.td_date").text();
        		for(Element g: oddGame.get(i/2).select("tr")) {
            		list.add(new Game(g.select("span.td_hour").text(),
            				g.select("span.team_lft").text(),
            				g.select("span.team_rgt").text(),
            				g.select("strong.td_score").text(),
            				g.select("span.td_stadium").text()));
        		}
            		gameList.add(new DayGame(date,list));
        	}else {			//odd
        		String date = evenGame.get(i/2).select("span.td_date").text();
        		for(Element g: evenGame.get(i/2).select("tr")) {
            		list.add(new Game(g.select("span.td_hour").text(),
            				g.select("span.team_lft").text(),
            				g.select("span.team_rgt").text(),
            				g.select("strong.td_score").text(),
            				g.select("span.td_stadium").text()));
        		}
            		gameList.add(new DayGame(date,list));
        	}
        }
        
        model.addAttribute("GameList", gameList);
    	return "/calendar";
    }
    
    // 회원가입 페이지
    @GetMapping("/user/signup")
    public String dispSignup() {
        return "/signup";
    }

    // 회원가입 처리
    @PostMapping("/user/signup")
    public String SignupAction(HttpServletRequest request, MemberDto memberDto, Model model) {
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

    // 영상전송 페이지
    @GetMapping("/admin/videoUpload")
    public String videoUpload() {
        return "/videoUpload";
    }
    
    // 영상전송 수행
    @PostMapping("/admin/videoUpload")
    public String videoUploadAction(HttpServletRequest request ,Model model) {
    	
    	String localPath = request.getParameter("path");				//로컬 경로 
    	String HostfileName = request.getParameter("HostfileName");		//호스트 서버에 저장될 파일 이름
    	VideoDto videoDto = new VideoDto();
    	videoDto.setName(HostfileName);
    	videoDto.setLike(new Long(0));
    	FTPUploader ftpUploader;
		try {
	        Long id = videoService.SaveSingleVideo(videoDto);
	        FTPHostInfo hostInfo = new FTPHostInfo();
			ftpUploader = new FTPUploader(hostInfo.hostIP, hostInfo.ID, hostInfo.PW);
	        ftpUploader.uploadFile(localPath, HostfileName, "/ridersTest/");
	        ftpUploader.disconnect();
	        if(id == -1) {
	        	throw new Exception();
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			videoService.DeleteSingleVideo(videoDto);
			e.printStackTrace();
			model.addAttribute("succesMsg", "영상전송 실패!");
			return "/videoUpload";
		}
		
    	model.addAttribute("succesMsg", "영상전송 성공!");
    	return "/videoUpload";
    }
    
}