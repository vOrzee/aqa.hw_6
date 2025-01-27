package ru.netology.data;

import com.codeborne.selenide.SelenideElement;
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

    @Value
    public static class CardItem {
        private CardInfo info;
        private SelenideElement actionButton;

        CardItem(SelenideElement cardElement) {
            var fullStringInfo = cardElement.getText().trim().split(" ");
            var lastNumbers = fullStringInfo[3].substring(0, 4);
            var balance = Integer.parseInt(fullStringInfo[5]);
            this.info = new CardInfo(lastNumbers, balance);
            this.actionButton = cardElement.$("button");
        }
    }

    @Value
    public static class CardInfo {
        private String maskCardNumber;
        private int balance;
    }

    public static CardItem getCardItem(SelenideElement cardElement) {
        return new CardItem(cardElement);
    }

    public static DataHelper.CardItem findCardFromNumber(List<DataHelper.CardItem> cardItems, String cardNumber) {
        var lastDigits = cardNumber.substring(cardNumber.length() - 4);
        var card = cardItems.stream().filter(cardItem -> cardItem.getInfo().getMaskCardNumber().equals(lastDigits)).findFirst();
        return card.orElse(null);
    }
}
