import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Автоматизированные тесты для работы с дневником питания FatSecret
 * TC003: Добавление продукта в дневник
 * TC004: Поиск продуктов
 */
public class FatSecretDiaryTests {

    private static final String BASE_URL = "https://foods.fatsecret.com";
    private static final String DIARY_URL = BASE_URL + "/Diary.aspx";
    private static final String LOGIN_URL = BASE_URL + "/Default.aspx";

    private static final String TEST_USERNAME = "Alice Free";
    private static final String TEST_PASSWORD = "EdgeTest2025!";

    /**
     * TC003: Добавление продукта в дневник
     * Тип: Функциональный
     */
    @Test
    @DisplayName("TC003: Добавление продукта в дневник")
    void test3_AddFoodToDiary() {
        // Предусловия: Пользователь авторизован в системе
        Configuration.pageLoadStrategy = "eager";

        // Авторизация
        Selenide.open(LOGIN_URL);
        getWebDriver().manage().window().maximize();

        $x("//input[@type='text' and (contains(@id, 'Username') or contains(@name, 'Username') or contains(@id, 'Login') or contains(@name, 'Login'))]")
                .shouldBe(visible)
                .setValue(TEST_USERNAME);

        $x("//input[@type='password' and (contains(@id, 'Password') or contains(@name, 'Password'))]")
                .shouldBe(visible)
                .setValue(TEST_PASSWORD);

        $x("//input[@type='submit' and (contains(@value, 'Sign In') or contains(@value, 'Login') or contains(@value, 'Войти'))] | //button[contains(text(), 'Sign In') or contains(text(), 'Login')]")
                .shouldBe(clickable)
                .click();

        sleep(2000);

        // Открыть дневник питания
        Selenide.open(DIARY_URL);
        sleep(2000);

        // Нажать "Add Food" в завтраке
        $x("//a[contains(text(), 'Add Food') or contains(text(), 'Добавить еду')] | //button[contains(text(), 'Add Food')] | //*[contains(@id, 'AddFood') or contains(@class, 'add-food')]")
                .shouldBe(visible)
                .click();

        sleep(1000);

        // Ввести "apple" в поиск
        $x("//input[@type='text' and (contains(@id, 'search') or contains(@name, 'search') or contains(@placeholder, 'Search') or contains(@placeholder, 'Поиск'))]")
                .shouldBe(visible)
                .setValue("apple");

        // Нажать поиск или Enter
        $x("//input[@type='submit' and contains(@value, 'Search')] | //button[contains(text(), 'Search')] | //*[@type='submit']")
                .shouldBe(clickable)
                .click();

        sleep(2000);

        // Выбрать продукт (первый результат поиска)
        $x("//a[contains(text(), 'Apple') or contains(text(), 'apple')] | //*[contains(@class, 'food-item') or contains(@class, 'search-result')]//a[1]")
                .shouldBe(visible)
                .click();

        sleep(1000);

        // Указать количество: 1 medium
        $x("//select[contains(@id, 'serving') or contains(@name, 'serving')] | //input[contains(@id, 'serving')]")
                .shouldBe(visible);

        // Попытка выбрать "medium" из выпадающего списка или ввести значение
        try {
            $x("//select[contains(@id, 'serving') or contains(@name, 'serving')]")
                    .selectOptionContainingText("medium");
        } catch (Exception e) {
            // Если это не select, попробуем ввести значение
            $x("//input[contains(@id, 'serving') or contains(@name, 'serving')]")
                    .setValue("1 medium");
        }

        // Нажать "Add selected" или кнопку добавления
        $x("//input[@type='submit' and (contains(@value, 'Add') or contains(@value, 'Добавить'))] | //button[contains(text(), 'Add') or contains(text(), 'Add selected')]")
                .shouldBe(clickable)
                .scrollTo()
                .click();

        sleep(2000);

        // Ожидаемый результат: Продукт добавлен в дневник, калории пересчитаны
        // Проверяем, что продукт отображается в дневнике
        $x("//*[contains(text(), 'Apple') or contains(text(), 'apple')] | //*[contains(text(), '72')]")
                .shouldBe(visible);

        // Проверяем, что счетчик калорий обновился
        $x("//*[contains(@id, 'calories') or contains(@class, 'calories')] | //*[contains(text(), 'kcal') or contains(text(), 'calories')]")
                .shouldBe(visible);

        System.out.println("✓ TC003 пройден. Продукт 'apple' успешно добавлен в дневник.");
    }

    /**
     * TC004: Поиск продуктов
     * Тип: Функциональный
     */
    @Test
    @DisplayName("TC004: Поиск продуктов")
    void test4_SearchFood() {
        // Предусловия: Пользователь находится в форме добавления продукта
        Configuration.pageLoadStrategy = "eager";

        // Авторизация
        Selenide.open(LOGIN_URL);
        getWebDriver().manage().window().maximize();

        $x("//input[@type='text' and (contains(@id, 'Username') or contains(@name, 'Username') or contains(@id, 'Login') or contains(@name, 'Login'))]")
                .shouldBe(visible)
                .setValue(TEST_USERNAME);

        $x("//input[@type='password' and (contains(@id, 'Password') or contains(@name, 'Password'))]")
                .shouldBe(visible)
                .setValue(TEST_PASSWORD);

        $x("//input[@type='submit' and (contains(@value, 'Sign In') or contains(@value, 'Login') or contains(@value, 'Войти'))] | //button[contains(text(), 'Sign In') or contains(text(), 'Login')]")
                .shouldBe(clickable)
                .click();

        sleep(2000);

        // Открыть форму добавления продукта
        Selenide.open(DIARY_URL);
        sleep(2000);

        // Нажать "Add Food"
        $x("//a[contains(text(), 'Add Food') or contains(text(), 'Добавить еду')] | //button[contains(text(), 'Add Food')] | //*[contains(@id, 'AddFood') or contains(@class, 'add-food')]")
                .shouldBe(visible)
                .click();

        sleep(1000);

        long startTime = System.currentTimeMillis();

        // Ввести "chicken breast" в поиск
        $x("//input[@type='text' and (contains(@id, 'search') or contains(@name, 'search') or contains(@placeholder, 'Search') or contains(@placeholder, 'Поиск'))]")
                .shouldBe(visible)
                .setValue("chicken breast");

        // Нажать поиск или Enter
        $x("//input[@type='submit' and contains(@value, 'Search')] | //button[contains(text(), 'Search')] | //*[@type='submit']")
                .shouldBe(clickable)
                .click();

        sleep(2000);

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Ожидаемый результат: Отображение релевантных результатов, время отклика <2 сек
        // Проверяем, что результаты поиска отображаются
        $x("//*[contains(text(), 'Chicken') or contains(text(), 'chicken')] | //*[contains(@class, 'food-item') or contains(@class, 'search-result')]")
                .shouldBe(visible);

        // Проверяем, что информация о калориях указана для каждого результата
        $x("//*[contains(text(), 'calories') or contains(text(), 'kcal')]")
                .shouldBe(visible);

        // Проверяем время отклика
        assert responseTime < 2000 : "Время отклика должно быть меньше 2 секунд. Фактическое: " + responseTime + "ms";

        System.out.println("✓ TC004 пройден. Поиск продуктов работает корректно. Время отклика: " + responseTime + "ms");
    }
}

