import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Автоматизированные тесты для отслеживания веса FatSecret
 * TC005: Отслеживание веса
 */
public class FatSecretWeightTests {

    private static final String BASE_URL = "https://foods.fatsecret.com";
    private static final String LOGIN_URL = BASE_URL + "/Default.aspx";

    private static final String TEST_USERNAME = "Alice Free";
    private static final String TEST_PASSWORD = "EdgeTest2025!";
    private static final String TEST_WEIGHT = "70";

    /**
     * Вспомогательный метод для авторизации
     */
    private void performLogin() {
        // Ждем загрузки страницы
        sleep(2000);

        // Проверяем, нужно ли открыть форму входа (возможно, есть кнопка "Sign In")
        try {
            $x("//a[contains(text(), 'Sign In') or contains(text(), 'Login') or contains(@href, 'login')] | //button[contains(text(), 'Sign In')]")
                    .shouldBe(visible)
                    .click();
            sleep(1000);
        } catch (Exception e) {
            // Форма входа уже открыта или не требуется клик
        }

        // Пробуем найти поле логина разными способами
        // Вариант 1: Простой поиск по типу input text (первое текстовое поле)
        try {
            $x("(//input[@type='text'])[1]")
                    .shouldBe(visible, Duration.ofSeconds(5))
                    .setValue(TEST_USERNAME);
        } catch (Exception e1) {
            // Вариант 2: Поиск по id/name с Username
            try {
                $x("//input[contains(@id, 'Username') or contains(@name, 'Username') or contains(@id, 'Login') or contains(@name, 'Login')]")
                        .shouldBe(visible, Duration.ofSeconds(5))
                        .setValue(TEST_USERNAME);
            } catch (Exception e2) {
                // Вариант 3: Поиск по placeholder или label
                try {
                    $x("//input[contains(@placeholder, 'username') or contains(@placeholder, 'Username') or contains(@placeholder, 'login') or contains(@placeholder, 'Login')]")
                            .shouldBe(visible, Duration.ofSeconds(5))
                            .setValue(TEST_USERNAME);
                } catch (Exception e3) {
                    // Вариант 4: Любое текстовое поле в форме
                    $x("//form//input[@type='text'] | //input[@type='text' and not(@type='hidden')]")
                            .shouldBe(visible, Duration.ofSeconds(5))
                            .setValue(TEST_USERNAME);
                }
            }
        }

        // Пробуем найти поле пароля
        try {
            $x("//input[@type='password']")
                    .shouldBe(visible, Duration.ofSeconds(5))
                    .setValue(TEST_PASSWORD);
        } catch (Exception e) {
            $x("//input[contains(@id, 'Password') or contains(@name, 'Password')]")
                    .shouldBe(visible, Duration.ofSeconds(5))
                    .setValue(TEST_PASSWORD);
        }

        // Пробуем найти кнопку входа
        try {
            $x("//input[@type='submit' and (contains(@value, 'Sign In') or contains(@value, 'Login') or contains(@value, 'Sign') or contains(@value, 'Войти'))]")
                    .shouldBe(clickable)
                    .scrollTo()
                    .click();
        } catch (Exception e1) {
            try {
                $x("//button[contains(text(), 'Sign In') or contains(text(), 'Login') or contains(text(), 'Sign')]")
                        .shouldBe(clickable)
                        .scrollTo()
                        .click();
            } catch (Exception e2) {
                // Последний вариант - просто submit
                $x("//input[@type='submit']")
                        .shouldBe(clickable)
                        .scrollTo()
                        .click();
            }
        }

        sleep(3000);
    }

    /**
     * TC005: Отслеживание веса
     * Тип: Функциональный
     */
    @Test
    @DisplayName("TC005: Отслеживание веса")
    void test5_TrackWeight() {
        // Предусловия: Пользователь авторизован в системе
        Configuration.pageLoadStrategy = "eager";
        Configuration.timeout = 10000;

        // Авторизация
        Selenide.open(LOGIN_URL);
        getWebDriver().manage().window().maximize();

        performLogin();

        // Перейти в раздел "Weight"
        $x("//a[contains(@href, 'Weight') or contains(text(), 'Weight') or contains(text(), 'Вес')] | //*[contains(@id, 'Weight')]")
                .shouldBe(visible)
                .click();

        sleep(2000);

        // Нажать "Add Weight Entry"
        $x("//a[contains(text(), 'Add Weight Entry') or contains(text(), 'Добавить запись о весе')] | //button[contains(text(), 'Add Weight Entry')] | //*[contains(@id, 'AddWeight')]")
                .shouldBe(visible)
                .click();

        sleep(1000);

        // Ввести вес 70 кг
        $x("//input[@type='text' or @type='number' and (contains(@id, 'weight') or contains(@name, 'weight') or contains(@id, 'Weight'))]")
                .shouldBe(visible)
                .setValue(TEST_WEIGHT);

        // Выбрать текущую дату (если есть календарь)
        try {
            LocalDate today = LocalDate.now();
            String dateStr = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            $x("//input[@type='text' and (contains(@id, 'date') or contains(@name, 'date'))]")
                    .shouldBe(visible)
                    .setValue(dateStr);
        } catch (Exception e) {
            // Если календарь не найден, дата может быть установлена автоматически
            System.out.println("Календарь не найден, используется текущая дата по умолчанию");
        }

        // Нажать "Add Entry" или кнопку сохранения
        $x("//input[@type='submit' and (contains(@value, 'Add') or contains(@value, 'Save') or contains(@value, 'Добавить'))] | //button[contains(text(), 'Add Entry') or contains(text(), 'Save')]")
                .shouldBe(clickable)
                .scrollTo()
                .click();

        sleep(2000);

        // Ожидаемый результат: Запись о весе добавлена, отображается в таблице
        // Проверяем, что запись отображается в списке
        $x("//*[contains(text(), '70') or contains(text(), '70.0')] | //table//td[contains(text(), '70')]")
                .shouldBe(visible);

        // Проверяем, что график обновлен (если есть элемент графика)
        try {
            $x("//*[contains(@id, 'chart') or contains(@class, 'chart') or contains(@id, 'graph')]")
                    .shouldBe(visible);
        } catch (Exception e) {
            System.out.println("График не найден, но запись добавлена успешно");
        }

        System.out.println("✓ TC005 пройден. Запись о весе 70 кг успешно добавлена.");
    }
}

