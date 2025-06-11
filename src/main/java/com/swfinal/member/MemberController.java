package com.swfinal.member;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	// 회원 가입 화면 이동
	@GetMapping("/join")
	public ModelAndView joinMember() {
		log.info("joinMember 메서드 실행...!");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("member/member-join");
		return mav;
	}

	// 로그인 화면 이동
	@GetMapping("/login")
	public ModelAndView loginMember() {
		log.info("loginMember 메서드 실행...!");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("member/member-login");
		return mav;
	}

	// 회원 가입
	@PostMapping("/request-register")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> insertMember(@RequestBody Map<String, Object> params) {
		log.info("insertMember 메서드 실행...!");
		Map<String, Object> result = memberService.insertMember(params);
		return ResponseEntity.ok(result);
	}

	// 회원 리스트 조회
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getMemberList(@ModelAttribute SearchForm form) {
		log.info("getMemberList	 메서드 실행...!");
		ModelAndView mav = new ModelAndView();
		Map<String, Object> result = memberService.getMemberList(form);
		mav.addObject("result", result);
		mav.setViewName("member/member-list");
		return mav;
	}

	// 로그인
	@PostMapping("/request-login")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> loginMember(@RequestBody Map<String, Object> params,
			HttpSession session) {
		log.info("loginMember 메서드 실행...!");
		Map<String, Object> result = memberService.loginMember(params);
		if ("0000".equals(result.get("REPL_CD"))) {
			session.setAttribute("loginUser", params.get("userId"));
		}
		return ResponseEntity.ok(result);
	}

	// 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		log.info("logout 메서드 실행...!");
		session.invalidate();
		return "redirect:/member/login";
	}

	// 회원정보 수정
	@PostMapping("/modify")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> modifyMember(@RequestBody Map<String, Object> params) {
		log.info("modifyMember 메서드 실행...!");
		Map<String, Object> result = memberService.modifyMember(params);
		return ResponseEntity.ok(result);
	}

	// 회원정보 삭제
	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteMember(@RequestBody Map<String, Object> params) {
		log.info("deleteMember 메서드 실행...!");
		Map<String, Object> result = memberService.deleteMember(params);
		return ResponseEntity.ok(result);
	}

	// 회원 상세 화면
	@GetMapping("/detail/{userSeq}")
	public ModelAndView detailMember(@PathVariable("userSeq") int userSeq) {
		log.info("detailMember 메서드 실행...!");
		ModelAndView mav = new ModelAndView();
		Map<String, Object> result = memberService.getMemberDetail(userSeq);
		mav.addObject("result", result);
		mav.setViewName("member/member-detail");
		return mav;
	}
	


}
