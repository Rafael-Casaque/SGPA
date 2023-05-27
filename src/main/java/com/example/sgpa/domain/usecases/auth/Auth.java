package com.example.sgpa.domain.usecases.auth;

import java.util.Optional;

import com.example.sgpa.domain.entities.Session.Session;
import com.example.sgpa.domain.entities.user.Technician;
import com.example.sgpa.domain.entities.user.User;
import com.example.sgpa.domain.entities.user.UserType;
import com.example.sgpa.domain.usecases.user.UserDAO;

public class Auth {
	private final UserDAO userDAO;

	public Auth(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	public boolean authenticate(int institutionalId, String password){
		Optional<User> user = userDAO.findOneByIdAndType(UserType.TECHNICIAN,institutionalId);
		if (user.isEmpty())
			throw new RuntimeException("Technician user not found");
		Technician technician = (Technician) user.get();
		if(!technician.getPassword().equals(password))
			throw new RuntimeException("Incorrect password");
		Session.makeInstance(technician);
		return true;		
	}
}
