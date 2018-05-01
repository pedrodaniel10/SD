package org.binas.station.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.binas.station.domain.exception.InvalidCreditException;
import org.binas.station.domain.exception.InvalidFormatEmailException;
import org.binas.station.domain.exception.UserDoesNotExistsException;

public class UsersManager {
	// Singleton -------------------------------------------------------------

	private UsersManager() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class SingletonHolder {
		private static final UsersManager INSTANCE = new UsersManager();
	}

	public static synchronized UsersManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	// ------------------------------------------------------------------------
	
	private static int DEFAULT_INITIAL_BALANCE = 10;
	private AtomicInteger initialBalance = new AtomicInteger(DEFAULT_INITIAL_BALANCE);

	/**HashMap to save the users*/ 
	private static ConcurrentHashMap<String,User> users = new ConcurrentHashMap<>();
	
	/**Adds a new user to the map
	 * @param email
	 * @return user view
	 * @throws InvalidFormatEmailException 
	 * @throws InvalidCreditException 
	 * @throws InvalidEmailException
	 * @throws EmailExistsException
	 */
	public synchronized boolean addUser(String email, int credit, int tag, int clientID) throws InvalidFormatEmailException, InvalidCreditException {
		if(stringNullOrEmpty(email)){
			throw new InvalidFormatEmailException("The email can not be null or empty.");
		}
		if(users.get(email) == null){
			User user = new User(email, initialBalance.get());
			users.put(email, user);
			user.setClientID(clientID);
			user.setTag(tag);
			try {
				user.setCredit(credit);
			} catch (InvalidCreditException e) {
				throw new InvalidCreditException("Credit Given is invalid.");
			}

			return true;
		}
		return false;
	}
	
	/**
	 * @throws InvalidFormatEmailException 
	 * @param email
	 * @return user
	 * @throws UserNotExistsException
	 * @throws  
	 */
	public User getUser(String email) throws UserDoesNotExistsException, InvalidFormatEmailException {
		if(stringNullOrEmpty(email)){
			throw new InvalidFormatEmailException("The email can not be null or empty.");
		}
		User user = users.get(email);
		if(user == null){
			throw new UserDoesNotExistsException("The user with email " + email + " doesn't exists.");
		}
		return user;
	}
	
	public synchronized void reset(){
		users.clear();
	}
	
	// Auxiliary Functions
	/**
	 * @param string
	 * @return
	 */
	private boolean stringNullOrEmpty(String string){
		if(string == null || string.trim().equals("")){
			return true;
		}
		return false;
	}
}
