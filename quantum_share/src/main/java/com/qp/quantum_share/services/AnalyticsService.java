package com.qp.quantum_share.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qp.quantum_share.configuration.ConfigurationClass;
import com.qp.quantum_share.response.ResponseStructure;
import com.restfb.FacebookClient;

import jakarta.servlet.http.HttpSession;

@Component
public class AnalyticsService {

	@Autowired
	ConfigurationClass config;

	@Autowired
	ResponseStructure<String> responseStructure;

	RestTemplate restTemplate = new RestTemplate();
	HttpHeaders headers = new HttpHeaders();
	String accesstoken="EAAFZCTnDSGUUBOzm32aQOhqbo6BwzM5tKZCTbOcl0roPYcxoc2u8wNW59YGAdDVEWUKypajltN5mGzqT2ki4wPXJCAdAZBbYB10VSJj7KF0IMzA5vGaAZBSVVy8BjreRlozGOCPXnmAUZCjXgZBTBqmMZBK01ZCmH9UeYLcLEQJZAZCY55xJXZAHzdFyYN0CxnR70AC34nsdCiNfww3B6MZD";
	
	public ResponseEntity<ResponseStructure<Map<String, Integer>>> FacebookPostAnalytics(String postId) {

		try {
//			String accesstoken="EAAT2rtm6eagBOZBLOnuymo7Kd0DZAuxOjJAZBIsFtxzNRuPXYwTdaoQ4rXXygZAy8rZBYnrR6538TIMuXgOnHifpn2ObMCoZCfuj0O4UZAimV7fI8ZC2RDn4n7i9avwn1vPMEWcg1ZAPsKUJGEwS1Un0aZCSS5DiRZCM2Jhb1NOcClf2uCcuSeXQAyMe8jAmsgZAYx4W4hw5wZCRS9N7WZBKeZAw8ZC8cKoCoO9uYHaLXSZB7h2EZD";
			String likeUrl = "https://graph.facebook.com/v19.0/" + postId
					+ "/insights?metric=post_reactions_by_type_total&access_token="+accesstoken;
			String commentUrl = "https://graph.facebook.com/" + postId
					+ "?fields=likes.summary(true)&access_token="+accesstoken;
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<String> likeResponse = restTemplate.exchange(likeUrl, HttpMethod.GET, entity, String.class);
			ResponseEntity<String> commentResponse = restTemplate.exchange(commentUrl, HttpMethod.GET, entity,
					String.class);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Integer> responseData = new HashMap<>();
			try {
				JsonNode likeData = mapper.readTree(likeResponse.getBody());
				JsonNode commentData = mapper.readTree(commentResponse.getBody());
				// Parse reactions by type
				JsonNode reactions = likeData.get("data").get(0).get("values").get(0).get("value");
				reactions.fields().forEachRemaining(entry -> {
					responseData.put(entry.getKey(), entry.getValue().asInt());
				});
				// Parse total comments
				int totalComments = commentData.get("likes").get("summary").get("total_count").asInt();
				responseData.put("total_comments", totalComments);
			} catch (Exception e) {
				System.out.println(e);
				ResponseStructure<Map<String, Integer>> responseStructure = new ResponseStructure<>();
				responseStructure.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				responseStructure.setMessage(e.getMessage());
				responseStructure.setPlatform("facebook");
				responseStructure.setStatus("error");
				responseStructure.setData(responseData);
				return new ResponseEntity<>(responseStructure, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			ResponseStructure<Map<String, Integer>> responseStructure = new ResponseStructure<>();
			responseStructure.setCode(HttpStatus.OK.value());
			responseStructure.setData(responseData);
			responseStructure.setMessage("Post reaction and comment count");
			responseStructure.setPlatform("facebook");
			responseStructure.setStatus("success");

			return new ResponseEntity<>(responseStructure, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e);
			ResponseStructure<Map<String, Integer>> responseStructure = new ResponseStructure<>();
			responseStructure.setCode(HttpStatus.CONFLICT.value());
			responseStructure.setMessage("PostId/AccessToken is invalid or Post dosent excesist");
			responseStructure.setPlatform("facebook");
			responseStructure.setStatus("error");
			responseStructure.setData(e.getMessage());
			return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
		}
	}

	public ResponseEntity<ResponseStructure> InstagramPostAnalytics(String postId) {
        try {
        	System.out.println(postId);
            String url = "https://graph.facebook.com/" + postId + "/insights?metric=comments,likes,saved,shares,video_views,follows,reach,replies";
//          String accessToken = "EAAT2rtm6eagBOyUUgXapvTxscMjO1czZCjmDG6SRg9ZCY7VZAk2G43fYyPCv2slF3hUQX70dkSbjulaibOlx1hZBprrbKLcZCvLMkiPoqedZA6BKyeZCMCiTK0qn0ZAUH4x8ES2glHORLP4YOWnuFhsigAsRquAZCLUCr72n3rpTMCRHfnNcbQK57qaDmxzSI4dq2GSK9MSb7J8i4sIfZBuNCy9KW93klwyNPvYZBGbLZCLM2AZDZD";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accesstoken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println(response); 
            ObjectMapper objectMapper=new ObjectMapper();
            // Parse JSON response
            JsonNode root = objectMapper.readTree(response.getBody());
//            System.out.println(root);
            JsonNode data = root.path("data");
//            System.out.println(data);
            Map<String, Integer> insights = new HashMap<>();
            for (JsonNode node : data) {
                String name = node.path("name").asText();
                int value = node.path("values").get(0).path("value").asInt();
                insights.put(name, value);
            }
            System.out.println(insights);
            responseStructure.setCode(HttpStatus.OK.value());
            responseStructure.setData(insights);
            responseStructure.setMessage("Post/video/story analytics");
            responseStructure.setPlatform("Instagram");
            responseStructure.setStatus("success");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e);
            responseStructure.setCode(HttpStatus.CONFLICT.value());
            responseStructure.setMessage("PostId/AccessToken is invalid or Post/video/story doesn't exist");
            responseStructure.setPlatform("Instagram");
            responseStructure.setStatus("error");
            responseStructure.setData(e.getMessage());
            return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
        }
    }

	public ResponseEntity<ResponseStructure> FacebookPageFollowersCount(String pageId) {
		try {	
			String url="https://graph.facebook.com/v20.0/"+pageId+"?fields=followers_count";
//			String accessToken = "EAAT2rtm6eagBOZBLOnuymo7Kd0DZAuxOjJAZBIsFtxzNRuPXYwTdaoQ4rXXygZAy8rZBYnrR6538TIMuXgOnHifpn2ObMCoZCfuj0O4UZAimV7fI8ZC2RDn4n7i9avwn1vPMEWcg1ZAPsKUJGEwS1Un0aZCSS5DiRZCM2Jhb1NOcClf2uCcuSeXQAyMe8jAmsgZAYx4W4hw5wZCRS9N7WZBKeZAw8ZC8cKoCoO9uYHaLXSZB7h2EZD";
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accesstoken);
			HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper objectMapper=new ObjectMapper();
            // Parse JSON response
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("followers_count");
            responseStructure.setCode(HttpStatus.OK.value());
            responseStructure.setData(data);
            responseStructure.setMessage("Facebook followers count");
            responseStructure.setPlatform("Facebook");
            responseStructure.setStatus("success");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
            
		} catch (Exception e) {
			System.out.println(e);
            responseStructure.setCode(HttpStatus.CONFLICT.value());
            responseStructure.setMessage("Somting went wrong");
            responseStructure.setPlatform("Facebook");
            responseStructure.setStatus("error");
            responseStructure.setData(e.getMessage());
            return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
		}	
	}

	public ResponseEntity<ResponseStructure> FacebookVideoAnalytics(String vedioId) {
		try {
//			String accessToken = "EAAT2rtm6eagBO5olmTxQfAeyOoiTvGuFxh6ZAwcCN4q5moN7zFXwIb26dbZB2zdPxJptpQFvDNBRYfH0NDJd58aTZAR7sRBLA96FkfH8EP1GlojgStmuKTuWS1tZBmzVO4fBpH1EVQZB9oXOjghHKRUD2IPTiafXnuAQmqGFJp0ZBvS8pJT8JEempzjtz2mEN7IbB8nhZCKja2F00GPEZCAgPgI8k5SkAvCDLrf587gZD";
			String url="https://graph.facebook.com/v20.0/"+vedioId+"/video_insights?access_token="+accesstoken;
			HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accesstoken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper objectMapper=new ObjectMapper();
            // Parse JSON response
            JsonNode root = objectMapper.readTree(response.getBody());
            System.out.println(root);
//          System.out.println(root);
          JsonNode data = root.path("data");
//          System.out.println(data);
          Map<String, Integer> insights = new HashMap<>();
            for (JsonNode node : data) {
                String name = node.path("name").asText();
                int value = node.path("values").get(0).path("value").asInt();
                insights.put(name, value);
            }
            responseStructure.setCode(HttpStatus.OK.value());
            responseStructure.setData(insights);
            responseStructure.setMessage("Facebook video analytics");
            responseStructure.setPlatform("Facebook");
            responseStructure.setStatus("success");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
            
		} catch (Exception e) {
			System.out.println(e);
            responseStructure.setCode(HttpStatus.CONFLICT.value());
            responseStructure.setMessage("Somting went wrong");
            responseStructure.setPlatform("Facebook");
            responseStructure.setStatus("error");
            responseStructure.setData(e.getMessage());
            return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
		}
	}

	public ResponseEntity<ResponseStructure> FacebookReelsAnalytics(String reelsId) {
		try {
//			String accessToken = "EAAT2rtm6eagBO5olmTxQfAeyOoiTvGuFxh6ZAwcCN4q5moN7zFXwIb26dbZB2zdPxJptpQFvDNBRYfH0NDJd58aTZAR7sRBLA96FkfH8EP1GlojgStmuKTuWS1tZBmzVO4fBpH1EVQZB9oXOjghHKRUD2IPTiafXnuAQmqGFJp0ZBvS8pJT8JEempzjtz2mEN7IbB8nhZCKja2F00GPEZCAgPgI8k5SkAvCDLrf587gZD";
			String url="https://graph.facebook.com/v20.0/"+reelsId+"/video_insights?access_token="+accesstoken;
			HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accesstoken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper objectMapper=new ObjectMapper();
            // Parse JSON response
            JsonNode root = objectMapper.readTree(response.getBody());
            System.out.println(root);
//          System.out.println(root);
          JsonNode data = root.path("data");
//          System.out.println(data);
          Map<String, Integer> insights = new HashMap<>();
            for (JsonNode node : data) {
                String name = node.path("name").asText();
                int value = node.path("values").get(0).path("value").asInt();
                insights.put(name, value);
            }
            responseStructure.setCode(HttpStatus.OK.value());
            responseStructure.setData(insights);
            responseStructure.setMessage("Facebook reels analytics");
            responseStructure.setPlatform("Facebook");
            responseStructure.setStatus("success");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
            
		} catch (Exception e) {
			System.out.println(e);
            responseStructure.setCode(HttpStatus.CONFLICT.value());
            responseStructure.setMessage("Somting went wrong");
            responseStructure.setPlatform("Facebook");
            responseStructure.setStatus("error");
            responseStructure.setData(e.getMessage());
            return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
		}
	}
	
	
}
