package org.easycleanco.easycleancobackend.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class SalesDAO {
    public Map<String, Float> getTop10CustomersWithHighestSpending() throws SQLException, ClassNotFoundException {
        Map<String, Float> top10Customers = new LinkedHashMap<>();
        try {
            Connection connection = DBConnection.getConnection();
            String query = "SELECT c.name, SUM(s.hourly_rate * b.duration) AS total_spent FROM booking b JOIN service s ON b.service_id = s.id JOIN customer c ON b.customer_id = c.id GROUP BY c.name ORDER BY total_spent DESC LIMIT 10";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                top10Customers.put(resultSet.getString("name"), resultSet.getFloat("total_spent"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return top10Customers;
    }
}
