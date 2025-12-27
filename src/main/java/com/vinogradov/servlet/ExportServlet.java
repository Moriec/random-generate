package com.vinogradov.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinogradov.model.User;
import com.vinogradov.util.CsvExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/export")
public class ExportServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ExportServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) session.getAttribute("users");

        if (users == null || users.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Нет данных для экспорта\"}");
            return;
        }

        String format = request.getParameter("format");
        if (format == null || format.isEmpty()) {
            format = "json";
        }

        logger.info("Экспорт {} пользователей в формате {}", users.size(), format);

        try {
            if ("csv".equalsIgnoreCase(format)) {
                CsvExporter.exportToCsv(users, response);
            } else if ("json".equalsIgnoreCase(format)) {
                response.setContentType("application/json; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=users.json");
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), users);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Неподдерживаемый формат экспорта\"}");
            }
        } catch (Exception e) {
            logger.error("Ошибка при экспорте данных: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Ошибка при экспорте данных\"}");
        }
    }
}



