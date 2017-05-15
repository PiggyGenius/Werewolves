package dao;

import java.sql.*;
import javax.sql.DataSource;
import javax.annotation.Resource;

import model.User;
import model.Game;

import java.lang.Runnable;

public class GameStartThread implements Runnable {
	private final DataSource dataSource;
	private String user;

	public GameStartThread(DataSource dataSource, String user){
		this.dataSource = dataSource;
		this.user = user;
	}

	@Override
	public void run(){
	}
}
