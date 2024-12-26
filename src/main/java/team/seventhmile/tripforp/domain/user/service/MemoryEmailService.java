package team.seventhmile.tripforp.domain.user.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.user.dto.VerificationCode;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemoryEmailService implements EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final Map<String, VerificationCode> verificationCodes;

    @Override
    public void sendVerificationEmail(String recipient) {
        // 이메일 중복 체크, 탈퇴된 이메일 확인
        Optional<User> existingUser = userRepository.findByEmail(recipient);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsDeleted()) { //탈퇴
                throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
            }
            //중복
            throw new AuthCustomException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        emailGenerateAndSend(recipient);
    }

    @Override
    public String generateEmailCode() {
        int codeLength = 6;  // 코드자리 6자리로 설정
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(codeLength);
        Random random = new Random();

        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(chars.length());
            char randomChar = chars.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString().trim();
    }

    @Override
    public void verifyEmailCode(String email, String code) {
        VerificationCode saved = verificationCodes.get(email);
        if (saved == null) {
            throw new AuthCustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
        }
        String storedCode = saved.getCode();
        if (LocalDateTime.now().isAfter(saved.getExp())) {
            verificationCodes.remove(email);
            throw new AuthCustomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        if (!storedCode.equals(code)) {
            throw new AuthCustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        verificationCodes.remove(email);
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsDeleted()) { //탈퇴
                throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
            }
        } else {
            throw new AuthCustomException(ErrorCode.USER_NOT_FOUND_IN_DATABASE);
        }

        emailGenerateAndSend(email);
    }

    @Override
    public void emailGenerateAndSend(String email) {
        String emailCode = generateEmailCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(String.format("%s <%s>", "Trip For P", fromEmail));
        message.setTo(email); //수신자 설정
        message.setSubject("[Trip For P] 인증코드 발송 안내"); //제목 설정
        message.setText("귀하의 인증 코드는 " + emailCode + "입니다.\n인증 코드는 5분 간 유지됩니다."); //내용 설정
        try {
            javaMailSender.send(message);
            verificationCodes.put(email,
                new VerificationCode(emailCode, LocalDateTime.now().plusMinutes(5)));
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생", e);
            throw new AuthCustomException(ErrorCode.EMAIL_SEND_ERROR);
        }

    }
}
