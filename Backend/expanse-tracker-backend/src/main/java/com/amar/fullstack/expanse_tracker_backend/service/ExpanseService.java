package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.NotificationRequest;
import com.amar.fullstack.expanse_tracker_backend.entity.*;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.exception.UnAuthorizedException;
import com.amar.fullstack.expanse_tracker_backend.notification.service.NotificationService;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseCategoryRepository;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpanseService {

    private static final Logger logger =
            LoggerFactory.getLogger(ExpanseService.class);

    private final ExpanseRepository expRepo;
    private final ExpanseCategoryRepository categoryRepo;
    private final NotificationService notificationService;

    public ExpanseService(ExpanseRepository expRepo,
                          ExpanseCategoryRepository categoryRepo,
                          NotificationService notificationService) {
        this.expRepo = expRepo;
        this.categoryRepo = categoryRepo;
        this.notificationService=notificationService;
    }

    public ExpanseResponseDto createExpanse(ExpanseRequestDto dto, User user) {

        ExpanseCategory category;

        if (dto.getCategoryId() != null) {

            category = getCategoryById(dto.getCategoryId(), user);

        } else if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {

            // ✅ normalize input (VERY IMPORTANT)
            String categoryName = dto.getCategoryName().trim().toLowerCase();

            category = categoryRepo
                    .findByNameIgnoreCaseAndUser_Id(categoryName, user.getId())
                    .orElseGet(() -> {
                        ExpanseCategory newCat = new ExpanseCategory();
                        newCat.setName(categoryName); // ✅ normalized save
                        newCat.setUser(user);
                        return categoryRepo.save(newCat);
                    });

        } else {
            throw new IllegalArgumentException("Category is required");
        }

        // ✅ resolve type once
        Type type = Type.valueOf(dto.getType());

        // ✅ ONLY expense affects totalAmount
        if (type == Type.EXPENSE) {
            category.setTotalAmount(
                    safe(category.getTotalAmount()) + dto.getAmount()
            );
        }else if(type==Type.INCOME){
            category.setTotalAmount(
                    safe(category.getTotalAmount()) + dto.getAmount()
            );
        }

        // ✅ create expense
        Expanse expanse = new Expanse();
        expanse.setName(dto.getName());
        expanse.setType(type);
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(category);
        expanse.setUser(user);

        Expanse saved=expRepo.save(expanse);
        if (type==Type.EXPENSE){
            sendExpenseNotification(saved,user);
        }else if(type==Type.INCOME){
            sendIncomeNotification(saved,user);
        }

        return mapToResponse(saved);
    }

    public List<ExpanseResponseDto> getUserExpenses(User user) {

        return expRepo.findByUser_Id(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpanseResponseDto getById(Long id, User user) {

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        return mapToResponse(expanse);
    }


    private ExpanseCategory resolveCategory(ExpanseRequestDto dto, User user) {
        if (dto.getCategoryId() != null) {
            return getCategoryById(dto.getCategoryId(), user);

        } else if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {

            return categoryRepo
                    .findByNameIgnoreCaseAndUser_Id(dto.getCategoryName(), user.getId())
                    .orElseGet(() -> {
                        ExpanseCategory newCat = new ExpanseCategory();
                        newCat.setName(dto.getCategoryName().trim());
                        newCat.setUser(user);
                        return categoryRepo.save(newCat);
                    });
        }

        throw new IllegalArgumentException("Category is required");
    }

    public ExpanseResponseDto updateExpanse(
            Long id,
            ExpanseRequestDto dto,
            User user) {

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        ExpanseCategory oldCategory = expanse.getCategory();
        ExpanseCategory newCategory = resolveCategory(dto, user);
        if (!oldCategory.getId().equals(newCategory.getId())) {

            oldCategory.setTotalAmount(
                    safe(oldCategory.getTotalAmount()) - expanse.getAmount()
            );

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + dto.getAmount()
            );

        } else {
            double diff = dto.getAmount() - expanse.getAmount();

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + diff
            );
        }

        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setType(Type.valueOf(dto.getType()));
        expanse.setCategory(newCategory);

        return mapToResponse(expRepo.save(expanse));
    }

    @Transactional
    public void deleteExpanse(Long id, User user) {

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        ExpanseCategory category = expanse.getCategory();

        if (category != null) {
            category.setTotalAmount(
                    safe(category.getTotalAmount()) - expanse.getAmount()
            );
        }
        expRepo.delete(expanse);
    }
    private ExpanseCategory getCategoryById(Long categoryId, User user) {

        return categoryRepo.findById(categoryId)
                .filter(cat -> cat.getUser().getId().equals(user.getId()))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));
    }

    private Expanse findExpenseById(Long id) {
        return expRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found"));
    }

    private void validateOwner(Expanse expanse, User user) {
        if (!expanse.getUser().getId().equals(user.getId())) {
            throw new UnAuthorizedException("Unauthorized access");
        }
    }

    private Double safe(Double value) {
        return value == null ? 0.0 : value;
    }

    private ExpanseResponseDto mapToResponse(Expanse expanse) {

        ExpanseResponseDto dto = new ExpanseResponseDto();

        dto.setId(expanse.getId());
        dto.setName(expanse.getName());
        dto.setType(expanse.getType().name());
        dto.setAmount(expanse.getAmount());
        dto.setDescription(expanse.getDescription());
        dto.setCategory(expanse.getCategory().getName());
        dto.setCreatedAt(expanse.getCreatedAt());
        dto.setUpdatedAt(expanse.getUpdatedAt());

        return dto;
    }

    private void sendExpenseNotification(Expanse expanse, User user) {

        double totalExpense = expRepo.getTotalExpense(user.getId()) != null
                ? expRepo.getTotalExpense(user.getId())
                : 0.0;

        double totalIncome = expRepo.getTotalIncome(user.getId()) != null
                ? expRepo.getTotalIncome(user.getId())
                : 0.0;

        double remainingBalance = totalIncome - totalExpense;

        if (expanse.getAmount() > 2000) {

            NotificationRequest notify = new NotificationRequest();

            notify.setEmail(user.getEmail());
            notify.setPhone(user.getPhone());

            notify.setSubject("High Expense Alert 🚨");

            notify.setMessage(
                    "Hello " + user.getName() + ",\n\n" +
                            "⚠ High Expense Alert!\n\n" +
                            "Amount: ₹" + expanse.getAmount() + "\n" +
                            "Category: " + expanse.getCategory().getName() + "\n\n" +

                            "Total Expense: ₹" + totalExpense + "\n" +
                            "Total Income: ₹" + totalIncome + "\n" +
                            "Remaining Balance: ₹" + remainingBalance + "\n\n" +

                            "Please review your spending.\n\n" +
                            "----------------------------------\n" +
                            "This is a system-generated message."
            );

            notify.setSmsMessage(
                    "High expense: ₹" + expanse.getAmount()
            );

            notify.setTypes(List.of(
                    NotificationType.EMAIL,
                    NotificationType.SMS
            ));

            notificationService.send(notify);

        } else {

            NotificationRequest notify = new NotificationRequest();

            notify.setEmail(user.getEmail());

            notify.setSubject("Expense Added");

            notify.setMessage(
                    "Hello " + user.getName() + ",\n\n" +
                            "A new expense has been added.\n\n" +
                            "Amount: ₹" + expanse.getAmount() + "\n" +
                            "Category: " + expanse.getCategory().getName() + "\n\n" +

                            "Total Expense: ₹" + totalExpense + "\n" +
                            "Remaining Balance: ₹" + remainingBalance + "\n\n" +

                            "----------------------------------\n" +
                            "This is a system-generated message."
            );

            notify.setTypes(List.of(NotificationType.EMAIL));

            notificationService.send(notify);
        }
    }
    private void sendIncomeNotification(Expanse expanse, User user) {

        NotificationRequest notify = new NotificationRequest();

        notify.setEmail(user.getEmail());


        notify.setSubject("Income Added");
        double totalIncome=expRepo.getTotalIncome(user.getId());
        notify.setMessage(
                "Hello " + user.getName() + ",\n\n" +
                        "New income has been added successfully.\n\n" +
                        "Amount: ₹" + expanse.getAmount() + "\n" +
                        "Category: " + expanse.getCategory().getName() + "\n\n" +
                        "Total in this category: ₹" + expanse.getCategory().getTotalAmount() + "\n" +
                        "Total Income: ₹" + totalIncome + "\n\n" +

                        "----------------------------------\n" +
                        "This is a system-generated message."
        );

        notify.setTypes(List.of(NotificationType.EMAIL));

        notificationService.send(notify);
    }


}