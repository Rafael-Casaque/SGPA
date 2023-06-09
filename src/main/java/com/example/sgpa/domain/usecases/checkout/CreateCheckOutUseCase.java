package com.example.sgpa.domain.usecases.checkout;

import com.example.sgpa.application.repository.sqlite.SqliteReservationDAO;
import com.example.sgpa.domain.entities.Session.Session;
import com.example.sgpa.domain.entities.checkout.Checkout;
import com.example.sgpa.domain.entities.historical.Event;
import com.example.sgpa.domain.entities.reservation.Reservation;
import com.example.sgpa.domain.entities.reservation.ReservationStatus;
import com.example.sgpa.domain.usecases.historical.EventDAO;
import com.example.sgpa.domain.entities.historical.EventType;
import com.example.sgpa.domain.entities.part.PartItem;
import com.example.sgpa.domain.entities.part.StatusPart;
import com.example.sgpa.domain.entities.user.User;
import com.example.sgpa.domain.usecases.part.CheckForPartItemAvailabilityUseCase;
import com.example.sgpa.domain.usecases.part.PartItemDAO;
import com.example.sgpa.domain.usecases.reservation.ReservationDAO;
import com.example.sgpa.domain.usecases.user.CheckForUserPendingIssuesUseCase;
import com.example.sgpa.domain.usecases.user.UserDAO;
import com.example.sgpa.domain.usecases.utils.EntityNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public class CreateCheckOutUseCase {
    private final PartItemDAO itemPartDAO;
    private final UserDAO userDAO;
    private final CheckOutDAO checkOutDAO;
    private final CheckedOutItemDAO checkedOutItemDAO;
    private final EventDAO eventDAO;
    ReservationDAO reservationDAO = new SqliteReservationDAO();
    private final CheckForUserPendingIssuesUseCase checkForUserPendingIssuesUseCase;
    private final CheckForPartItemAvailabilityUseCase checkForPartItemAvailabilityUseCase;
    public CreateCheckOutUseCase(UserDAO userDAO,
                          PartItemDAO itemPartDAO,
                          CheckOutDAO checkOutDAO,
                          CheckedOutItemDAO checkedOutItemDAO,
                          EventDAO eventDAO,
                          CheckForUserPendingIssuesUseCase checkForUserPendingIssuesUseCase,
                          CheckForPartItemAvailabilityUseCase checkForPartItemAvailabilityUseCase){
        this.userDAO = userDAO;
        this.itemPartDAO =itemPartDAO;
        this.checkOutDAO = checkOutDAO;
        this.checkedOutItemDAO = checkedOutItemDAO;
        this.eventDAO = eventDAO;
        this.checkForUserPendingIssuesUseCase = checkForUserPendingIssuesUseCase;
        this.checkForPartItemAvailabilityUseCase = checkForPartItemAvailabilityUseCase;
    }
    public Checkout createCheckout(int userId, Set<PartItem> partItems) throws Exception {
        if (userId == 0 || partItems.isEmpty())
            throw new IllegalArgumentException("User and check out items must be not null.");
        User user = userDAO.findOne(userId).orElseThrow(()-> new EntityNotFoundException("User not found"));
        checkForUserPendingIssuesUseCase.checkForUserPendingIssues(userId);
        checkForPartItemAvailabilityUseCase.checkForAvailabilityOfTheParts(partItems);
        User loggedTechnician = Session.getLoggedTechnician();
        Checkout checkout = new Checkout(partItems, user, loggedTechnician);
        int id = checkOutDAO.create(checkout);
        checkout.setCheckOutId(id);
        checkout.getCheckedOutItems().forEach(checkedOutItemDAO::create);
        partItems.forEach(item -> item.setStatus(StatusPart.CHECKED_OUT));
        partItems.forEach(itemPartDAO::update);
        partItems.forEach(item -> eventDAO.create(new Event(EventType.CHECKOUT, user, loggedTechnician, item)));
        return checkout;
    }

    public Checkout createCheckout(Reservation reservation) throws Exception {
        int userId = reservation.getUserId();
        Set<PartItem> partItems = reservation.getItems();
        if ( userId == 0 || partItems.isEmpty())
            throw new IllegalArgumentException("User and reserved items must be not null or empties.");
        if (reservation.isClosed())
            throw new RuntimeException("Checkout not allowed: Reservation status " + reservation.getStatus().toString());
        User user = userDAO.findOne(userId).orElseThrow(()-> new EntityNotFoundException("User not found"));
        checkForUserPendingIssuesUseCase.checkForUserPendingIssues(userId);
        User loggedTechnician = Session.getLoggedTechnician();
        Checkout checkout = new Checkout(reservation);
        int id = checkOutDAO.create(checkout);
        checkout.setCheckOutId(id);
        reservation.setStatus(ReservationStatus.FINISHED);
        reservationDAO.update(reservation);
        checkout.getCheckedOutItems().forEach(checkedOutItemDAO::create);
        partItems.forEach(item -> item.setStatus(StatusPart.CHECKED_OUT));
        partItems.forEach(itemPartDAO::update);
        partItems.forEach(item -> eventDAO.create(new Event(EventType.CHECKOUT, user, loggedTechnician, item)));
        return checkout;
    }
}
