package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.List;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static AuthInfo getOtherAuthInfo(AuthInfo original) {
        return new AuthInfo("petya", "123qwerty");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

    public static List<String> getValidCardNumbers() {
        return List.of("5559 0000 0000 0001", "5559 0000 0000 0002");
    }

    public static String generateCardNumber() {
        return new Faker().business().creditCardNumber();
    }
}
