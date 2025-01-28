package ru.netology.test;

import lombok.val;
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
import static java.lang.Math.abs;
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

        val firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startFirstCardBalance = dashboardPage.getBalance(firstCardNumber);

        // При любом количестве карт "выбрасываем" из списка ранее выбранную для того, чтобы взять случайную из оставшихся
        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        val secondCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startSecondCardBalance = dashboardPage.getBalance(secondCardNumber);

        val transferPage = dashboardPage.transferToCard(secondCardNumber);

        val amount = (int) (startFirstCardBalance * random.nextDouble());
        dashboardPage = transferPage.transferFromCard(String.valueOf(amount), firstCardNumber);

        val expectedFirstCardBalance = startFirstCardBalance - abs(amount);
        val expectedSecondCardBalance = startSecondCardBalance + abs(amount);

        val actualFirstCardBalance = dashboardPage.getBalance(firstCardNumber);
        val actualSecondCardBalance = dashboardPage.getBalance(secondCardNumber);
        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldShowErrorNotificationForInvalidTransferData() {
        var cardList = getValidCardNumbers();

        val firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startFirstCardBalance = dashboardPage.getBalance(firstCardNumber);

        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        val secondCardNumber = cardList.get(random.nextInt(cardList.size()));

        val transferPage = dashboardPage.transferToCard(secondCardNumber);

        val amount = (int) (startFirstCardBalance * (1.0 + random.nextDouble()));
        transferPage.transferFromCard(String.valueOf(amount), firstCardNumber);
        transferPage.checkNotificationMessage("Ошибка! Произошла ошибка");
    }

    @Test
    void shouldNotPerformTransferWhenOverdraftOccurs() {
        var cardList = getValidCardNumbers();

        val firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startFirstCardBalance = dashboardPage.getBalance(firstCardNumber);

        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        val secondCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startSecondCardBalance = dashboardPage.getBalance(secondCardNumber);

        val transferPage = dashboardPage.transferToCard(secondCardNumber);

        val amount = (int) (startFirstCardBalance * (1.0 + random.nextDouble()));
        transferPage.transferFromCard(String.valueOf(amount), firstCardNumber);
        dashboardPage = transferPage.cancelTransfer();
        assertEquals(startFirstCardBalance, dashboardPage.getBalance(firstCardNumber));
        assertEquals(startSecondCardBalance, dashboardPage.getBalance(secondCardNumber));
    }

    @Test
    void transferMoneyFailureTestWrongCardNumberFrom() {
        val cardList = getValidCardNumbers();

        val firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startFirstCardBalance = dashboardPage.getBalance(firstCardNumber);

        val transferPage = dashboardPage.transferToCard(firstCardNumber);

        val amount = (int) (startFirstCardBalance * random.nextDouble());
        dashboardPage = transferPage.transferFromCard(String.valueOf(amount), generateCardNumber());
        transferPage.checkNotificationMessage("Ошибка! Произошла ошибка");
        dashboardPage = transferPage.cancelTransfer();
        assertEquals(startFirstCardBalance, dashboardPage.getBalance(firstCardNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = { "amount", "", "0" })
    void transferMoneyFailureTestWrongSymbolsAmount(String amount) {
        var cardList = getValidCardNumbers();

        val firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startFirstCardBalance = dashboardPage.getBalance(firstCardNumber);

        cardList = cardList.stream().filter(str -> !str.equals(firstCardNumber)).collect(Collectors.toList());
        val secondCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startSecondCardBalance = dashboardPage.getBalance(secondCardNumber);

        val transferPage = dashboardPage.transferToCard(secondCardNumber);

        dashboardPage = transferPage.transferFromCard(amount, firstCardNumber);
        assertNotNull(dashboardPage);
        val actualFirstCardBalance = dashboardPage.getBalance(firstCardNumber);
        val actualSecondCardBalance = dashboardPage.getBalance(secondCardNumber);
        assertEquals(startFirstCardBalance, actualFirstCardBalance);
        assertEquals(startSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void selfCardTransaction() {
        val cardList = getValidCardNumbers();

        val firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startBalance = dashboardPage.getBalance(firstCardNumber);

        val transferPage = dashboardPage.transferToCard(firstCardNumber);

        dashboardPage = transferPage.transferFromCard(String.valueOf(random.nextInt(Integer.MAX_VALUE)), firstCardNumber);
        assertEquals(startBalance, dashboardPage.getBalance(firstCardNumber));
    }

    @Test
    void escapeTransactionTest() {
        val cardList = getValidCardNumbers();

        val firstCardNumber = cardList.get(random.nextInt(cardList.size()));
        val startBalance = dashboardPage.getBalance(firstCardNumber);

        val transferPage = dashboardPage.transferToCard(firstCardNumber);

        dashboardPage = transferPage.cancelTransfer();
        assertEquals(startBalance, dashboardPage.getBalance(firstCardNumber));
    }
}

