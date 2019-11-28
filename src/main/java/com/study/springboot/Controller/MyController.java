package com.study.springboot.Controller;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.study.springboot.FTP.FTPHostInfo;
import com.study.springboot.FTP.FTPUploader;
import com.study.springboot.crawling.Crawler;
import com.study.springboot.crawling.DayGame;
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
    private Crawler crawler;
    
    @RequestMapping("/leftSidebar.html")
    public String loadSidebar() {
    	return "/leftSidebar";
    }
    
    @RequestMapping("/headers.html")
    public String loadHeader() {
    	return "/headers";
    }
    
    //메인
    @GetMapping("/")
    public String index(Model model) {
        DayGame dayGame = crawler.GetTodayGame();
        
        model.addAttribute("gameSize", dayGame.getGameList().size() + 1);
        model.addAttribute("todayGame", dayGame);
        return "/index";
    }
    
    //일정 및 결과
    @RequestMapping("/gameList")
    public String gameList(Model model) {
        List<DayGame> monthGame = crawler.GetMonthGame();
        
        model.addAttribute("GameList", monthGame);
    	return "/calendar";
    }
    
    //기록,순위
    @RequestMapping("/rank")
    public String rank() {
    	return "/rank";
    }
    
    //영상 시청
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
    	String videoName = useDate + "-" +leftTeam + "-" + rightTeam + ".mp4";
    	System.out.println(videoName);
    	//여기서 영상이 있는지 없는지 데이터 베이스 조회
    	if(videoService.VideoOverlapCheck(videoName) == 1)
    		return "/watchVideo";
    	else
    		return "/noVideo";	//없으면 준비중 페이지로 이동
    }
    
    
    
    // 회원가입 페이지
    @GetMapping("/user/signup")
    public String dispSignup() {
        return "/signup";
    }

    // 회원가입 처리
    @PostMapping("/user/signup")
    public String SignupAction(HttpServletRequest request, MemberDto memberDto, Model model) {
		/*
		 * if(memberDto.getEmail().isEmpty()) { model.addAttribute("errorMsg",
		 * "아이디를 입력해 주십시오."); return "/signup"; }else
		 * if(memberDto.getPassword().isEmpty()) { model.addAttribute("errorMsg",
		 * "비밀번호를 입력해 주십시오."); return "/signup"; }
		 */
    	
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
    		model.addAttribute("loginFailureError", "아이디나 비밀번호를 확인해 주십시오.");
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