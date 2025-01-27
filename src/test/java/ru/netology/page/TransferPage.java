package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private final SelenideElement heading = $("[data-test-id=dashboard]~.heading");
    private final SelenideElement amountField = $("[data-test-id='amount'] input");
    private final SelenideElement fromField = $("[data-test-id='from'] input");
    private final SelenideElement toField = $("[data-test-id='to'] input");
    private final SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private final SelenideElement cancelButton = $("[data-test-id='action-cancel']");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification']");

    public TransferPage() {
        heading.shouldHave(Condition.text("Пополнение карты"));
    }

    public DashboardPage transferFromCard(String amount, String cardNumber) {
        amountField.setValue(amount);
        fromField.setValue(cardNumber);
        transferButton.click();
        if(errorNotification.is(Condition.visible)) {
            errorNotification.shouldHave(Condition.text("Ошибка! Произошла ошибка"));
            return null;
        }
        return new DashboardPage();
    }

    public DashboardPage cancelTransfer() {
        cancelButton.click();
        return new DashboardPage();
    }
}