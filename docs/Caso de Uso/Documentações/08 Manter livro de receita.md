Realiza manutenção de livro de receita

**Atores:** Usuário logado  
**Descrição:** Realiza a criação, atualização, ou exclusão de um livro de receita  
**Pré-condições:** Usuário deve estar autenticado; para alterar/apagar, deve ser o autor do livro  
**Pós-condições:** Livro de receita é criado, atualizado, ou removido conforme ação  
**Referências:** RF04, RF09, RF10, RNF02, RNF03, RN01, RN02, RN03, RN04, RN05  

**Fluxo básico:**  
    1. Usuário acessa a tela de criação/edição do livro  
    2. Sistema apresenta campos preenchíveis  
    3. Usuário informa informações  
    4. Sistema salva o livro  

**Fluxos alternativos:**  
    **Apagar livro:**  
        1. Usuário seleciona a opção de apagar  
        2. Sistema solicita confirmação  
        3. Usuário confirma  
        4. Sistema apaga o livro  

**Fluxos de exceção:**  
    **Usuário não autorizado:**  
        1. Fluxo é interrompido  
        2. É exibida mensagem de erro apropriada  