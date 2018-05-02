package org.binas.domain;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.UserView;

import exceptions.InvalidEmailException;
import exceptions.NoCreditException;

public class User {
	private final String email;
	private boolean hasBina;
	
	/**
	 * @param email
	 * @throws InvalidEmailException 
	 * @throws EmailExists_Exception 
	 * @throws InvalidEmail_Exception 
	 */
	public User(String email) throws InvalidEmailException {
		checkEmail(email);
		this.email = email;
		this.hasBina = false;
	}
	
	/**
	 * @param email
	 * @throws InvalidEmailException 
	 * @throws EmailExists_Exception 
	 * @throws InvalidEmail_Exception 
	 */
	private void checkEmail(String email) throws InvalidEmailException {
		final String regex = "^(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)@(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)";
		
		if(email == null || email.trim().equals("")){
			throw new InvalidEmailException("The email can not be null or empty.");
		}
		if(!email.matches(regex)){
			throw new InvalidEmailException("The email " + email + " format is invalid.");
		}	
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
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
		
	/**
	 * @return UserView with the information of the user
	 */
	public synchronized UserView getUserView(){
		UserView userView= new UserView();
		
		userView.setEmail(this.email);
		userView.setHasBina(this.hasBina);
		
		return userView;
	}

}
