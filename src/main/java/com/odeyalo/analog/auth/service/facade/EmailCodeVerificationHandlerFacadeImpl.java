package com.odeyalo.analog.auth.service.facade;

import com.odeyalo.analog.auth.config.security.jwt.utils.JwtTokenProvider;
import com.odeyalo.analog.auth.dto.response.JwtTokenResponseDTO;
import com.odeyalo.analog.auth.entity.RefreshToken;
import com.odeyalo.analog.auth.entity.User;
import com.odeyalo.analog.auth.entity.VerificationCode;
import com.odeyalo.analog.auth.exceptions.CodeVerificationException;
import com.odeyalo.analog.auth.repository.UserRepository;
import com.odeyalo.analog.auth.service.refresh.RefreshTokenProvider;
import com.odeyalo.analog.auth.service.support.CustomUserDetails;
import com.odeyalo.analog.auth.service.support.verification.CodeVerificationManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Verify user code and if code is correct generate JwtTokenRefreshDTO, active user
 */
@Component
public class EmailCodeVerificationHandlerFacadeImpl implements EmailCodeVerificationHandlerFacade {
    private final CodeVerificationManager verificationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final UserRepository userRepository;

    public EmailCodeVerificationHandlerFacadeImpl(CodeVerificationManager verificationManager, JwtTokenProvider jwtTokenProvider, RefreshTokenProvider refreshTokenProvider, UserRepository userRepository) {
        this.verificationManager = verificationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public JwtTokenResponseDTO verifyCode(String codeValue) throws CodeVerificationException {
        Optional<VerificationCode> codeOptional = this.verificationManager.getVerificationCodeByCodeValue(codeValue);
        VerificationCode code = codeOptional.orElseThrow(() -> new CodeVerificationException("Presented code is wrong"));
        User user = code.getUser();
        this.deleteUsedCode(code);
        this.activeUser(user);
        String jwtToken = this.jwtTokenProvider.generateJwtToken(new CustomUserDetails(user));
        RefreshToken refreshToken = this.refreshTokenProvider.createAndSaveToken(user);
        return new JwtTokenResponseDTO(true, jwtToken, refreshToken.getRefreshToken());
    }

    private void deleteUsedCode(VerificationCode code) {
        this.verificationManager.deleteCode(code);
    }

    private void activeUser(User user) {
        user.setAccountActivated(true);
        this.userRepository.save(user);
    }
}
