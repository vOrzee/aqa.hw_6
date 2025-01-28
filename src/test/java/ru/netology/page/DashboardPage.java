package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private final SelenideElement heading = $("[data-test-id=dashboard]");
    private final SelenideElement reloadButton = $("[data-test-id='action-reload']");
    private final ElementsCollection cardItems = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        heading.shouldBe(visible);
//        reloadButton.should(exist);
//        cardItems.first().shouldBe(exist);
    }

    public int getBalance(String cardNumber) {
        val card = findCardFromNumber(cardNumber);
        return extractBalance(card.getText());
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    public TransferPage transferToCard(String cardNumber) {
        var card = findCardFromNumber(cardNumber);
        card.$("button").click();
        return new TransferPage();
    }

    public DashboardPage reload() {
        reloadButton.click();
        return new DashboardPage();
    }

    public SelenideElement findCardFromNumber(String cardNumber) {
        var lastDigits = cardNumber.substring(cardNumber.length() - 4);
        return cardItems.find(Condition.text(lastDigits));
    }
}
