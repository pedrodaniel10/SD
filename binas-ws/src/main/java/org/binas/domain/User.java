package org.binas.domain;

import java.util.HashMap;
import java.util.Map;

import org.binas.ws.EmailExists;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;

import exceptions.Exceptions;

public class User {
	private static Map<String, User> users = new HashMap<String, User>();
	
	private static int DEFAULT_CREDIT = 10;
	
	private final String email;
	private int credit;
	private boolean hasBina;
	
	/**
	 * @param email
	 * @throws EmailExists_Exception 
	 * @throws InvalidEmail_Exception 
	 */
	public User(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		checkEmail(email);
		this.email = email;
		this.credit = DEFAULT_CREDIT;
		this.hasBina = false;
		
		this.addUser(this);
	}
	
	/**
	 * @param email
	 * @throws EmailExists_Exception 
	 * @throws InvalidEmail_Exception 
	 */
	private void checkEmail(String email) throws EmailExists_Exception, InvalidEmail_Exception{
		final String regex = "^(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)@(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)";
		
		if(email == null || email.trim().equals("")){
			Exceptions.throwInvalidEmail("The email can not be null or empty.");
		}
		else if(!email.matches(regex)){
			Exceptions.throwInvalidEmail("The email " + email + " format is invalid.");
		}
		
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the credit
	 */
	public synchronized int getCredit() {
		return credit;
	}
	
	public synchronized void addCredit(int amount){
		this.credit += amount;
	}
	
	public synchronized void substractCredit(int amount) throws NoCredit_Exception{
		if(this.credit - amount < 0){
			Exceptions.throwNoCredit("No credit avaiable for the operation.");
		}
		this.credit -= amount;
	}

	/**
	 * @return the hasBina
	 */
	public synchronized boolean isHasBina() {
		return hasBina;
	}

	/**
	 * @param hasBina the hasBina to set
	 */
	public synchronized void setHasBina(boolean hasBina) {
		this.hasBina = hasBina;
	}
	
	private synchronized void addUser(User user) throws EmailExists_Exception{
		if(users.containsKey(user.getEmail())){
			Exceptions.throwEmailExists("The email " + user.getEmail() + " already exists.");
			return;
		}
		users.put(user.getEmail(), user);
	}
	
	/**
	 * @param email
	 * @return user or null if does not exist
	 * @throws UserNotExists_Exception 
	 */
	public static User getUser(String email) throws UserNotExists_Exception{
		User user = users.get(email);
		
		if(user == null){
			Exceptions.throwUserNotExists("The user with email " + email + "does not exists.");
		}
		return user;
	}
	
	
	public synchronized UserView getUserView(){
		UserView userView= new UserView();
		
		userView.setEmail(this.email);
		userView.setCredit(this.credit);
		userView.setHasBina(this.hasBina);
		
		return userView;
	}

	public static void clear(){
		users.clear();
	}
}
