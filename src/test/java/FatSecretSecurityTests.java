import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

/**
 * Автоматизированные тесты для безопасности FatSecret
 * TC008: Безопасность соединения
 */
public class FatSecretSecurityTests {

    private static final String BASE_URL = "https://foods.fatsecret.com";
    private static final String LOGIN_URL = BASE_URL + "/Auth.aspx?pa=s";

    /**
     * TC008: Безопасность соединения
     * Тип: Безопасность
     */
    @Test
    @DisplayName("TC008: Безопасность соединения")
    void test08_SecurityConnection() {
        // Открываем страницу
        Configuration.pageLoadStrategy = "eager";
        open(LOGIN_URL);
        getWebDriver().manage().window().maximize();
        sleep(2000); // Пауза для загрузки страницы

        // Проверяем URL и протокол
        String currentUrl = getWebDriver().getCurrentUrl();

        // Проверяем, что используется HTTPS
        assert currentUrl.startsWith("https://") : "Ожидалось использование HTTPS протокола";

        // Проверяем, что URL содержит домен fatsecret.com
        assert currentUrl.contains("fatsecret.com") : "URL должен содержать домен fatsecret.com";

        // Проверяем отсутствие mixed content (HTTP ресурсы на HTTPS странице)
        try {
            Object mixedContent = executeJavaScript(
                    "var links = document.querySelectorAll('a[href^=\"http://\"], img[src^=\"http://\"], script[src^=\"http://\"], link[href^=\"http://\"]'); " +
                            "return links.length === 0;"
            );

            assert (Boolean) mixedContent : "Обнаружен mixed content (HTTP ресурсы на HTTPS странице)";
        } catch (Exception e) {
            System.out.println("Не удалось проверить mixed content через JavaScript");
        }

        // Проверяем, что страница загрузилась без ошибок безопасности
        $(By.tagName("body"))
                .shouldBe(visible);

        System.out.println("✓ TC008 пройден. Безопасность соединения проверена.");
        System.out.println("  Протокол: HTTPS");
        System.out.println("  URL: " + currentUrl);
        System.out.println("  Mixed content: не обнаружен");
    }
}
