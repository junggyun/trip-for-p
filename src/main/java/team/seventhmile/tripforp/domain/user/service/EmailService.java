package team.seventhmile.tripforp.domain.user.service;

public interface EmailService {


    void sendVerificationEmail(String recipient);
    String generateEmailCode();
    void verifyEmailCode(String email, String code);
    void sendPasswordResetEmail(String email);
    void emailGenerateAndSend(String email);

}
