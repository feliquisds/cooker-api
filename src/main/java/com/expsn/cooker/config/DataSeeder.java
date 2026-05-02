package com.expsn.cooker.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.expsn.cooker.model.Category;
import com.expsn.cooker.model.Difficulty;
import com.expsn.cooker.model.Ingredient;
import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.RecipeBook;
import com.expsn.cooker.model.RecipeRef;
import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.RecipeBookRepository;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.RequestRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeBookRepository bookRepository;
    private final RequestRepository requestRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> CONECTADO AO BANCO: " + mongoTemplate.getDb().getName());
        // Limpar banco para teste limpo (opcional)
        userRepository.deleteAll();
        recipeRepository.deleteAll();
        bookRepository.deleteAll();
        requestRepository.deleteAll();
        // 1. CRIAR USUÁRIOS
        User chefJoao = User.builder()
                .handle("cheff_joao")
                .name("João Silva")
                .email("joao@cooker.com")
                .isPrivate(false)
                .build();
        userRepository.save(chefJoao);

        User anaSecreta = User.builder()
                .handle("ana_bio")
                .name("Ana Souza")
                .isPrivate(true) // Ana é privada, nada dela deve aparecer na busca global
                .build();
        userRepository.save(anaSecreta);

        // 2. CRIAR RECEITA (Do João)
        Recipe carbonara = Recipe.builder()
                .authorId(chefJoao.getId())
                .title("Carbonara Autêntica")
                .difficulty(Difficulty.MEDIUM)
                .timeMinutes(25)
                .isPublic(true)
                .tags(List.of("#italiana", "#massa"))
                .ingredients(List.of(
                        new Ingredient(200.0, "g", "Espaguete"),
                        new Ingredient(100.0, "g", "Guanciale")
                ))
                .stepsMD(List.of("Ferva a água", "Frite o porco", "Misture o ovo e queijo"))
                .build();
        recipeRepository.save(carbonara);

        // 3. CRIAR LIVRO DE RECEITAS (Estrutura Dinâmica/Recursiva)
        // Criando os itens
        RecipeRef refCarbonara = new RecipeRef();
        refCarbonara.setRecipeId(carbonara.getId());
        refCarbonara.setTitle(carbonara.getTitle());

        Category pastaCategory = new Category();
        pastaCategory.setName("Massas Clássicas");
        pastaCategory.setItems(List.of(refCarbonara));

        RecipeBook meuLivro = RecipeBook.builder()
                .ownerId(chefJoao.getId())
                .title("Segredos da Itália")
                .isPublic(true)
                .items(List.of(pastaCategory)) // O livro tem uma categoria que tem uma receita
                .build();
        bookRepository.save(meuLivro);

        // 4. CRIAR REQUEST (Da Ana)
        RecipeRequest req = RecipeRequest.builder()
                .requesterId(anaSecreta.getId())
                .title("Sugestão de Bolo Diet")
                .tags(List.of("#doce", "#diet"))
                .createdAt(LocalDateTime.now())
                .build();
        requestRepository.save(req);

        System.out.println(">>> Banco de Dados COOKER populado com sucesso! <<<");
    }
}