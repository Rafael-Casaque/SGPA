package com.example.sgpa.domain.usecases.part;

import com.example.sgpa.domain.entities.Session.Session;
import com.example.sgpa.domain.entities.part.PartItem;
import com.example.sgpa.domain.usecases.utils.EntityNotFoundException;

import java.util.Objects;
import java.util.Optional;

public class UpdatePartItemUseCase {
    private final PartItemDAO partItemDAO;

    public UpdatePartItemUseCase(PartItemDAO itemPartDAO) {
        this.partItemDAO = itemPartDAO;
    }

    // faz sentido ter o local de armazenamento salvo?
    public PartItem updatePartItem(String patrimonialId, String newObservation) {
        Optional<PartItem> optPartItem = partItemDAO.findOne(patrimonialId);
        if (optPartItem.isEmpty())
            throw new EntityNotFoundException("Part Item not found");
        if (Objects.equals(newObservation, "")) {
            throw new RuntimeException("Invalid null description");
        }
        Session.getLoggedTechnician();
        optPartItem.get().setObservation(newObservation);
        partItemDAO.update(optPartItem.get());
        return optPartItem.get();
    }

}
