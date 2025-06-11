package com.swfinal.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swfinal.exception.LoginException;
import com.swfinal.exception.RegisterException;
import com.swfinal.mapper.MemberMapper;
import com.swfinal.util.EncryptUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
	private final MemberMapper memberMapper;
	private final EncryptUtil encryptUtil;

	// 회원가입 처리
	@Transactional(readOnly = true)
	public Map<String, Object> insertMember(Map<String, Object> params) {
		log.info("회원가입 요청 정보: {}", params);
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap.put("REPL_CD", "0000");
			resultMap.put("REPL_MSG", "SUCCESS");
			resultMap.put("REPL_PAGE_MSG", "정상처리 되었습니다.");

			String userId = (String) params.get("userId");

			// TODO 입력값 검증
			if (userId == null || userId.isEmpty()) {
				throw new RegisterException("2000", "아이디를 입력 오류", "아이디를 입력해주세요");
			}

			// TODO 아이디 정복 검사
			int checkDuplCnt = memberMapper.checkDuplUserId(userId);
			if (checkDuplCnt > 0) {
				throw new RegisterException("2001", "중복된 아이디입니다.", "중복된 아이디입니다.");
			}

			// TODO 회원가입 수행
			if (memberMapper.insertMember(params) < 0) {
				throw new RegisterException("2002", "회원가입 실패", "회원 가입 중 오류가 발생하였습니다.");
			}
		} catch (RegisterException rex) {
			resultMap.put("REPL_CD", rex.getReplCd());
			resultMap.put("REPL_MSG", rex.getReplMsg());
			resultMap.put("REPL_PAGE_MSG", rex.getReplPageMsg());
			log.error("회원가입 중 오류 발생: {}", rex.getReplMsg());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("회원 가입 오류 발생", e);
			resultMap.put("REPL_CD", "0001");
			resultMap.put("REPL_MSG", "FAILED");
			resultMap.put("REPL_PAGE_MSG", "회원 가입 오류 발생");
		}
		return resultMap;
	}

	// 회원 리스트 조회
	@Transactional(readOnly = true)
	public Map<String, Object> getMemberList(SearchForm searchForm) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap.put("REPL_CD", "0000");
			resultMap.put("REPL_MSG", "SUCCESS");
			resultMap.put("REPL_PAGE_MSG", "정상처리 되었습니다.");

			Integer pageNum = searchForm.getPageNum() != null && searchForm.getPageNum() > 0 ? searchForm.getPageNum()
					: 1;
			String searchUserId = searchForm.getSearchUserId() != null ? searchForm.getSearchUserId().trim() : "";
			String searchUserEmail = searchForm.getSearchUserEmail() != null ? searchForm.getSearchUserEmail().trim()
					: "";
			int pageSize = 4;
			int offset = (pageNum - 1) * pageSize;
			Map<String, Object> paramsMap = new HashMap<>();
			paramsMap.put("pageNum", pageNum);
			paramsMap.put("searchUserId", searchUserId);
			paramsMap.put("searchUserEmail", searchUserEmail);
			paramsMap.put("pageSize", pageSize);
			paramsMap.put("pageOffset", offset);

			int totalCount = memberMapper.selectUserTotalCount(paramsMap);
			log.info("totalCount: {}", totalCount);
			resultMap.put("TOTAL_COUNT", totalCount);

			int firstPageNum = 1;
			int pageBlockSize = 5;

			List<Map<String, Object>> user_list = memberMapper.selectUserList(paramsMap);
			log.info("상품 목록 조회 성공 : {}", user_list);
			resultMap.put("USER_LIST", user_list);

			int lastPageNum = 3;
			// (int) Math.ceil((double) totalCount / pageSize);
			int startBlockPage = ((pageNum - 1) / pageBlockSize) * pageBlockSize + 1;
			int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, lastPageNum);

			List<Integer> pageBlockList = new ArrayList<>();
			for (int i = startBlockPage; i <= endBlockPage; i++) {
				pageBlockList.add(i);
			}

			Map<String, Object> pagingMap = new HashMap<>();
			pagingMap.put("PAGE_BLOCK_LIST", pageBlockList);
			pagingMap.put("FIRST_PAGE_NUM", firstPageNum);
			pagingMap.put("LAST_PAGE_NUM", lastPageNum);
			pagingMap.put("PAGE_BLOCK_LIST", pageBlockList);
			pagingMap.put("PAGE_NUM", pageNum);
			pagingMap.put("PAGE_SIZE", pageSize);
			pagingMap.put("PAGE_OFFSET", offset);

			resultMap.put("pagingMap", pagingMap);

			resultMap.put("paramsMap", paramsMap);

		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("REPL_CD", "0001");
			resultMap.put("REPL_MSG", "PRODUCT_LIST_ERROR");
			resultMap.put("REPL_PAGE_MSG", "회원 목록 조회 중 오류가 발생하였습니다.");
		}
		return resultMap;
	}

	// 로그인 처리
	@Transactional(readOnly = true)
	public Map<String, Object> loginMember(Map<String, Object> params) {
		log.info("로그인 요청 정보: {}", params);
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap.put("REPL_CD", "0000");
			resultMap.put("REPL_MSG", "SUCCESS");
			resultMap.put("REPL_PAGE_MSG", "정상처리 되었습니다.");

			String userId = (String) params.get("userId");
			String userPw = (String) params.get("userPw");

			// TODO 입력값 검증
			if (userId == null || userId.isEmpty()) {
				throw new LoginException("3000", "아이디를 입력해주세요", "입력값을 넣어주세요");
			}

			// TODO 사용자가 존재하는지 확인
			Map<String, Object> memberInfo = memberMapper.login(params);
			if (memberInfo == null) {
				throw new LoginException("3001", "사용자 조회 실패", "사용자 조회 중 오류가 발생하였습니다.");
			}
			log.info("membeInfo: {}", memberInfo);
			// TODO 로그인 처리
			String dbPw = (String) memberInfo.get("user_pw");
			String encPw = (String) encryptUtil.encryptSha256(userPw);
			if (!dbPw.equals(encPw)) {
				throw new LoginException("3002", "비밀번호 오류", "비밀번호가 일치하지 않습니다.");
			}

			resultMap.put("memberInfo", memberInfo);

		} catch (LoginException rex) {
			resultMap.put("REPL_CD", rex.getReplCd());
			resultMap.put("REPL_MSG", rex.getReplMsg());
			resultMap.put("REPL_PAGE_MSG", rex.getReplPageMsg());
			log.error("로그인 중 오류 발생: {}", rex.getReplMsg());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("로그인 처리 중 오류 발생", e);
			resultMap.put("REPL_CD", "0001");
			resultMap.put("REPL_MSG", "FAILED");
			resultMap.put("REPL_PAGE_MSG", "로그인 오류 발생");
		}
		return resultMap;
	}

	// 회원 정보 수정
	@Transactional(readOnly = true)
	public Map<String, Object> modifyMember(Map<String, Object> params) {
		log.info("회원 정보 수정 요청 정보: {}", params);
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap.put("REPL_CD", "0000");
			resultMap.put("REPL_MSG", "회원 정보 수정 성공");
			resultMap.put("REPL_PAGE_MSG", "회원 정보가 성공적으로 수정 되었습니다.");

			if (memberMapper.modifyMember(params) < 0) {
				throw new RegisterException("2003", "회원 정보 수정 오류", "회원정보 수정 성공.");
			}

		} catch (RegisterException rex) {
			rex.printStackTrace();
			resultMap.put("REPL_CD", rex.getReplCd());
			resultMap.put("REPL_MSG", rex.getReplMsg());
			resultMap.put("REPL_PAGE_MSG", rex.getReplPageMsg());
			log.error("회원 정보 수정 중 오류 발생: {}", rex.getReplMsg());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("회원 정보 수정 중 오류 발생", e);
			resultMap.put("REPL_CD", "0001");
			resultMap.put("REPL_MSG", "FAILED");
			resultMap.put("REPL_PAGE_MSG", "회원 정보 수정 오류 발생");
		}
		return resultMap;
	}

	// 회원 정보 삭제
	@Transactional(readOnly = true)
	public Map<String, Object> deleteMember(Map<String, Object> params) {
		log.info("회원 정보 삭제 요청: {}", params);
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap.put("REPL_CD", "0000");
			resultMap.put("REPL_MSG", "SUCCESS");
			resultMap.put("REPL_PAGE_MSG", "정상처리 되었습니다.");

			if (memberMapper.deleteMember(params) < 0) {
				throw new RegisterException("2003", "회원 정보 삭제 오류", "회원정보 삭제 성공.");
			}

		} catch (RegisterException rex) {
			rex.printStackTrace();
			resultMap.put("REPL_CD", rex.getReplCd());
			resultMap.put("REPL_MSG", rex.getReplMsg());
			resultMap.put("REPL_PAGE_MSG", rex.getReplPageMsg());
			log.error("회원 정보 삭제 중 오류 발생: {}", rex.getReplMsg());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("회원 정보 삭제 중 오류 발생", e);
			resultMap.put("REPL_CD", "0001");
			resultMap.put("REPL_MSG", "FAILED");
			resultMap.put("REPL_PAGE_MSG", "회원 정보 삭제 오류 발생");
		}
		return resultMap;
	}

	// 회원 상세 정보 조회
	@Transactional(readOnly = true)
	public Map<String, Object> getMemberDetail(int userSeq) {
		log.info("회원 상세 정보 요청 정보 :{}", userSeq);
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap.put("REPL_CD", "0000");
			resultMap.put("REPL_MSG", "SUCCESS");
			resultMap.put("REPL_PAGE_MSG", "정상처리 되었습니다.");

			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("userSeq", userSeq);
			Map<String, Object> userInfo = memberMapper.getMemberInfo(paramMap);
			log.info("USER_INFO: {}", userInfo);
			resultMap.put("USER_INFO", userInfo);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("회원 정보 조회 중 오류 발생", e);
			resultMap.put("REPL_CD", "0001");
			resultMap.put("REPL_MSG", "FAILED");
			resultMap.put("REPL_PAGE_MSG", "회원 정보 조회 오류 발생");
		}
		return resultMap;
	}

}
