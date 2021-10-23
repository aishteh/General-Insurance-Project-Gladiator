package com.lti.projectgladiator.ltinsure.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lti.projectgladiator.ltinsure.beans.Address;
import com.lti.projectgladiator.ltinsure.beans.Customer;
import com.lti.projectgladiator.ltinsure.beans.User;
import com.lti.projectgladiator.ltinsure.dto.CustomerDto;
import com.lti.projectgladiator.ltinsure.dto.StatusDto;
import com.lti.projectgladiator.ltinsure.exception.UserServiceException;
import com.lti.projectgladiator.ltinsure.service.EmailService;
import com.lti.projectgladiator.ltinsure.service.UserService;

@RestController
@CrossOrigin
public class UserRegistrationController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	@PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
	public StatusDto register(@RequestBody CustomerDto customerDto) {
		try {
			
			Customer customer = new Customer();
			System.out.println(customerDto.toString());
            customer.setName(customerDto.getName());
            customer.setContactNo(customerDto.getContactNo());
            customer.setDateOfBirth(customerDto.getDateOfBirth());
            customer.setEmailId(customerDto.getEmailId());

            Address address = new Address();
            address.setCity(customerDto.getCity());
            address.setPincode(customerDto.getPincode());
            address.setLandMark(customerDto.getLandmark());
            
            customer.setAddress(address);

            User user = new User();
            user.setEmailId(customerDto.getEmailId());
            user.setPassword(customerDto.getPassword());
            user.setCustomer(customer);
            user.setLastPasswordSet(LocalDate.now());
            user.setCreatedOn(LocalDate.now());
            user.setRole("USER");
            
            userService.register(user);
            
			try {
				System.out.println("enter in mail");
	            SimpleMailMessage registeredMail = new SimpleMailMessage();
	            registeredMail.setFrom("ltinsure.controller@gmail.com"); // email of sender
	            registeredMail.setTo(user.getEmailId());
	            registeredMail.setSubject("Registered successfully");
	            registeredMail.setText("Hi "+customerDto.getName()+",\r\n"
	            		+ "\n"
	            		+"\n"
	            		+ "Welcome to LTInsure. We’re thrilled to see you here!"
	            		+ "\r\n\n"
	            		+ "LTInsure provides the fastest, easiest way to buy or claim a insurance. You can buy an insurance from our portal at an affordable price.\r\n"
	            		
	            		+"We would love to hear what you think of the services and if there is anything we can improve. If you have any questions, please reply to this email. We are always happy to help!"
	            		
	            		);
				
	            emailService.sendEmail(registeredMail);
	            System.out.println("mail Sent");
	            
			}catch(MailException e){
				throw new UserServiceException("Error occured during sending email!");
			}
            
			StatusDto status = new StatusDto();
			status.setMessage("Registered successfully!");
			status.setStatus(StatusDto.StatusType.SUCCESS);
			return status;

		} catch (UserServiceException e) {
			StatusDto status = new StatusDto();
			status.setMessage(e.getMessage());
			status.setStatus(StatusDto.StatusType.FAILURE);
			return status;
		} catch (DataAccessException e) {
			StatusDto status = new StatusDto();
			status.setMessage(e.getMessage());
			status.setStatus(StatusDto.StatusType.FAILURE);
			return status;
		} catch (Exception e) {
			StatusDto status = new StatusDto();
			status.setMessage(e.getMessage());
			status.setStatus(StatusDto.StatusType.FAILURE);
			return status;
		}

	}

}

