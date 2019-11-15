package com.study.springboot.crawling;

import lombok.Data;

@Data
public class Game {
	String time;
	String leftTeam;
	String rightTeam;
	String score;
	
	public Game() {}; 	
	public Game(String t, String lf, String rt,String sc) {
		time = t; leftTeam = lf; rightTeam = rt; score = sc;
	}
}
