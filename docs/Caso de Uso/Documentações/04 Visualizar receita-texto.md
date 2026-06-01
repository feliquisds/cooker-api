Apresenta detalhes de uma receita ou texto

**Atores:** Usuário não logado, usuário logado  
**Descrição:** Exibe informações de uma receita ou de um texto, como imagens e metadados  
**Pré-condições:** Item a ser visualizado deve existir  
**Pós-condições:** Informações do item é apresentado ao usuário  
**Referências:** RF02, RF03, RF05, RF06, RF09, RNF01, RNF02, RNF03, RN03, RN04, RN05  

**Fluxo básico:**  
    1. Usuário acessa/pesquisa um item  
    2. Sistema obtém dados  
    3. Informações são apresentadas ao usuário final  
    4. Um botão de exportar para PDF é adicionado  

**Fluxos alternativos:**  
    **Usuário está logado:**  
        1. Fluxo segue normalmente  
        2. Um botão de favoritar é adicionado  

    **Usuário é autor do item:**  
        1. Fluxo segue normalmente  
        2. Um botão de editar é adicionado  

**Fluxos de exceção:**  
    **Perfil/livro de receita/item privado:**  
        1. Fluxo é interrompido  
        2. É exibido mensagem de erro apropriada  
