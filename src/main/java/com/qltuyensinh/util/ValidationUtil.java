package com.qltuyensinh.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return true; // email không bắt buộc
        return email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return true;
        return phone.matches("^0\\d{9,10}$");
    }

    public static boolean isValidScore(String scoreStr) {
        try {
            double score = Double.parseDouble(scoreStr);
            // Điểm từng môn tối đa 10; tổng điểm xét tuyển được cộng từ 3 môn.
            return score >= 0 && score <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidCccd(String cccd) {
        return cccd != null && cccd.matches("\\d{12}");
    }

    public static boolean isNonNegativeInteger(String value) {
        try { return Integer.parseInt(value.trim()) >= 0; }
        catch (Exception e) { return false; }
    }

    public static boolean isNonNegativeNumber(String value) {
        try { return Double.parseDouble(value.trim()) >= 0; }
        catch (Exception e) { return false; }
    }

    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FMT);
    }
}
