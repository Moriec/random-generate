package com.vinogradov.util;

import com.vinogradov.model.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {
    public static void exportToCsv(List<User> users, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");

        PrintWriter writer = response.getWriter();
        writer.println("\uFEFFFull Name,Email,Phone,Address,Birth Date,Age,Gender,Country"); // BOM for UTF-8

        for (User user : users) {
            String fullName = escapeCsvField(user.getFullName());
            String email = escapeCsvField(user.getEmail());
            String phone = escapeCsvField(user.getPhone());
            String address = escapeCsvField(user.getAddress());
            String birthDate = escapeCsvField(user.getBirthDate());
            String gender = escapeCsvField(user.getGender());
            String country = escapeCsvField(user.getCountry());

            writer.printf("%s,%s,%s,%s,%s,%d,%s,%s%n",
                    fullName, email, phone, address, birthDate, user.getAge(), gender, country);
        }

        writer.flush();
    }

    private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}



