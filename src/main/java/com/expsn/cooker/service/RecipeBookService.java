package com.expsn.cooker.service;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.expsn.cooker.model.BookComponent;
import com.expsn.cooker.model.Category;
import com.expsn.cooker.model.RecipeBook;
import com.expsn.cooker.model.RecipeRef;
import com.expsn.cooker.model.TextRef;
import com.expsn.cooker.repository.RecipeBookRepository;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.TextRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeBookService {

    private final RecipeBookRepository recipeBookRepository;
    private final RecipeRepository recipeRepository;
    private final TextRepository textRepository;
    private final MongoTemplate mongoTemplate;

    public RecipeBook save(RecipeBook book) {
        return mongoTemplate.save(book);
    }
    
    public List<RecipeBook> searchBooks(String title, List<String> tags, String authorHandle) {
        // 1. Cruza com a coleção de usuários para checar privacidade e handle
        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users")
                .localField("ownerId")
                .foreignField("_id")
                .as("authorData");

        Criteria criteria = new Criteria();

        // Filtros de conteúdo do livro
        if (title != null) criteria.and("title").regex(title, "i");
        if (tags != null && !tags.isEmpty()) criteria.and("tags").all(tags);

        // Filtro pelo Handle do Autor
        if (authorHandle != null) {
            criteria.and("authorData.handle").is(authorHandle.replace("@", ""));
        }

        // REGRA DE OURO DE PRIVACIDADE:
        // O livro deve ser público E o perfil do autor NÃO pode ser privado
        criteria.and("isPublic").is(true);
        criteria.and("authorData.isPrivate").is(false);

        Aggregation agg = Aggregation.newAggregation(
                lookupUser,
                Aggregation.unwind("authorData"),
                Aggregation.match(criteria)
        );

        return mongoTemplate.aggregate(agg, "recipe_books", RecipeBook.class).getMappedResults();
    }

    public RecipeBook getHydratedBook(String id) {
        // 1. Busca o livro original
        RecipeBook book = recipeBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // 2. Inicia a recursão para preencher os nomes
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
}
