import import import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

/**
 * Автоматизированные тесты для авторизации и регистрации пользователей FatSecret
 * TC001: Регистрация пользователя
 * TC002: Авторизация пользователя
 */
public class FatSecretAuthTests {

    private static final String REGISTRATION_URL = "https://foods.fatsecret.com/Default.aspx?pa=m";
    private static final String LOGIN_URL = "https://foods.fatsecret.com/Auth.aspx?pa=s";
    
    private static final String TEST_EMAIL = "test.edge@gmail.com";
    private static final String TEST_PASSWORD = "EdgeTest2025!";
    private static final String TEST_USERNAME = "Alice Free";

    /**
     * TC001: Регистрация пользователя
     * Тип: Функциональный
     */
    @Test
    void test01UserRegistration() {
        // Открываем страницу регистрации
        open(REGISTRATION_URL);
        sleep(2000); // Пауза для загрузки страницы
        
        // Вводим имя пользователя
        $(By.xpath("//input[contains(@id, 'Username') or contains(@name, 'Username')]")).setValue(TEST_USERNAME);
        
        // Вводим email
        $(By.xpath("//input[@type='email' or contains(@id, 'Email')]")).setValue(TEST_EMAIL);
        
        // Вводим пароль
        $(By.xpath("//input[@type='password']")).setValue(TEST_PASSWORD);
        
        // Нажимаем на кнопку регистрации
        $(By.xpath("//input[@type='submit' or contains(@value, 'next') or contains(@value, 'Next')]")).click();
        
        // Проверяем, что регистрация прошла успешно
        sleep(2000);
        
        // Проверяем, что мы не на странице регистрации
        $(By.tagName("body")).shouldNotHave(text("Register"));
        
        sleep(3000); // Пауза для просмотра результата
    }

    /**
     * TC002: Авторизация пользователя
     * Тип: Функциональный
     */
    @Test
    void test02UserLogin() {
        // Открываем страницу авторизации
        open(LOGIN_URL);
        sleep(2000); // Пауза для загрузки страницы
        
        // Вводим логин (используем точный ID из HTML)
        $(By.id("ctl11_Logincontrol1_Name")).setValue(TEST_USERNAME);
        
        // Вводим пароль (используем точный ID из HTML)
        $(By.id("ctl11_Logincontrol1_Password")).setValue(TEST_PASSWORD);
        
        // Нажимаем на кнопку Войти (используем точный ID из HTML)
        $(By.id("ctl11_Logincontrol1_Login")).click();
        
        // Проверяем, что авторизация прошла успешно
        sleep(2000);
        
        // Проверяем, что мы перешли в дневник
        $(By.tagName("body")).shouldNotHave(text("Войти"));
        
        sleep(3000); // Пауза для просмотра результата
    }
}

