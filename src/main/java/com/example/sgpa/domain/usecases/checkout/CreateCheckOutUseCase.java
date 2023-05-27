package com.example.sgpa.domain.usecases.checkout;

import com.example.sgpa.domain.entities.Session.Session;
import com.example.sgpa.domain.entities.checkout.Checkout;
import com.example.sgpa.domain.entities.historical.Event;
import com.example.sgpa.domain.usecases.historical.EventDAO;
import com.example.sgpa.domain.entities.historical.EventType;
import com.example.sgpa.domain.entities.part.PartItem;
import com.example.sgpa.domain.entities.part.StatusPart;
import com.example.sgpa.domain.entities.user.Technician;
import com.example.sgpa.domain.entities.user.User;
import com.example.sgpa.domain.usecases.part.CheckForPartItemAvailabilityUseCase;
import com.example.sgpa.domain.usecases.part.PartItemDAO;
import com.example.sgpa.domain.usecases.user.CheckForUserPendingsIssuesUseCase;
import com.example.sgpa.domain.usecases.user.UserDAO;
import com.example.sgpa.domain.usecases.utils.EntityNotFoundException;

import java.util.Optional;
import java.util.Set;

public class CreateCheckOutUseCase {
    private final PartItemDAO itemPartDAO;
    private final UserDAO userDAO;
    private final CheckOutDAO checkOutDAO;
    private final CheckedOutItemDAO checkedOutItemDAO;
    private final EventDAO eventDAO;
    private final CheckForUserPendingsIssuesUseCase checkForUserPendingsIssuesUseCase;
    private final CheckForPartItemAvailabilityUseCase checkForPartItemAvailabilityUseCase;
    public CreateCheckOutUseCase(UserDAO userDAO,
                          PartItemDAO itemPartDAO,
                          CheckOutDAO checkOutDAO,
                          CheckedOutItemDAO checkedOutItemDAO,
                          EventDAO eventDAO,
                          CheckForUserPendingsIssuesUseCase checkForUserPendingsIssuesUseCase,
                          CheckForPartItemAvailabilityUseCase checkForPartItemAvailabilityUseCase){
        this.userDAO = userDAO;
        this.itemPartDAO =itemPartDAO;
        this.checkOutDAO = checkOutDAO;
        this.checkedOutItemDAO = checkedOutItemDAO;
        this.eventDAO = eventDAO;
        this.checkForUserPendingsIssuesUseCase = checkForUserPendingsIssuesUseCase;
        this.checkForPartItemAvailabilityUseCase = checkForPartItemAvailabilityUseCase;
    }
    public Checkout createCheckout(int userId, Set<PartItem> itemParts){
        Optional<User> user = userDAO.findOne(userId);
        if (user.isEmpty())
            throw new EntityNotFoundException("User not found");
        checkForUserPendingsIssuesUseCase.checkForUserPendingIssues(userId);
        checkForPartItemAvailabilityUseCase.checkForAvailabilityOfTheParts(itemParts);
        Technician loggedTechnician = Session.getLoggedTechnician();
        Checkout checkout = new Checkout(itemParts, user.get(), loggedTechnician);
        int id = checkOutDAO.create(checkout);
        checkout.setCheckOutId(id);
        checkout.getCheckedOutItems().forEach(checkedOutItemDAO::create);
        itemParts.forEach(itemPart -> itemPart.setStatus(StatusPart.CHECKED_OUT));
        itemParts.forEach(itemPartDAO::update);
        itemParts.forEach(itemPart ->
                eventDAO.create(new Event(EventType.CHECKOUT, user.get(), loggedTechnician, itemPart)));
        return checkout;
    }

}
