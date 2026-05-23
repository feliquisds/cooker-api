package com.expsn.cooker.service;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.expsn.cooker.exception.CookerException;
import com.expsn.cooker.model.BookComponent;
import com.expsn.cooker.model.Category;
import com.expsn.cooker.model.RecipeBook;
import com.expsn.cooker.model.RecipeRef;
import com.expsn.cooker.model.TextRef;
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.RecipeBookRepository;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.TextRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeBookService {

    private final RecipeBookRepository recipeBookRepository;
    private final RecipeRepository recipeRepository;
    private final TextRepository textRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    private final UserService userService;

    public RecipeBook save(RecipeBook book) {
        RecipeBook savedBook = mongoTemplate.save(book);
        userService.addRecipeBookToSaved(savedBook.getOwnerId(), savedBook.getId());
        return savedBook;
    }

    public List<RecipeBook> getSavedBooks(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CookerException("Usuário não encontrado"));

        if (user.getSavedBookIds() == null || user.getSavedBookIds().isEmpty()) {
            return List.of();
        }

        return recipeBookRepository.findAllById(user.getSavedBookIds()).stream()
                .filter(book -> canViewBook(book, userId))
                .toList();
    }
    
    public List<RecipeBook> searchBooks(String title, List<String> tags, String authorHandle, String currentUserId) {
        // 1. Cruza com a coleção de usuários para checar privacidade e handle
        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users")
                .localField("ownerId")
                .foreignField("_id")
                .as("authorData");

        List<Criteria> criteriaList = new java.util.ArrayList<>();
        if (title != null && !title.isBlank()) {
            criteriaList.add(Criteria.where("title").regex(title, "i"));
        }
        if (tags != null && !tags.isEmpty()) {
            criteriaList.add(Criteria.where("tags").all(tags));
        }
        if (authorHandle != null && !authorHandle.isBlank()) {
            criteriaList.add(Criteria.where("authorData.handle").is(normalizeHandle(authorHandle)));
        }

        criteriaList.add(buildVisibilityCriteria(currentUserId));

        Criteria matchCriteria = criteriaList.size() == 1
                ? criteriaList.get(0)
                : new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        Aggregation agg = Aggregation.newAggregation(
                lookupUser,
                Aggregation.unwind("authorData"),
                Aggregation.match(matchCriteria)
        );

        return mongoTemplate.aggregate(agg, "recipe_books", RecipeBook.class).getMappedResults();
    }

    public RecipeBook getBookById(String id, String userId) {
        RecipeBook book = recipeBookRepository.findById(id)
            .orElseThrow(() -> new CookerException("Livro não encontrado"));
        User owner = userRepository.findById(book.getOwnerId())
            .orElseThrow(() -> new CookerException("Dono do livro não encontrado"));

        if (!canViewBook(book, owner, userId)) {
            throw new CookerException("Acesso negado a este livro");
        }

        hydrateItems(book.getItems());

        return book;
    }

    private void hydrateItems(List<BookComponent> items) {
        if (items == null || items.isEmpty()) return;

        for (BookComponent item : items) {
            if (item instanceof RecipeRef ref) {
                // Busca o título da receita e injeta no objeto de referência
                recipeRepository.findById(ref.getRecipeId())
                        .ifPresent(recipe -> ref.setTitle(recipe.getTitle()));
                
            } else if (item instanceof TextRef ref) {
                // Busca o título do texto e injeta no objeto de referência
                textRepository.findById(ref.getTextId())
                        .ifPresent(text -> ref.setTitle(text.getTitle()));

            } else if (item instanceof Category cat) {
                // Se for uma categoria, chama o próprio método para hidratar o que está dentro dela
                // É aqui que a mágica da recursividade acontece!
                hydrateItems(cat.getItems());
            }
        }
    }

    private boolean canViewBook(RecipeBook book, String userId) {
        User owner = userRepository.findById(book.getOwnerId())
                .orElseThrow(() -> new CookerException("Dono do livro não encontrado"));

        return canViewBook(book, owner, userId);
    }

    private boolean canViewBook(RecipeBook book, User owner, String userId) {
        boolean isOwner = userId != null && book.getOwnerId().equals(userId);
        if (isOwner) {
            return true;
        }

        return book.isPublic() && !owner.isPrivate();
    }

    private Criteria buildVisibilityCriteria(String currentUserId) {
        if (currentUserId == null) {
            return Criteria.where("isPublic").is(true)
                    .and("authorData.isPrivate").is(false);
        }

        return new Criteria().orOperator(
                Criteria.where("ownerId").is(currentUserId),
                Criteria.where("isPublic").is(true).and("authorData.isPrivate").is(false)
        );
    }

    private String normalizeHandle(String handle) {
        return handle.replace("@", "").trim();
    }
}
