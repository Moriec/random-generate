package com.vinogradov.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinogradov.model.ApiResponse;
import com.vinogradov.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String API_BASE_URL = "https://randomuser.me/api/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<User> fetchUsers(int count, String gender, String nationality) {
        logger.info("Запрос на генерацию {} пользователей. Параметры: пол={}, национальность={}, timestamp={}", 
                count, gender, nationality, System.currentTimeMillis());

        List<User> users = new ArrayList<>();
        try {
            String urlString = buildApiUrl(count, gender, nationality);
            logger.debug("Выполнение запроса к API: {}", urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBody.append(line);
                    }
                    
                    String jsonResponse = responseBody.toString();
                    logger.info("Получен ответ от API, длина: {} символов", jsonResponse.length());
                    
                    try {
                        ApiResponse apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);
                        if (apiResponse != null) {
                            users = apiResponse.getResults();
                            if (users == null) {
                                logger.error("Список пользователей null в ответе API");
                                users = new ArrayList<>();
                            } else {
                                logger.info("Успешно получено {} пользователей из API", users.size());
                            }
                        } else {
                            logger.error("ApiResponse равен null после парсинга JSON");
                            users = new ArrayList<>();
                        }
                    } catch (Exception parseException) {
                        logger.error("Ошибка парсинга JSON ответа: {}", parseException.getMessage(), parseException);
                        logger.error("Первые 500 символов ответа: {}", jsonResponse.length() > 500 ? jsonResponse.substring(0, 500) : jsonResponse);
                        users = new ArrayList<>();
                    }
                }
            } else {
                logger.error("Ошибка HTTP при запросе к API: код ответа {}", responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            logger.error("Ошибка при получении данных из API: {}", e.getMessage(), e);
            e.printStackTrace();
        }

        return users;
    }

    private String buildApiUrl(int count, String gender, String nationality) {
        StringBuilder url = new StringBuilder(API_BASE_URL);
        url.append("?results=").append(count);

        if (gender != null && !gender.isEmpty() && !"all".equalsIgnoreCase(gender)) {
            url.append("&gender=").append(gender);
        }

        if (nationality != null && !nationality.isEmpty()) {
            url.append("&nat=").append(nationality);
        }

        return url.toString();
    }
}

