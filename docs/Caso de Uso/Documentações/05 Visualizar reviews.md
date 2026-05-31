Apresenta reviews de um item

**Atores:** Usuário não logado, usuário logado  
**Descrição:** Exibe os reviews associados a uma receita ou texto  
**Pré-condições:** Item a ser visualizado deve existir  
**Pós-condições:** Reviews do item são apresentados  
**Referências:** RF07, RF09, RNF02, RNF03, RN03, RN04, RN05, RN06  

**Fluxo básico:**  
    1. Usuário escolhe visualizar reviews de um item  
    2. Sistema obtém dados  
    3. Reviews são apresentados ao usuário final  

**Fluxos alternativos:**  
    **Usuário está logado e não é autor do item:**  
        1. Fluxo segue normalmente  
        2. Um botão de adicionar review é adicionado  

**Fluxos de exceção:**  
    **Perfil/livro de receita/item privado:**  
        1. Fluxo é interrompido  
        2. É exibido mensagem de erro apropriada  