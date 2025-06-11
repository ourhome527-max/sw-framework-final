package com.swfinal.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
	int checkDuplUserId(String userId);
	int insertMember(Map<String, Object> params);
	int modifyMember(Map<String, Object> params);
	int deleteMember(Map<String, Object> params);
	Map<String, Object> getMemberInfo(Map<String, Object> params);
	Map<String, Object> login(Map<String, Object> params);
	
	int selectUserTotalCount(Map<String, Object> params);
	List<Map<String, Object>> selectUserList(Map<String, Object> params);
	
}
