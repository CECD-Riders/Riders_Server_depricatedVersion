package com.study.springboot.Controller;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.study.springboot.FTP.FTPHostInfo;
import com.study.springboot.FTP.FTPUploader;
import com.study.springboot.crawling.Crawler;
import com.study.springboot.crawling.DayGame;
import com.study.springboot.crawling.IndividualRank;
import com.study.springboot.crawling.TeamRank;
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
    public String gameListGet(HttpServletRequest request, Model model) {
    	int year,month;
    	int beforeYear,beforeMonth, nextYear,nextMonth;
    	String year_ = request.getParameter("year");
    	String month_ = request.getParameter("month");
    	if(year_ == null && month_==null) {
        	Calendar cal = Calendar.getInstance();
        	year = cal.get(Calendar.YEAR);
        	month = (cal.get(Calendar.MONTH) + 1);
    	}else {
    		year = Integer.parseInt(year_);
    		month = Integer.parseInt(month_);
    	}
        List<DayGame> yearAndMonthGame = crawler.GetYearAndMonthGame(year,month);
        
        //현 시점 기준 이전,다음 날짜 만들기
        //이전
        if(month == 1) {
			beforeMonth = 12;
			beforeYear = year-1;
		}else {
			beforeYear = year;
			beforeMonth = month-1;
			if(beforeMonth == 9)
				beforeMonth = 6;
		}
        //다음
        if(month == 12) {
			nextMonth = 1;
			nextYear = year+1;
		}else {
			nextYear = year;
			nextMonth = month+1;
			if(nextMonth == 7)
				nextMonth = 10;
		}
        
        model.addAttribute("beforeYear", beforeYear);
        model.addAttribute("beforeMonth", beforeMonth);
        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);
        model.addAttribute("nextYear", nextYear);
        model.addAttribute("nextMonth", nextMonth);
        model.addAttribute("GameList", yearAndMonthGame);
    	return "/calendar";
    }
   
    
    //기록,순위
    @RequestMapping("/rank")
    public String rank(HttpServletRequest request, Model model) {
    	int year,beforeYear,nextYear;
    	Calendar cal = Calendar.getInstance();
    	String year_ = request.getParameter("year");
    	String conference = request.getParameter("conference");
    	if(conference == null)//디폴트 동부
    		conference = "EAST";
    	
    	if(year_ == null)
    		year = cal.get(Calendar.YEAR) + 1;
    	else
    		year = Integer.parseInt(year_);   	
    	beforeYear = year - 1;
    	nextYear = year + 1;
    	if(year == 2013)
    		beforeYear = 0;
    	if(year == cal.get(Calendar.YEAR) + 1)
    		nextYear = 0;
    	
    	Pair<List<TeamRank>,List<IndividualRank>> teamAndIndividualRank 
    		= crawler.GetTeamAndIndividualRank(year,conference); 
    	
    	model.addAttribute("currentYear", year);
    	model.addAttribute("beforeYear", beforeYear);
    	model.addAttribute("nextYear", nextYear);
    	model.addAttribute("conference",conference);
    	model.addAttribute("teamRankList", teamAndIndividualRank.getFirst());
    	model.addAttribute("individualRankList", teamAndIndividualRank.getSecond());
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
    	System.out.println("==============");
    	System.out.println(localPath);
    	System.out.println("==============");
    	String HostfileName = request.getParameter("HostfileName");		//호스트 서버에 저장될 파일 이름
    	if(localPath.equals("") && HostfileName.equals(""))
    	{
			model.addAttribute("succesMsg", "영상전송 실패! 파일 정보를 입력해 주세요");
			return "/videoUpload";
    	}
    	Long id;
    	VideoDto videoDto = new VideoDto();
    	videoDto.setName(HostfileName);
    	videoDto.setLike(new Long(0));
    	FTPUploader ftpUploader;
		try {
	        id = videoService.SaveSingleVideo(videoDto);
	        if(id == -1) {
	        	model.addAttribute("succesMsg", "영상전송 실패! 중복된 영상이 있습니다.");
	        }
	        else {
		        FTPHostInfo hostInfo = new FTPHostInfo();
				ftpUploader = new FTPUploader(hostInfo.hostIP, hostInfo.ID, hostInfo.PW);
		        ftpUploader.uploadFile(localPath, HostfileName, "/ridersTest/");
		        ftpUploader.disconnect();
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			videoService.DeleteSingleVideo(videoDto);
			
			model.addAttribute("succesMsg", "영상전송 실패! 서버와의 연결을 확인해 주세요.");
			return "/videoUpload";
		}
		
		if(id !=-1)
			model.addAttribute("succesMsg", "영상전송 성공!");
    	
    	return "/videoUpload";
    }
}