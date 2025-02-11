package org.easycleanco.easycleancobackend.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

public class ServiceDAO {

	public ArrayList<Service> getAllServices() throws SQLException {
	    ArrayList<Service> serviceList = new ArrayList<>();
	    Service serviceBean = null;

	    Connection conn = null;

	    try {
	        conn = DBConnection.getConnection();
	        String sqlStr = "SELECT service.*, service_category.name as 'category'\r\n"
	        		+ "FROM service\r\n"
	        		+ "INNER JOIN service_category\r\n"
	        		+ "ON service.service_category_id = service_category.id;";
	        
	        Statement stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(sqlStr);

	        while (rs.next()) {
	            serviceBean = new Service();
	            serviceBean.setId(rs.getInt("id"));
	            serviceBean.setServiceCategoryId(rs.getInt("service_category_id"));
	            serviceBean.setServiceCategory(rs.getString("category"));
	            serviceBean.setName(rs.getString("name"));
	            serviceBean.setDescription(rs.getString("description"));
	            serviceBean.setHourlyRate(rs.getInt("hourly_rate"));
	            serviceBean.setImage(rs.getString("image"));

	            serviceList.add(serviceBean);
	            System.out.print("....done writing to service bean!......");
	        }
	    } catch (Exception e) {
	        System.out.print(".......ServiceDB:" + e);
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }

	    return serviceList;
	}
	
	public ArrayList<Service> getServicesByCategory(int categoryId) throws SQLException {
	    ArrayList<Service> serviceList = new ArrayList<>();
	    Connection conn = null;

	    try {
	        conn = DBConnection.getConnection();
	        String sqlStr = "SELECT service.*, service_category.name AS 'category' " +
	                        "FROM service " +
	                        "INNER JOIN service_category ON service.service_category_id = service_category.id " +
	                        "WHERE service.service_category_id = ?";

	        PreparedStatement pstmt = conn.prepareStatement(sqlStr);
	        pstmt.setInt(1, categoryId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Service serviceBean = new Service();
	            serviceBean.setId(rs.getInt("id"));
	            serviceBean.setServiceCategoryId(rs.getInt("service_category_id"));
	            serviceBean.setServiceCategory(rs.getString("category"));
	            serviceBean.setName(rs.getString("name"));
	            serviceBean.setDescription(rs.getString("description"));
	            serviceBean.setHourlyRate(rs.getInt("hourly_rate"));
	            serviceBean.setImage(rs.getString("image"));

	            serviceList.add(serviceBean);
	            System.out.println("....done writing to service bean!......");
	        }
	    } catch (Exception e) {
	        System.out.println(".......ServiceDB Error: " + e);
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }

	    return serviceList;
	}

	
	public ArrayList<Service> getAllServicesWithRating() throws SQLException {
	    ArrayList<Service> serviceList = new ArrayList<>();
	    Service serviceBean = null;

	    Connection conn = null;

	    try {
	        conn = DBConnection.getConnection();
	        String sqlStr = "SELECT s.id AS service_id,\r\n"
	        		+ "       s.name AS service_name,\r\n"
	        		+ "       s.description AS service_description,\r\n"
	        		+ "       s.hourly_rate AS hourly_rate,\r\n"
	        		+ "       sc.id AS service_category_id,\r\n"
	        		+ "       sc.name AS service_category,\r\n"
	        		+ "       AVG(f.rating) AS average_rating\r\n"
	        		+ "FROM service s\r\n"
	        		+ "LEFT JOIN service_category sc ON s.service_category_id = sc.id\r\n"
	        		+ "LEFT JOIN booking b ON s.id = b.service_id\r\n"
	        		+ "LEFT JOIN feedback f ON b.id = f.booking_id\r\n"
	        		+ "GROUP BY s.id, sc.id\r\n"
	        		+ "HAVING AVG(f.rating) > 0\r\n"
	        		+ "ORDER BY average_rating DESC;";
	        
	        Statement stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(sqlStr);

	        while (rs.next()) {
	            serviceBean = new Service();
	            serviceBean.setId(rs.getInt("service_id"));
	            serviceBean.setServiceCategoryId(rs.getInt("service_category_id"));
	            serviceBean.setServiceCategory(rs.getString("service_category"));
	            serviceBean.setName(rs.getString("service_name"));
	            serviceBean.setDescription(rs.getString("service_description"));
	            serviceBean.setHourlyRate(rs.getInt("hourly_rate"));
	            serviceBean.setRating(rs.getInt("average_rating"));
	            
	            serviceList.add(serviceBean);
	            System.out.print("....done writing to service bean!......");
	        }
	    } catch (Exception e) {
	        System.out.print(".......ServiceDB:" + e);
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }

	    return serviceList;
	}
	
	
	public ArrayList<Service> getAllServicesWithBookingCount() throws SQLException {
	    ArrayList<Service> serviceList = new ArrayList<>();
	    Service serviceBean = null;

	    Connection conn = null;

	    try {
	        conn = DBConnection.getConnection();
	        String sqlStr = "SELECT \r\n"
	        		+ "    s.id AS service_id,\r\n"
	        		+ "    s.name AS service_name,\r\n"
	        		+ "    s.description,\r\n"
	        		+ "    s.hourly_rate,\r\n"
	        		+ "    sc.name AS category_name,\r\n"
	        		+ "    COUNT(b.id) AS booking_count\r\n"
	        		+ "FROM \r\n"
	        		+ "    service s\r\n"
	        		+ "JOIN \r\n"
	        		+ "    service_category sc ON s.service_category_id = sc.id\r\n"
	        		+ "LEFT JOIN \r\n"
	        		+ "    booking b ON s.id = b.service_id\r\n"
	        		+ "GROUP BY \r\n"
	        		+ "    s.id, s.name, sc.name\r\n"
	        		+ "HAVING \r\n"
	        	    + "COUNT(b.id) > 0 \r\n"
	        		+ "ORDER BY \r\n"
	        		+ "    booking_count DESC; -- Sorting by most booked services\r\n"
	        		+ "";
	        
	        Statement stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(sqlStr);

	        while (rs.next()) {
	            serviceBean = new Service();
	            serviceBean.setId(rs.getInt("service_id"));
	            serviceBean.setServiceCategory(rs.getString("category_name"));
	            serviceBean.setName(rs.getString("service_name"));
	            serviceBean.setDescription(rs.getString("description"));
	            serviceBean.setHourlyRate(rs.getInt("hourly_rate"));
	            serviceBean.setBookingCount(rs.getInt("booking_count"));
	            
	            serviceList.add(serviceBean);
	            System.out.print("....done writing to service bean!......");
	        }
	    } catch (Exception e) {
	        System.out.print(".......ServiceDB:" + e);
	    } finally {
	        if (conn != null) {
	            conn.close();
	        }
	    }

	    return serviceList;
	}


	public int insertService(int serviceCategoryId, String name, String description, int hourlyRate, String image)
			throws SQLException, IOException, ClassNotFoundException {
		Connection conn = null;
		int nrow = 0;
		try {
			conn = DBConnection.getConnection();
			String sqlStr = "INSERT INTO service (service_category_id, name, description, hourly_rate, image) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sqlStr);

			pstmt.setInt(1, serviceCategoryId);
			pstmt.setString(2, name);
			pstmt.setString(3, description);
			pstmt.setInt(4, hourlyRate);
			pstmt.setString(5, image);

			nrow = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} 
		return nrow;
	}

	public int updateService(int serviceId, int serviceCategoryId, String name, String description, int hourlyRate, String image)
	        throws SQLException, IOException, ClassNotFoundException {
	    Connection conn = null;
	    int nrow = 0;
	    try {
	        conn = DBConnection.getConnection();
	        
	        // The SQL query will now update an existing service record, using serviceId to locate it
	        String sqlStr = "UPDATE service SET service_category_id = ?, name = ?, description = ?, hourly_rate = ?, image = ? WHERE id = ?";
	        PreparedStatement pstmt = conn.prepareStatement(sqlStr);

	        pstmt.setInt(1, serviceCategoryId);
	        pstmt.setString(2, name);
	        pstmt.setString(3, description);
	        pstmt.setInt(4, hourlyRate);
	        pstmt.setString(5, image);
	        pstmt.setInt(6, serviceId); // Ensure the serviceId is passed to update the correct service

	        nrow = pstmt.executeUpdate(); // Execute the update
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e;
	    } finally {
	        if (conn != null) {
	            conn.close(); // Close the connection
	        }
	    }
	    return nrow; // Returns the number of rows affected
	}
	
	public int deleteService(int serviceId) throws SQLException, IOException, ClassNotFoundException {
	    Connection conn = null;
	    int nrow = 0;
	    try {
	        conn = DBConnection.getConnection();

	        // The SQL query will delete a service record based on serviceId
	        String sqlStr = "DELETE FROM service WHERE id = ?";
	        PreparedStatement pstmt = conn.prepareStatement(sqlStr);

	        pstmt.setInt(1, serviceId); // Set the serviceId to delete the correct service

	        nrow = pstmt.executeUpdate(); // Execute the delete

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e;
	    } finally {
	        if (conn != null) {
	            conn.close(); // Close the connection
	        }
	    }
	    return nrow; // Returns the number of rows affected
	}



}
