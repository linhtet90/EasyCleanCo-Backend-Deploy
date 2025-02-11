package org.easycleanco.easycleancobackend.controllers;

import java.util.ArrayList;


import org.easycleanco.easycleancobackend.models.*;
import org.springframework.web.bind.annotation.*;



@RestController
public class ServiceController {
	@CrossOrigin(origins = "http://localhost:8080")
	@RequestMapping(method = RequestMethod.GET, path = "/getAllServices")
	public ArrayList<Service> getAllServices() {

		ArrayList<Service> serviceList = new ArrayList<>();
		try {
			ServiceDAO db = new ServiceDAO();
			serviceList = db.getAllServices();
			
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return serviceList;
	}
	
	@GetMapping("/getServicesByCategory/{categoryId}") 
    public ArrayList<Service> getServicesByCategory(@PathVariable int categoryId) {
        ArrayList<Service> serviceList = new ArrayList<>();
        try {
            ServiceDAO db = new ServiceDAO();
            serviceList = db.getServicesByCategory(categoryId);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return serviceList;
    }
	
	@RequestMapping(method = RequestMethod.GET, path = "/getAllServicesWithRating")
	public ArrayList<Service> getAllServicesWithRating() {

		ArrayList<Service> serviceList = new ArrayList<>();
		try {
			ServiceDAO db = new ServiceDAO();
			serviceList = db.getAllServicesWithRating();
			
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return serviceList;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/getAllServicesWithBookingCount")
	public ArrayList<Service> getAllServicesWithBookingCount() {

		ArrayList<Service> serviceList = new ArrayList<>();
		try {
			ServiceDAO db = new ServiceDAO();
			serviceList = db.getAllServicesWithBookingCount();
			
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return serviceList;
	}
	
	@RequestMapping(path = "/createService", consumes = "application/json", method = RequestMethod.POST)
	public int createService(@RequestBody Service service) {
	    int rec = 0;
	    try {
	        // Create an instance of ServiceDAO
	        ServiceDAO db = new ServiceDAO();

	        // Call the insertService method to insert the service details into the database
	        rec = db.insertService(service.getServiceCategoryId(), 
	                               service.getName(), 
	                               service.getDescription(), 
	                               service.getHourlyRate(), 
	                               service.getImage());

	        System.out.println("...done create service.." + rec);
	    } catch (Exception e) {
	        System.out.println("Error: " + e.toString());
	    }

	    return rec;
	}
	
	@RequestMapping(path = "/updateService", consumes = "application/json", method = RequestMethod.PUT)
	public int updateService(@RequestBody Service service) {
	    int rec = 0;
	    try {
	        // Create an instance of ServiceDAO
	        ServiceDAO db = new ServiceDAO();

	        // Call the updateService method to update the service details in the database
	        rec = db.updateService(service.getId(),            // Pass the service ID to locate the service
	                               service.getServiceCategoryId(),
	                               service.getName(),
	                               service.getDescription(),
	                               service.getHourlyRate(),
	                               service.getImage());

	        System.out.println("...done updating service.." + rec);
	    } catch (Exception e) {
	        System.out.println("Error: " + e.toString());
	    }

	    return rec;
	}

	@RequestMapping(path = "/deleteService/{serviceId}", method = RequestMethod.DELETE)
	public int deleteService(@PathVariable("serviceId") int serviceId) {
	    int rec = 0;
	    try {
	        // Create an instance of ServiceDAO
	        ServiceDAO db = new ServiceDAO();

	        // Call the deleteService method to delete the service from the database
	        rec = db.deleteService(serviceId); // Pass the service ID to delete

	        System.out.println("...done deleting service.." + rec);
	    } catch (Exception e) {
	        System.out.println("Error: " + e.toString());
	    }

	    return rec;
	}
	
}
