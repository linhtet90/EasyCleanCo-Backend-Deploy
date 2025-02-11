package org.easycleanco.easycleancobackend.controllers;

import java.util.ArrayList;

import org.easycleanco.easycleancobackend.models.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class CustomerController {
	
	@RequestMapping(method = RequestMethod.GET, path = "/getAllUsers")
	public ArrayList<User> getAllUsers() {
	    ArrayList<User> myList = new ArrayList<>();
	    try {
	        UserDAO db = new UserDAO();
	        myList = db.getAllUsers(); // Call getAllUsers method in UserDAO
	    } catch (Exception e) {
	        System.out.println("Error :" + e);
	    }
	    return myList;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/getAllUsersWithStatus")
	public ArrayList<User> getAllUsersWithStatus() {
	    ArrayList<User> myList = new ArrayList<>();
	    try {
	        UserDAO db = new UserDAO();
	        myList = db.getAllUsersWithStatus(); 
	    } catch (Exception e) {
	        System.out.println("Error :" + e);
	    }
	    return myList;
	}


	@RequestMapping(path = "/createUser", consumes = "application/json", method = RequestMethod.POST)
	public int createUser(@RequestBody User user) {
	    int rec = 0;
	    try {
	        // Create an instance of UserDAO
	        UserDAO db = new UserDAO();

	        // Call the modified insertUser method that handles both customer and address
	        rec = db.insertUser(user);

	        System.out.println("...done create user.." + rec);
	    } catch (Exception e) {
	        System.out.println("Error: " + e.toString());
	    }

	    return rec;
	}

	
	@RequestMapping(path = "/updateUser/{uid}", consumes = "application/json", method = RequestMethod.PUT)
	public int updateUser(@PathVariable String uid, @RequestBody User user) {
	    int rec = 0;
	    try {
	        UserDAO db = new UserDAO();
	        // Set the user ID to the provided UID (from path variable)
	        user.setId(Integer.parseInt(uid));
	        rec = db.updateUser(user); // Call the updateUser method in UserDAO
	        System.out.print(" ... in UserController - done updating user .. " + rec);
	    } catch (Exception e) {
	        System.out.print(e.toString());
	    }
	    return rec;
	}

	
	@RequestMapping(path = "/deleteUser/{uid}", method = RequestMethod.DELETE)
	public int deleteUser(@PathVariable String uid) {
	    int rec = 0;
	    try {
	        UserDAO db = new UserDAO();
	        rec = db.deleteUser(Integer.parseInt(uid)); // Parse the UID to integer and call deleteUser method in UserDAO
	        System.out.print(" ... in UserController - done deleting user .. " + rec);
	    } catch (Exception e) {
	        System.out.print(e.toString());
	    }
	    return rec;
	}


}
