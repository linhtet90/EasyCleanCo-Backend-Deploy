package org.easycleanco.easycleancobackend.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BookingDAO {

    public int createBooking(Booking booking) throws SQLException, ClassNotFoundException {
        int affectedRows = 0;
        try {
            Connection connection = DBConnection.getConnection();
            String query = "INSERT INTO booking (customer_id, service_id, duration, scheduled_date_time, address, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, booking.getCustomerId());
            preparedStatement.setInt(2, booking.getServiceId());
            preparedStatement.setInt(3, booking.getDuration());
            preparedStatement.setString(4, booking.getScheduledDateTime());
            preparedStatement.setString(5, booking.getAddress());
            preparedStatement.setString(6, booking.getNotes());
            affectedRows = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return affectedRows;
    }

    public ArrayList<Booking> getAllBookings(String startDate, String endDate, String status, String serviceName) throws SQLException, ClassNotFoundException {
        ArrayList<Booking> bookings = new ArrayList<>();
        try {
            Connection connection = DBConnection.getConnection();
            StringBuilder query = new StringBuilder("SELECT b.id, b.customer_id, c.name AS customer_name, b.service_id, s.name AS service_name, b.duration, b.scheduled_date_time, b.address, b.status, b.notes, s.hourly_rate FROM booking b JOIN service s ON b.service_id = s.id JOIN customer c ON b.customer_id = c.id");

            boolean hasStartDate = startDate != null && !startDate.isEmpty();
            boolean hasEndDate = endDate != null && !endDate.isEmpty();
            boolean hasStatus = status != null && !status.isEmpty();
            boolean hasServiceName = serviceName != null && !serviceName.isEmpty();

            if (hasStartDate || hasEndDate || hasStatus || hasServiceName) {
                query.append(" WHERE");
                boolean firstCondition = true;
                if (hasStartDate && hasEndDate) {
                    query.append(" DATE(b.scheduled_date_time) BETWEEN ? AND ?");
                    firstCondition = false;
                }
                if (hasStatus) {
                    if (!firstCondition) {
                        query.append(" AND");
                    }
                    query.append(" b.status = ?");
                    firstCondition = false;
                }
                if (hasServiceName) {
                    if (!firstCondition) {
                        query.append(" AND");
                    }
                    query.append(" s.name = ?");
                }
            }

            query.append(" ORDER BY b.scheduled_date_time DESC");

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

            int paramIndex = 1;
            if (hasStartDate && hasEndDate) {
                preparedStatement.setString(paramIndex++, startDate);
                preparedStatement.setString(paramIndex++, endDate);
            }
            if (hasStatus) {
                preparedStatement.setString(paramIndex++, status);
            }

            if (hasServiceName) {
                preparedStatement.setString(paramIndex, serviceName);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Booking booking = new Booking();
                booking.setBookingId(resultSet.getInt("id"));
                booking.setCustomerId(resultSet.getInt("customer_id"));
                booking.setCustomerName(resultSet.getString("customer_name"));
                booking.setServiceId(resultSet.getInt("service_id"));
                booking.setServiceName(resultSet.getString("service_name"));
                booking.setHourlyRate(resultSet.getInt("hourly_rate"));
                booking.setDuration(resultSet.getInt("duration"));
                booking.setScheduledDateTime(resultSet.getString("scheduled_date_time"));
                booking.setAddress(resultSet.getString("address"));
                booking.setStatus(resultSet.getString("status"));
                booking.setNotes(resultSet.getString("notes"));
                bookings.add(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public int updateBookingStatus(int bookingId, String status) throws SQLException, ClassNotFoundException {
        int affectedRows = 0;
        try {
            Connection connection = DBConnection.getConnection();
            String query = "UPDATE booking SET status = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, bookingId);
            affectedRows = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return affectedRows;
    }

    public Map<String, Integer> getBookingsByDay() throws SQLException, ClassNotFoundException {
            Map<String, Integer> dailyBookings = new TreeMap<>();
            try {
                Connection connection = DBConnection.getConnection();
                String query = "SELECT DATE(scheduled_date_time) AS day, COUNT(*) AS total FROM booking GROUP BY day ORDER BY day";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    dailyBookings.put(resultSet.getString("day"), resultSet.getInt("total"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dailyBookings;
        }

    public Map<String, Integer> getBookingsByWeek() throws SQLException, ClassNotFoundException {
            Map<String, Integer> weeklyBookings = new TreeMap<>();
            try {
                Connection connection = DBConnection.getConnection();
                String query = "SELECT YEARWEEK(scheduled_date_time) AS week, COUNT(*) AS total FROM booking GROUP BY week ORDER BY week";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    weeklyBookings.put(resultSet.getString("week"), resultSet.getInt("total"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return weeklyBookings;
        }

    public Map<String, Integer> getBookingsByMonth() throws SQLException, ClassNotFoundException {
            Map<String, Integer> monthlyBookings = new TreeMap<>();
            try {
                Connection connection = DBConnection.getConnection();
                String query = "SELECT DATE_FORMAT(scheduled_date_time, '%Y-%m') AS month, COUNT(*) AS total FROM booking GROUP BY month ORDER BY month";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    monthlyBookings.put(resultSet.getString("month"), resultSet.getInt("total"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return monthlyBookings;
    }
}
