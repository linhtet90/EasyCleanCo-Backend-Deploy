package org.easycleanco.easycleancobackend.models;

import java.sql.*;
import java.util.ArrayList;

public class UserDAO {

	public ArrayList<User> getAllUsers() throws SQLException, ClassNotFoundException {
		ArrayList<User> userList = new ArrayList<>();
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			String sql = "SELECT c.id, c.email, c.password, c.name, c.phone_number, a.floor, a.apt_no, a.block, a.address_line, a.postal_code "
					+ "FROM customer c " + "JOIN address a ON c.id = a.customer_id";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setName(rs.getString("name"));
				user.setPhoneNumber(rs.getString("phone_number"));

				Address address = new Address();
				address.setFloor(rs.getInt("floor"));
				address.setAptNo(rs.getInt("apt_no"));
				address.setBlock(rs.getInt("block"));
				address.setAddressLine(rs.getString("address_line"));
				address.setPostalCode(rs.getInt("postal_code"));

				user.setAddress(address);
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.close(); // Close connection
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return userList;
	}

	
	public ArrayList<User> getAllUsersWithStatus() throws SQLException, ClassNotFoundException {
		ArrayList<User> userList = new ArrayList<>();
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			String sql = "SELECT c.id, c.name, c.email, c.phone_number, \r\n"
					+ "       COUNT(b.id) AS total_bookings,\r\n"
					+ "       (CASE \r\n"
					+ "            WHEN COUNT(b.id) >= 5 AND MAX(b.scheduled_date_time) >= NOW() - INTERVAL 3 MONTH THEN 'Active'\r\n"
					+ "            WHEN COUNT(b.id) = 0 OR MAX(b.scheduled_date_time) < NOW() - INTERVAL 6 MONTH THEN 'Inactive'\r\n"
					+ "        END) AS retention_status,\r\n"
					+ "       a.address_line, a.postal_code\r\n"
					+ "FROM customer c\r\n"
					+ "LEFT JOIN booking b ON c.id = b.customer_id\r\n"
					+ "LEFT JOIN address a ON c.id = a.customer_id\r\n"
					+ "GROUP BY c.id, a.address_line, a.postal_code\r\n"
					+ "HAVING retention_status IN ('Active', 'Inactive')\r\n"
					+ "ORDER BY retention_status DESC;";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
				user.setPhoneNumber(rs.getString("phone_number"));
				user.setTotalBooking(rs.getInt("total_bookings"));;
				user.setRetentionStatus(rs.getString("retention_status"));

				Address address = new Address();
				address.setAddressLine(rs.getString("address_line"));
				address.setPostalCode(rs.getInt("postal_code"));

				user.setAddress(address);
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.close(); // Close connection
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return userList;
	}
	
	
	public int insertUser(User user) throws SQLException, ClassNotFoundException {
		int nrow = 0;
		Connection conn = null;

		try {
			// Get connection and start transaction
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false); // Start transaction

			// Insert user into the customer table
			String sqlInsertUser = "INSERT INTO customer (email, password, name, phone_number) VALUES (?, ?, ?, ?)";
			PreparedStatement pstmtUser = conn.prepareStatement(sqlInsertUser, Statement.RETURN_GENERATED_KEYS);
			pstmtUser.setString(1, user.getEmail());
			pstmtUser.setString(2, user.getPassword());
			pstmtUser.setString(3, user.getName());
			pstmtUser.setString(4, user.getPhoneNumber());

			int affectedRows = pstmtUser.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating customer failed, no rows affected.");
			}

			// Retrieve the generated customer ID
			ResultSet generatedKeys = pstmtUser.getGeneratedKeys();
			int customerId = 0;
			if (generatedKeys.next()) {
				customerId = generatedKeys.getInt(1); // This is the ID of the newly inserted customer
			}

			// Insert address using the generated customer ID
			String sqlInsertAddress = "INSERT INTO address (floor, apt_no, block, address_line, postal_code, customer_id) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmtAddress = conn.prepareStatement(sqlInsertAddress);
			pstmtAddress.setInt(1, user.getAddress().getFloor());
			pstmtAddress.setInt(2, user.getAddress().getAptNo());
			pstmtAddress.setInt(3, user.getAddress().getBlock());
			pstmtAddress.setString(4, user.getAddress().getAddressLine());
			pstmtAddress.setInt(5, user.getAddress().getPostalCode());
			pstmtAddress.setInt(6, customerId);

			pstmtAddress.executeUpdate();

			// Commit the transaction
			conn.commit();
			nrow = 1; // Success
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback(); // Rollback in case of error
				} catch (SQLException rollbackEx) {
					e.addSuppressed(rollbackEx); // Add rollback exception as suppressed
				}
			}
			e.printStackTrace();
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true); // Restore auto-commit mode
					conn.close(); // Close connection
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return nrow;
	}

	public int updateUser(User user) throws SQLException, ClassNotFoundException {
		int nrow = 0;
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false); // Start transaction

			// Update user in the customer table
			String sqlUpdateUser = "UPDATE customer SET email = ?, password = ?, name = ?, phone_number = ? WHERE id = ?";
			PreparedStatement pstmtUser = conn.prepareStatement(sqlUpdateUser);
			pstmtUser.setString(1, user.getEmail());
			pstmtUser.setString(2, user.getPassword());
			pstmtUser.setString(3, user.getName());
			pstmtUser.setString(4, user.getPhoneNumber());
			pstmtUser.setInt(5, user.getId());

			int affectedRows = pstmtUser.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Updating customer failed, no rows affected.");
			}

			// Update address using the customer ID
			String sqlUpdateAddress = "UPDATE address SET floor = ?, apt_no = ?, block = ?, address_line = ?, postal_code = ? WHERE customer_id = ?";
			PreparedStatement pstmtAddress = conn.prepareStatement(sqlUpdateAddress);
			pstmtAddress.setInt(1, user.getAddress().getFloor());
			pstmtAddress.setInt(2, user.getAddress().getAptNo());
			pstmtAddress.setInt(3, user.getAddress().getBlock());
			pstmtAddress.setString(4, user.getAddress().getAddressLine());
			pstmtAddress.setInt(5, user.getAddress().getPostalCode());
			pstmtAddress.setInt(6, user.getId());

			pstmtAddress.executeUpdate();

			// Commit the transaction
			conn.commit();
			nrow = 1; // Success
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback(); // Rollback in case of error
				} catch (SQLException rollbackEx) {
					e.addSuppressed(rollbackEx); // Add rollback exception as suppressed
				}
			}
			e.printStackTrace();
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true); // Restore auto-commit mode
					conn.close(); // Close connection
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return nrow;
	}

	public int deleteUser(int userId) throws SQLException, ClassNotFoundException {
		int nrow = 0;
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false); // Start transaction

			// Delete address first (because of foreign key constraint)
			String sqlDeleteAddress = "DELETE FROM address WHERE customer_id = ?";
			PreparedStatement pstmtAddress = conn.prepareStatement(sqlDeleteAddress);
			pstmtAddress.setInt(1, userId);
			pstmtAddress.executeUpdate();

			// Delete user from customer table
			String sqlDeleteUser = "DELETE FROM customer WHERE id = ?";
			PreparedStatement pstmtUser = conn.prepareStatement(sqlDeleteUser);
			pstmtUser.setInt(1, userId);

			nrow = pstmtUser.executeUpdate();

			// Commit the transaction
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback(); // Rollback in case of error
				} catch (SQLException rollbackEx) {
					e.addSuppressed(rollbackEx); // Add rollback exception as suppressed
				}
			}
			e.printStackTrace();
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true); // Restore auto-commit mode
					conn.close(); // Close connection
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return nrow;
	}

}
