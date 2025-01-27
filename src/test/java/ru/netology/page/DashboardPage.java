package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ru.netology.data.DataHelper.findCardFromNumber;
import static ru.netology.data.DataHelper.getCardItem;

public class DashboardPage {
    private final SelenideElement heading = $("[data-test-id=dashboard]");
    private final SelenideElement firstCard = $("div[data-test-id='92df3f1c-a033-48e6-8390-206f6b1f56c0']");
    private final SelenideElement secondCard = $("div[data-test-id='0f3f5c2a-249e-4c3d-8287-09f7a039391d']");
    private final SelenideElement reloadButton = $("[data-test-id='action-reload']");

    private final List<DataHelper.CardItem> cardItems;

    public DashboardPage() {
        heading.shouldBe(visible);
        cardItems = List.of(getCardItem(firstCard), getCardItem(secondCard));
    }

    public int getBalance(String cardNumber) {
        var card = findCardFromNumber(cardItems, cardNumber);
        return card.getInfo().getBalance();
    }

    public TransferPage transferToCard(String cardNumber) {
        var card = findCardFromNumber(cardItems, cardNumber);
        card.getActionButton().click();
        return new TransferPage();
    }

    public DashboardPage reload() {
        reloadButton.click();
        return new DashboardPage();
    }
}
