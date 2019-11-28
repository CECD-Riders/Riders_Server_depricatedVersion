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
    
    //����
    @GetMapping("/")
    public String index(Model model) {
        DayGame dayGame = crawler.GetTodayGame();
        
        model.addAttribute("gameSize", dayGame.getGameList().size() + 1);
        model.addAttribute("todayGame", dayGame);
        return "/index";
    }
    
    //���� �� ���
    @RequestMapping("/gameList")
    public String gameList(Model model) {
        List<DayGame> monthGame = crawler.GetMonthGame();
        
        model.addAttribute("GameList", monthGame);
    	return "/calendar";
    }
    
    //���,����
    @RequestMapping("/rank")
    public String rank() {
    	return "/rank";
    }
    
    //���� ��û
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
    	//���⼭ ������ �ִ��� ������ ������ ���̽� ��ȸ
    	if(videoService.VideoOverlapCheck(videoName) == 1)
    		return "/watchVideo";
    	else
    		return "/noVideo";	//������ �غ��� �������� �̵�
    }
    
    
    
    // ȸ������ ������
    @GetMapping("/user/signup")
    public String dispSignup() {
        return "/signup";
    }

    // ȸ������ ó��
    @PostMapping("/user/signup")
    public String SignupAction(HttpServletRequest request, MemberDto memberDto, Model model) {
		/*
		 * if(memberDto.getEmail().isEmpty()) { model.addAttribute("errorMsg",
		 * "���̵� �Է��� �ֽʽÿ�."); return "/signup"; }else
		 * if(memberDto.getPassword().isEmpty()) { model.addAttribute("errorMsg",
		 * "��й�ȣ�� �Է��� �ֽʽÿ�."); return "/signup"; }
		 */
    	
        Long id = memberService.joinUser(memberDto);
        if(id == -1) {//���̵� �ߺ��� ��� => �ٽ� ȸ������ â���� �����ְ� ���̵� �ߺ��̶�� ��������
        	model.addAttribute("errorMsg", "�ߺ��� ���̵� �ֽ��ϴ�!");
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
    		model.addAttribute("loginFailureError", "���̵� ��й�ȣ�� Ȯ���� �ֽʽÿ�.");
    	}
        return "/login";
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

    // �������� ������
    @GetMapping("/admin/videoUpload")
    public String videoUpload() {
        return "/videoUpload";
    }
    
    // �������� ����
    @PostMapping("/admin/videoUpload")
    public String videoUploadAction(HttpServletRequest request ,Model model) {
    	
    	String localPath = request.getParameter("path");				//���� ��� 
    	String HostfileName = request.getParameter("HostfileName");		//ȣ��Ʈ ������ ����� ���� �̸�
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
			model.addAttribute("succesMsg", "�������� ����!");
			return "/videoUpload";
		}
		
    	model.addAttribute("succesMsg", "�������� ����!");
    	
    	return "/videoUpload";
    }
}