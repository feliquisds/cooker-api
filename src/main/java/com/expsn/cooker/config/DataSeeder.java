package com.expsn.cooker.config;

import java.sql.Date;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.expsn.cooker.model.Category;
import com.expsn.cooker.model.Difficulty;
import com.expsn.cooker.model.Ingredient;
import com.expsn.cooker.model.IngredientSection;
import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.RecipeBook;
import com.expsn.cooker.model.RecipeRef;
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.RecipeBookRepository;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeBookRepository bookRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${app.seeder.enabled:false}")
    private boolean seederEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (!seederEnabled) {
            return;
        }
        System.out.println(">>> CONECTADO AO BANCO: " + mongoTemplate.getDb().getName());

        User rafa = User.builder()
                .handle("rafafafa")
                .name("Rafael")
                .email("rafael@cooker.com")
                .password("$2a$10$gtpa8C/.4O4ThZHAm6YlsuaWLp8RBJZrBwGrHJIkBRXWxCUuUd7A2")
                .isPrivate(false)
                .avatarUrl(null)
                .bio("Meu nome é Rafa e gosto muito de cozinhar. Apoie o projeto Cooker!")
                .birthDate(new Date(2002 - 1900, 3 - 1, 5).toLocalDate())
                .favoriteRecipeIds(null)
                .notificationTags(null)
                .savedBookIds(null)
                .build();
        rafa = userRepository.save(rafa);

        Recipe temperoVerde = Recipe.builder()
                .authorId(rafa.getId())
                .title("Tempero verde")
                .difficulty(Difficulty.EASY)
                .timeMinutes(5)
                .images(null)
                .isPublic(true)
                .tags(List.of("tempero", "rápida", "vegana"))
                .descriptionMD("Este tempero é frequentemente usado para marinar carnes na culinária mineira: esta receita é a do restaurante Gosto Com Gosto, um dos meus favoritos em Visconde de Mauá.\n\nEsta receita foi adaptada do livro Interpretações do Gosto, da autora Mônica Rangel.\n\nEquipamento especial: um processador de alimentos.")
                .ingredientSections(List.of(
                        IngredientSection.builder()
                                .title(null)
                                .ingredients(List.of(
                                        new Ingredient(2, null, "cebolas"),
                                        new Ingredient(1, null, "pimentão verde"),
                                        new Ingredient(5, "dentes", "de alho"),
                                        new Ingredient(100, "g", "de cheiro verde (salsinha e cebolinha em partes iguais)")
                                ))
                                .build()
                ))
                .stepsMD(List.of("Bata todos os ingredientes no processador de alimentos. O tempero dura até 3 dias na geladeira."))
                .build();
        temperoVerde = recipeRepository.save(temperoVerde);

        // 3. CRIAR LIVRO DE RECEITAS (Estrutura Dinâmica/Recursiva)
        // Criando os itens
        RecipeRef refTemperoVerde = new RecipeRef();
        refTemperoVerde.setRecipeId(temperoVerde.getId());
        refTemperoVerde.setTitle(temperoVerde.getTitle());

        Category temperosCategory = new Category();
        temperosCategory.setName("Temperos");
        temperosCategory.setItems(List.of(refTemperoVerde));

        RecipeBook meuLivro = RecipeBook.builder()
                .ownerId(rafa.getId())
                .title("Receitas IV")
                .descriptionMD("Meu objetivo aqui é ensinar que qualquer um (sim, até você!) pode aprender a cozinhar. Todas as receitas nesse site foram testadas e aprovadas por mim, então meticulosamente adaptadas e traduzidas (quando necessário).")
                .isPublic(true)
                .items(List.of(temperosCategory))
                .build();
        bookRepository.save(meuLivro);

        System.out.println(">>> Banco de Dados COOKER populado com sucesso! <<<");
    }
}