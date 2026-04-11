package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import com.amar.fullstack.expanse_tracker_backend.entity.Type;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.CategoryRepository;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpanseService {

    private static Logger logger = LoggerFactory.getLogger(ExpanseService.class);
    private final ExpanseRepository expRepo;
    private final CategoryRepository categoryRepo;

    public ExpanseService(ExpanseRepository expRepo,
                          CategoryRepository categoryRepo) {
        this.expRepo = expRepo;
        this.categoryRepo = categoryRepo;
    }

    public ExpanseResponseDto createExpanse(ExpanseRequestDto dto, User user){
        logger.info("Starting creation of expense for user: {} with data: {}", user.getEmail(), dto);
        logger.debug("Expense request -> name:{}, amount:{}, type:{}, category:{}, description:{}",dto.getName(),dto.getAmount(),dto.getType(),dto.getCategory(),dto.getDescription());
        ExpanseCategory category = getOrCreateCategory(dto.getCategory());
        logger.debug("Using category:{} ",category.getName());
        category.setTotalAmount(safe(category.getTotalAmount()) + dto.getAmount());

        categoryRepo.save(category);
        logger.debug("Updated category total amount: {}", category.getTotalAmount());
        Expanse expanse = new Expanse();
        expanse.setName(dto.getName());
        expanse.setType(Type.valueOf(dto.getType()));
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(category);
        expanse.setUser(user);

        Expanse savedExpanse= expRepo.save(expanse);
        logger.info("Expense created successfully with id: {} for user: {}", savedExpanse.getId(), user.getEmail());
        return mapToResponse(savedExpanse);
    }
    public List<ExpanseResponseDto> getAll() {
        logger.info("Fetching all expenses");
        List<Expanse> expanses=expRepo.findAll();
        if (expanses.isEmpty()){
            logger.warn("No expenses found in the database");
        }else{
            logger.info("Fetched {} expenses from the database", expanses.size());
        }
        logger.debug("Expanses data: {}",expanses);
        return expanses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpanseResponseDto getById(Long id) {
        logger.info("Fetching expense with id: {}", id);
        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Expense not found with id: {}", id);
                    return new RuntimeException("Expense not found with id: " + id);
                });

        logger.info("Expense fetched successfully with id: {}", id);

        return mapToResponse(expanse);
    }

    @Transactional
    public ExpanseResponseDto updateExpanse(Long id, ExpanseRequestDto dto, User user) {
        logger.info("Entered in updateExpanse method for expense id: {} by user: {}", id, user.getEmail());
        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() ->{
                        logger.warn("Expense not found with id: {}", id);
                        return new RuntimeException("Expanse not found");
                });
        logger.debug("Fetched expense for update: {}", expanse);
        if (!expanse.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized update attempt for expense id: {} by user: {}", id, user.getEmail());
            throw new RuntimeException("You are not authorized to update this expense");
        }
        ExpanseCategory oldCategory = expanse.getCategory();
        ExpanseCategory newCategory = getOrCreateCategory(dto.getCategory());
        logger.info("expense id: {} - old category: {}, new category: {}", id, oldCategory.getName(), newCategory.getName());
        if (!oldCategory.getName().equals(newCategory.getName())) {
            logger.debug("Category changed for expense id: {}. Adjusting totals - old category: {}, new category: {}", id, oldCategory.getName(), newCategory.getName());
            oldCategory.setTotalAmount(
                    safe(oldCategory.getTotalAmount()) - expanse.getAmount()
            );
            ExpanseCategory old_Category=categoryRepo.save(oldCategory);
            logger.info("Updated old category total amount: {} for category: {}", old_Category.getTotalAmount(), old_Category.getName());
            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + dto.getAmount()
            );
            logger.info("Updated new category total amount: {} for category: {}", newCategory.getTotalAmount(), newCategory.getName());
        } else {
            double diff = dto.getAmount() - expanse.getAmount();
            logger.debug("Amount changed for expense id: {}. Adjusting category total by diff: {} for category: {}", id, diff, newCategory.getName());
            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + diff
            );
        }
        categoryRepo.save(newCategory);
        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(newCategory);

        Expanse updated=expRepo.save(expanse);
        logger.info("Expanse updated successfully with id: {} by user: {}", updated.getId(), user.getEmail());
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteExpanse(Long id,User user){
        logger.info("Entered in deleteExpanse method for expense id: {} by user: {}", id, user.getEmail());
        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Expense not found with id: {}", id);
                    return new RuntimeException("Expanse not found");
                });
        logger.debug("Fetched expense for deletion: {}", expanse);
        if (!expanse.getUser().getId().equals(user.getId())){
            logger.warn("Unauthorized delete attempt for expense id: {} by user: {}", id, user.getEmail());
            throw new RuntimeException("You are not authorized to delete this expense");
        }

        ExpanseCategory category = expanse.getCategory();

        category.setTotalAmount(
                safe(category.getTotalAmount()) - expanse.getAmount()
        );
        logger.debug("Adjusted category total amount: {} for category: {} after deleting expense id: {}", category.getTotalAmount(), category.getName(), id);
        categoryRepo.save(category);
        expRepo.delete(expanse);
        logger.info("Expense deleted successfully with id: {} by user: {}", id, user.getEmail());
    }

    private Double safe(Double value){
        return value == null ? 0.0 : value;
    }

    private ExpanseCategory getOrCreateCategory(String name) {

        logger.info("Fetching or creating category with name: {}", name);

        Optional<ExpanseCategory> optionalCategory = categoryRepo.findByNameIgnoreCase(name);

        if (optionalCategory.isPresent()) {
            logger.info("Category found with name: {}", name);
            return optionalCategory.get();
        }
        logger.warn("Category not found. Creating new category with name: {}", name);
        ExpanseCategory newCategory = new ExpanseCategory();
        newCategory.setName(name);
        ExpanseCategory saved = categoryRepo.save(newCategory);
        logger.info("New category created with id: {} and name: {}",
                saved.getId(), saved.getName());
        return saved;
    }
    public ExpanseResponseDto getById(Long id, User user) {
        logger.info("Fetching expense with id: {} for user: {}", id, user.getEmail());

        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Expense not found with id: {} for user: {}", id, user.getEmail());
                    return new RuntimeException("Expanse not found");
                });
        logger.info("Expense fetched successfully with id: {} for user: {}", id, user.getEmail());

        if (!expanse.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized access attempt for expense id: {} by user: {}", id, user.getEmail());
            throw new RuntimeException("Unauthorized access");
        }
        ExpanseResponseDto expanse_response =mapToResponse(expanse);
        return expanse_response;
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

    public List<ExpanseResponseDto> getUserExpenses(User user) {

        logger.info("Fetching expenses for user: {}", user.getEmail());
        List<Expanse> expenses = expRepo.findByUserId(user.getId());
        if (expenses.isEmpty()) {
            logger.warn("No expenses found for user: {}", user.getEmail());
        } else {
            logger.info("Total expenses fetched for user {}: {}",
                    user.getEmail(), expenses.size());
        }
        logger.debug("Expenses data for user {}: {}", user.getEmail(), expenses);
        return expenses.stream()
                .map(this::mapToResponse)
                .toList();
    }
}