package com.vinogradov.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinogradov.config.FreeMarkerConfig;
import com.vinogradov.model.User;
import com.vinogradov.service.UserService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final UserService userService = new UserService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("generate".equals(action)) {
            handleGenerateRequest(request, response);
        } else {
            showMainPage(request, response);
        }
    }

    private void showMainPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Configuration cfg = FreeMarkerConfig.getInstance(getServletContext());
            Template template = cfg.getTemplate("index.ftl");

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("title", "Random User Generator");
            dataModel.put("contextPath", request.getContextPath());

            response.setContentType("text/html; charset=UTF-8");
            template.process(dataModel, response.getWriter());
        } catch (TemplateException e) {
            logger.error("Ошибка при обработке шаблона: {}", e.getMessage(), e);
            throw new ServletException("Ошибка при обработке шаблона", e);
        }
    }

    private void handleGenerateRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int count = Integer.parseInt(request.getParameter("count"));
            if (count < 1 || count > 100) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Количество должно быть от 1 до 100\"}");
                return;
            }

            String gender = request.getParameter("gender");
            if (gender == null || gender.isEmpty()) {
                gender = "all";
            }

            String nationality = request.getParameter("nationality");
            if (nationality == null) {
                nationality = "";
            }

            List<User> users = userService.fetchUsers(count, gender, nationality);
            
            logger.info("Получен список пользователей размером: {}", users != null ? users.size() : "null");

            HttpSession session = request.getSession();
            session.setAttribute("users", users);
            session.setAttribute("generationParams", Map.of(
                    "count", count,
                    "gender", gender,
                    "nationality", nationality
            ));

            logger.info("Сгенерировано {} пользователей. Параметры: count={}, gender={}, nationality={}, timestamp={}",
                    users.size(), count, gender, nationality, System.currentTimeMillis());

            if ("application/json".equals(request.getHeader("Accept"))) {
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(users));
            } else {
                try {
                    Configuration cfg = FreeMarkerConfig.getInstance(getServletContext());
                    Template template = cfg.getTemplate("user_cards.ftl");

                    Map<String, Object> dataModel = new HashMap<>();
                    dataModel.put("users", users);

                    response.setContentType("text/html; charset=UTF-8");
                    template.process(dataModel, response.getWriter());
                } catch (TemplateException e) {
                    logger.error("Ошибка при обработке шаблона: {}", e.getMessage(), e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\": \"Ошибка при обработке шаблона\"}");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Неверный формат количества\"}");
            logger.error("Ошибка парсинга параметра count: {}", e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
            logger.error("Ошибка при генерации пользователей: {}", e.getMessage(), e);
        }
    }
}

