package net.mpimedia.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mpimedia.util.LogProxyFactory;

@Service
public class ProgressService {
	
	@Autowired
	private RealtimeService realtimeService;
	
	private double currentProgress=  0.0;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public void init(String requestId) {
		currentProgress = 0.0;
		realtimeService.sendProgress(1,requestId);
	}
	
	/**
	 * send progress
	 * @param progress
	 * @param maxProgress
	 * @param percent
	 * @param newProgress
	 * @param requestId
	 */
	public void sendProgress(double progress, double maxProgress, double percent, boolean newProgress, String requestId) {
		if(newProgress) {
			currentProgress = 0.0;
		}
		currentProgress+=(progress/maxProgress);
		System.out.println("| | | | |  PROGRESS: "+currentProgress+" adding :"+progress+"/"+maxProgress+", portion: "+percent+" ==> "+ currentProgress*percent);
		realtimeService.sendProgress(currentProgress*percent, requestId);
	}

	/**
	 * end progress and send 98 - 100 %
	 * @param requestId
	 */
	public void sendComplete(String requestId) {
		System.out.println("________COMPLETE PROGRESS________");
		realtimeService.sendProgress(98, requestId);
		realtimeService.sendProgress(99, requestId);
		realtimeService.sendProgress(100, requestId);
		
	}

}
