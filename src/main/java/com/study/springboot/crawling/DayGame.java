package com.study.springboot.crawling;

import java.util.List;

import lombok.Data;

@Data
public class DayGame {
	String date;
	List<Game> gameList;
	
	public DayGame() {};
	
	public DayGame(String d, List<Game> gl) {
		date = d;
		gameList = gl;
	}
}
