package org.easycleanco.easycleancobackend.models;

public class User {
	private int id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private Address address;
    private int totalBooking;
    private String retentionStatus;
    
    
	public String getRetentionStatus() {
		return retentionStatus;
	}
	public void setRetentionStatus(String retentionStatus) {
		this.retentionStatus = retentionStatus;
	}
	public int getTotalBooking() {
		return totalBooking;
	}
	public void setTotalBooking(int totalBooking) {
		this.totalBooking = totalBooking;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}

    
}