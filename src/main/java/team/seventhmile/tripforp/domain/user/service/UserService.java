package team.seventhmile.tripforp.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.dto.UserInfoRequest;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;


public interface UserService {

	// 회원가입
	void register(UserDto userDto);

	boolean isDuplicatedNickname(String nickname);

	// 비밀번호 재설정 로직
	ResponseEntity<?> resetPassword(String username, String newPassword);

	//개인정보 조회
	UserInfoResponse getUserInfo(UserDetails userDetails);

	//비밀번호 변경 (비밀번호 로직 중복 정리 예정)
	ResponseEntity<?> modifyPassword(HttpServletRequest request, String newPassword);

	String extractAccessToken(HttpServletRequest request);

	//개인정보 수정
	UserInfoResponse updateInfo(@AuthenticationPrincipal CustomUserDetails userDetails, UserInfoRequest userInfoReq);

	//비밀번호 찾기(이메일 인증 후 비밀번호 재설정)
	ResponseEntity<?> findPassword(String email, String newPassword);

	//회원 탈퇴
	void deleteUser(UserDetails userDetails, HttpServletResponse response);
}
