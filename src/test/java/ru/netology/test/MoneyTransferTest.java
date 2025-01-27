package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import java.util.Random;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.DataHelper.generateCardNumber;
import static ru.netology.data.DataHelper.getValidCardNumbers;

class MoneyTransferTest {

    DashboardPage dashboardPage;
    Random random = new Random();

    private void loginToPersonalAccount() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @BeforeEach
    void setUp() {
        loginToPersonalAccount();
    }

    @Test
    void transferMoneySuccessTest() {
        var cardList = getValidCardNumbers();

        var firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        var startBalance = dashboardPage.getBalance(firstCardNumber);

        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        var secondCardNumber = cardList.get(random.nextInt(cardList.size()));

        var transferPage = dashboardPage.transferToCard(secondCardNumber);

        var amount = (int) (startBalance * random.nextDouble());
        dashboardPage = transferPage.transferFromCard(String.valueOf(amount), firstCardNumber);

        var expectedBalance = startBalance - amount;
        var actualBalance = dashboardPage.getBalance(firstCardNumber);
        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    void transferMoneyFailureTest() {
        var cardList = getValidCardNumbers();

        var firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        var startBalance = dashboardPage.getBalance(firstCardNumber);

        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        var secondCardNumber = cardList.get(random.nextInt(cardList.size()));

        var transferPage = dashboardPage.transferToCard(secondCardNumber);

        var amount = (int) (startBalance * (1.0 + random.nextDouble()));
        dashboardPage = transferPage.transferFromCard(String.valueOf(amount), firstCardNumber);
        assertNull(dashboardPage);
    }

    @Test
    void transferMoneyFailureTestWrongCardNumberFrom() {
        var cardList = getValidCardNumbers();

        var firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        var startBalance = dashboardPage.getBalance(firstCardNumber);

        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        var secondCardNumber = cardList.get(random.nextInt(cardList.size()));

        var transferPage = dashboardPage.transferToCard(secondCardNumber);

        var amount = (int) (startBalance * random.nextDouble());
        dashboardPage = transferPage.transferFromCard(String.valueOf(amount), generateCardNumber());
        assertNull(dashboardPage);
    }

    @ParameterizedTest
    @ValueSource(strings = { "amount", "", "0" })
    void transferMoneyFailureTestWrongSymbolsAmount(String amount) {
        var cardList = getValidCardNumbers();

        var firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        var startBalance = dashboardPage.getBalance(firstCardNumber);

        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        var secondCardNumber = cardList.get(random.nextInt(cardList.size()));

        var transferPage = dashboardPage.transferToCard(secondCardNumber);

        dashboardPage = transferPage.transferFromCard(amount, firstCardNumber);
        assertNotNull(dashboardPage);
        assertEquals(startBalance, dashboardPage.getBalance(firstCardNumber));
    }

    @Test
    void selfCardTransaction() {
        var cardList = getValidCardNumbers();

        var firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        var startBalance = dashboardPage.getBalance(firstCardNumber);

        var transferPage = dashboardPage.transferToCard(firstCardNumber);

        dashboardPage = transferPage.transferFromCard(String.valueOf(random.nextInt(Integer.MAX_VALUE)), firstCardNumber);
        assertNotNull(dashboardPage);
        assertEquals(startBalance, dashboardPage.getBalance(firstCardNumber));
    }

    @Test
    void escapeTransactionTest() {
        var cardList = getValidCardNumbers();

        var firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        var startBalance = dashboardPage.getBalance(firstCardNumber);

        var transferPage = dashboardPage.transferToCard(firstCardNumber);

        dashboardPage = transferPage.cancelTransfer();
        assertNotNull(dashboardPage);
        assertEquals(startBalance, dashboardPage.getBalance(firstCardNumber));
    }
}

