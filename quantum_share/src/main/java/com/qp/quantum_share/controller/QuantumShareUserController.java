package com.qp.quantum_share.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qp.quantum_share.configuration.JwtUtilConfig;
import com.qp.quantum_share.dao.QuantumShareUserDao;
import com.qp.quantum_share.dto.QuantumShareUser;
import com.qp.quantum_share.response.ResponseStructure;
import com.qp.quantum_share.services.QuantumShareUserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/quantum-share/user")
public class QuantumShareUserController {

	@Autowired
	QuantumShareUserService quantumShareUserService;

	@Autowired
	HttpServletRequest request;

	@Autowired
	ResponseStructure<String> structure;

	@Autowired
	JwtUtilConfig jwtUtilConfig;

	@Autowired
	QuantumShareUserDao userDao;

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<String>> userLogin(@RequestParam String emph,
			@RequestParam String password) {
		return quantumShareUserService.login(emph, password);
	}

	@PostMapping("/signup")
	public ResponseEntity<ResponseStructure<String>> signup(@RequestBody QuantumShareUser userDto) {
		return quantumShareUserService.userSignUp(userDto);
	}

//	@GetMapping("/access/remainingdays")
//	public ResponseEntity<PackageResponse> userRemainingDays(@RequestBody User user) {
//		return quantumShareUserService.calculateRemainingPackageDays(user);
//	}

	@GetMapping("/verify")
	public ResponseEntity<ResponseStructure<String>> verifyEmail(@RequestParam("token") String token) {
		return quantumShareUserService.verifyEmail(token);
	}

	@GetMapping("/account-overview")
	public ResponseEntity<ResponseStructure<String>> accountOverView() {
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7); // remove "Bearer " prefix
		int userId = jwtUtilConfig.extractUserId(jwtToken);
		return quantumShareUserService.accountOverView(userId);
	}

	@PostMapping("/account-overview")
	public ResponseEntity<ResponseStructure<String>> accountOverView(
			@RequestParam(required = false) MultipartFile file) {
		if (file.isEmpty() || !file.getContentType().startsWith("image")) {
			structure.setCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Missing or invalid file type");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);

		}
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7);
		int userId = jwtUtilConfig.extractUserId(jwtToken);
		return quantumShareUserService.accountOverView(userId, file);
	}

	@GetMapping("/access/remainingdays")
	public ResponseEntity<ResponseStructure<String>> userRemainingDays() {
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			structure.setCode(115);
			structure.setMessage("Missing or invalid authorization token");
			structure.setStatus("error");
			structure.setPlatform(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.UNAUTHORIZED);
		}
		String jwtToken = token.substring(7); // remove "Bearer " prefix
		int userId = jwtUtilConfig.extractUserId(jwtToken);
		return quantumShareUserService.calculateRemainingPackageDays(userId);
	}
	

	@GetMapping("/test/session")
	public Map<String, Object> test() {
		Map<String,Object> map=new HashMap<String, Object>();
		System.out.println("coming");
		map.put("name", "meghana");
		map.put("company", "QP");
		map.put("id", "QSU24001");
		return map;
	}

}
