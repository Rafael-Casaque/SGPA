package com.example.sgpa.domain.usecases.checkout;

import com.example.sgpa.domain.entities.checkout.CheckedOutItem;
import com.example.sgpa.domain.entities.checkout.CheckedOutItemKey;
import com.example.sgpa.domain.entities.checkout.Checkout;
import com.example.sgpa.domain.entities.user.User;
import com.example.sgpa.domain.usecases.utils.DAO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CheckedOutItemDAO extends DAO<CheckedOutItem, CheckedOutItemKey> {
    List<CheckedOutItem> findLate();
    Optional<CheckedOutItem> findNotReturned(String patrimonialId);
    List<CheckedOutItem> findLateByUser(String userId);
    List<CheckedOutItem> findByUser(String userId);
    List<CheckedOutItem> findByPeriodUser(String userId);
    List<CheckedOutItem> findLateByPeriod(String userId);
}