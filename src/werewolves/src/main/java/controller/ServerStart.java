package controller;

import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import java.io.PrintWriter;
import java.io.IOException;
import controller.TimeManager;

@WebListener
public class ServerStart implements ServletContextListener {
	@Resource(name="jdbc/werewolves")
	private DataSource dataSource;
	private TimeManager timeManager;
	private Thread thread;

    public void contextInitialized(ServletContextEvent event) {
		this.timeManager = new TimeManager(this.dataSource);
		this.thread = new Thread(this.timeManager);
		this.thread.start();
    }

    public void contextDestroyed(ServletContextEvent event) {
		try {
			this.timeManager.terminate();
			this.thread.join();
		} catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
    }
}
