Apresenta detalhes de uma solicitação de receita

**Atores:** Usuário não logado, usuário logado  
**Descrição:** Exibe informações de uma solicitação de receita, como descrição, autor, e respostas  
**Pré-condições:** Solicitação de receita a ser visualizada deve existir  
**Pós-condições:** Informações da solicitação de receita são apresentadas  
**Referências:** RF08, RF09, RNF02, RNF03, RN03, RN04  

**Fluxo básico:**  
    1. Usuário acessa/pesquisa uma solicitação de receita  
    2. Sistema obtém dados  
    3. Informações são apresentadas ao usuário final  

**Fluxos alternativos:**  
    **Usuário está logado:**  
        1. Fluxo segue normalmente  
        2. Um campo de adicionar resposta é adicionado  

**Fluxos de exceção:**  
    **Perfil/solicitação de receita privado:**  
        1. Fluxo é interrompido  
        2. É exibido mensagem de erro apropriada  