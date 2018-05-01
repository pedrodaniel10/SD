package org.binas.station.domain;

import org.binas.station.domain.exception.InvalidCreditException;
import org.binas.station.domain.exception.InvalidFormatEmailException;

public class User {
	private final String email;
	private int credit;
	private int tag;
	private int clientID;
	
	/**
	 * @param email
	 * @throws InvalidEmailException 
	 * @throws EmailExists_Exception 
	 * @throws InvalidEmail_Exception 
	 */
	public User(String email, int beginCredit) throws InvalidFormatEmailException {
		checkEmail(email);
		this.email = email;
		this.credit = beginCredit;
	}
	
	/**
	 * @param email
	 * @throws InvalidEmailException 
	 * @throws EmailExists_Exception 
	 * @throws InvalidEmail_Exception 
	 */
	private void checkEmail(String email) throws InvalidFormatEmailException {
		final String regex = "^(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)@(([a-zA-Z0-9]+)|([a-zA-Z0-9]+\\.?[a-zA-Z0-9]+)+)";
		
		if(email == null || email.trim().equals("")){
			throw new InvalidFormatEmailException("The email can not be null or empty.");
		}
		if(!email.matches(regex)){
			throw new InvalidFormatEmailException("The email " + email + " format is invalid.");
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
	
	/**
	 * @param amount
	 * @throws InvalidCreditException 
	 */
	public synchronized void setCredit(int amount) throws InvalidCreditException{
		if( amount < 0) {
			throw new InvalidCreditException("Amount cannot be negative");
		}
		this.credit = amount;
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
}
