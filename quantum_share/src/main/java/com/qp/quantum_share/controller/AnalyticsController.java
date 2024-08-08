package com.qp.quantum_share.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qp.quantum_share.response.ResponseStructure;
import com.qp.quantum_share.services.AnalyticsService;

@RestController()
@RequestMapping("/analytics")
public class AnalyticsController {
	
	@Autowired
	AnalyticsService analyticsService;
	
	@GetMapping("/facebookpostanalytics")
	public ResponseEntity<ResponseStructure<Map<String, Integer>>> FacebookPostAnalytics(@RequestParam String postId){
		return analyticsService.FacebookPostAnalytics(postId);
	}
	
	@GetMapping("/instagrampostanalytics")
	public ResponseEntity<ResponseStructure> InstagramPostAnalytics(@RequestParam String postId){
		return analyticsService.InstagramPostAnalytics(postId);
	}
	
	@GetMapping("/facebookfollowerscount")
	public ResponseEntity<ResponseStructure> FacebookPageFollowersCount(@RequestParam String pageId){
		return analyticsService.FacebookPageFollowersCount(pageId);
	}
	
	@GetMapping("/facebookvideoanalytics")
	public ResponseEntity<ResponseStructure> FacebookVideoAnalytics(@RequestParam String vedioId){
		return analyticsService.FacebookVideoAnalytics(vedioId);
	}
	
	@GetMapping("/facebookreelsanalytics")
	public ResponseEntity<ResponseStructure> FacebookReelsAnalytics(@RequestParam String reelsId){
		return analyticsService.FacebookReelsAnalytics(reelsId);
	}
	
	
}
