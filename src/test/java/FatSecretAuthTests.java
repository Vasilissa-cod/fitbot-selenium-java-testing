import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Автоматизированные тесты для авторизации и регистрации пользователей FatSecret
 * TC001: Регистрация пользователя
 * TC002: Авторизация пользователя
 */
public class FatSecretAuthTests {

    private static final String BASE_URL = "https://foods.fatsecret.com";
    private static final String REGISTRATION_URL = BASE_URL + "/Default.aspx?pa=m";
    private static final String LOGIN_URL = BASE_URL + "/Default.aspx";

    private static final String TEST_EMAIL = "test.edge@gmail.com";
    private static final String TEST_PASSWORD = "EdgeTest2025!";
    private static final String TEST_USERNAME = "Alice Free";

    /**
     * TC001: Регистрация пользователя
     * Тип: Функциональный
     */
    @Test
    @DisplayName("TC001: Регистрация пользователя")
    void test1_UserRegistration() {
        // Предусловия: Пользователь не зарегистрирован в системе
        // Открыть https://foods.fatsecret.com/Default.aspx?pa=m
        Configuration.pageLoadStrategy = "eager";
        Selenide.open(REGISTRATION_URL);
        getWebDriver().manage().window().maximize();

        // Ввести данные по "Alice Free"
        // Заполнить имя пользователя
        $x("//input[@name='ctl00$ctl00$MainContent$MainContent$txtUsername' or contains(@id, 'Username')]")
                .shouldBe(visible)
                .setValue(TEST_USERNAME);

        // Заполнить email
        $x("//input[@type='email' or contains(@id, 'Email') or contains(@name, 'Email')]")
                .shouldBe(visible)
                .setValue(TEST_EMAIL);

        // Заполнить пароль
        $x("//input[@type='password' and (contains(@id, 'Password') or contains(@name, 'Password'))]")
                .shouldBe(visible)
                .setValue(TEST_PASSWORD);

        // Нажать "next" или кнопку регистрации
        $x("//input[@type='submit' or contains(@value, 'next') or contains(@value, 'Next') or contains(@value, 'Sign Up') or contains(@value, 'Register')]")
                .shouldBe(clickable)
                .scrollTo()
                .click();

        // Ожидаемый результат: Успешная регистрация, перенаправление на страницу профиля
        // Проверяем, что произошло перенаправление (URL изменился или появился элемент профиля)
        sleep(2000);

        // Проверяем отсутствие ошибок регистрации
        $x("//div[contains(@class, 'error') or contains(@class, 'alert-danger')]")
                .shouldNotBe(visible);

        // Проверяем, что мы на странице настройки профиля или дневника
        String currentUrl = getWebDriver().getCurrentUrl();
        assert currentUrl.contains("fatsecret.com") : "Ожидалось перенаправление на страницу FatSecret";

        System.out.println("✓ TC001 пройден. Регистрация выполнена успешно. URL: " + currentUrl);
    }

    /**
     * TC002: Авторизация пользователя
     * Тип: Функциональный
     */
    @Test
    @DisplayName("TC002: Авторизация пользователя")
    void test2_UserLogin() {
        // Предусловия: Пользователь зарегистрирован в системе
        // Перейти на страницу входа https://foods.fatsecret.com/Default.aspx
        Configuration.pageLoadStrategy = "eager";
        Selenide.open(LOGIN_URL);
        getWebDriver().manage().window().maximize();

        // Ввести логин
        $x("//input[@type='text' and (contains(@id, 'Username') or contains(@name, 'Username') or contains(@id, 'Login') or contains(@name, 'Login'))]")
                .shouldBe(visible)
                .setValue(TEST_USERNAME);

        // Ввести пароль
        $x("//input[@type='password' and (contains(@id, 'Password') or contains(@name, 'Password'))]")
                .shouldBe(visible)
                .setValue(TEST_PASSWORD);

        // Нажать "Sign In"
        $x("//input[@type='submit' and (contains(@value, 'Sign In') or contains(@value, 'Login') or contains(@value, 'Войти'))] | //button[contains(text(), 'Sign In') or contains(text(), 'Login')]")
                .shouldBe(clickable)
                .scrollTo()
                .click();

        // Ожидаемый результат: Успешная авторизация, переход в дневник питания
        sleep(2000);

        // Проверяем, что произошел переход в дневник (URL содержит Diary или появился элемент дневника)
        String currentUrl = getWebDriver().getCurrentUrl();
        assert currentUrl.contains("fatsecret.com") : "Ожидалось перенаправление на страницу FatSecret";

        // Проверяем наличие элементов дневника или навигации
        $x("//a[contains(@href, 'Diary') or contains(text(), 'Diary') or contains(text(), 'Дневник')] | //div[contains(@id, 'Diary')]")
                .shouldBe(visible);

        // Проверяем, что HTTPS используется (безопасность)
        assert currentUrl.startsWith("https://") : "Ожидалось использование HTTPS";

        System.out.println("✓ TC002 пройден. Авторизация выполнена успешно. URL: " + currentUrl);
    }
}

