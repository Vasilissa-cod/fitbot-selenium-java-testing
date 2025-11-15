import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

/**
 * Автоматизированные тесты для производительности FatSecret
 * TC007: Производительность загрузки
 */
public class FatSecretPerformanceTests {

    private static final String BASE_URL = "https://foods.fatsecret.com";
    private static final String DIARY_URL = BASE_URL + "/Diary.aspx";
    private static final String LOGIN_URL = BASE_URL + "/Auth.aspx?pa=s";

    private static final String TEST_USERNAME = "Alice Free";
    private static final String TEST_PASSWORD = "EdgeTest2025!";

    @BeforeEach
    void clearCache() {
        // Очищаем кэш браузера перед тестом
        try {
            getWebDriver().manage().deleteAllCookies();
        } catch (Exception e) {
            // Игнорируем ошибку, если драйвер еще не инициализирован
        }
    }

    /**
     * Вспомогательный метод для авторизации
     */
    private void performLogin() {
        sleep(2000); // Пауза для загрузки страницы

        // Вводим имя пользователя
        $(By.xpath("//input[@type='text' or @placeholder='Email or Member Name']"))
                .shouldBe(visible)
                .setValue(TEST_USERNAME);

        // Вводим пароль
        $(By.xpath("//input[@type='password']"))
                .shouldBe(visible)
                .setValue(TEST_PASSWORD);

        // Нажимаем на кнопку Sign In
        $(By.xpath("//input[@type='submit' and contains(@value, 'Sign In')]"))
                .shouldBe(visible)
                .shouldBe(clickable)
                .click();

        sleep(3000); // Пауза после входа
    }

    /**
     * TC007: Производительность загрузки
     * Тип: Производительность
     */
    @Test
    @DisplayName("TC007: Производительность загрузки")
    void test07_PageLoadPerformance() {
        // Засекаем время начала загрузки
        Configuration.pageLoadStrategy = "eager";
        long startTime = System.currentTimeMillis();

        // Открываем страницу авторизации
        open(LOGIN_URL);
        getWebDriver().manage().window().maximize();

        // Авторизация для доступа к дневнику
        try {
            performLogin();
        } catch (Exception e) {
            System.out.println("Пользователь уже авторизован или форма входа не найдена");
        }

        // Открываем страницу дневника после авторизации
        open(DIARY_URL);
        sleep(2000);

        // Измеряем время загрузки страницы
        long navigationStart = executeJavaScript("return performance.timing.navigationStart;");
        long loadEventEnd = executeJavaScript("return performance.timing.loadEventEnd;");

        // Вычисляем метрики производительности
        long fcp = loadEventEnd - navigationStart; // First Contentful Paint (приблизительно)
        long lcp = loadEventEnd - navigationStart; // Largest Contentful Paint (приблизительно)

        // Проверяем, что страница загрузилась
        $(By.tagName("body"))
                .shouldBe(visible);

        // Останавливаем замер времени
        long totalTime = System.currentTimeMillis() - startTime;

        // Вычисляем метрики
        long fcpMs = fcp > 0 ? fcp : totalTime;
        long lcpMs = lcp > 0 ? lcp : totalTime;

        // Проверяем метрики производительности
        assert fcpMs < 1800 : "FCP должен быть <1.8s. Фактическое: " + fcpMs + "ms";
        assert lcpMs < 2500 : "LCP должен быть <2.5s. Фактическое: " + lcpMs + "ms";

        System.out.println("✓ TC007 пройден. Производительность загрузки соответствует требованиям.");
        System.out.println("  FCP (приблизительно): " + fcpMs + "ms");
        System.out.println("  LCP (приблизительно): " + lcpMs + "ms");
        System.out.println("  Общее время загрузки: " + totalTime + "ms");
    }
}
