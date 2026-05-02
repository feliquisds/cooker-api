package com.expsn.cooker.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "type" // O campo que dirá no JSON/Mongo o que é o objeto
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = RecipeRef.class, name = "RECIPE"),
  @JsonSubTypes.Type(value = TextRef.class, name = "TEXT"),
  @JsonSubTypes.Type(value = Category.class, name = "CATEGORY")
})
public abstract class BookComponent {
    // Classe base vazia ou com atributos comuns se houver
}