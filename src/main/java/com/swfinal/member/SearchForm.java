package com.swfinal.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchForm {
	private Integer pageNum;
	private String searchUserId;
	private String searchUserEmail;
}
