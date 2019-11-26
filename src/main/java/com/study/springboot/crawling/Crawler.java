package com.study.springboot.crawling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class Crawler {
	
	public DayGame  GetTodayGame() {
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
        return gameList.get(0);
	}
	
	public List<DayGame> GetMonthGame(){
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
        return gameList;
	}
	
}
