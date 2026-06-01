Realiza criação de respostas para solicitações de receita

**Atores:** Usuário logado  
**Descrição:** Realiza a criação de respostas para uma solicitação de receita  
**Pré-condições:** Solicitação de receita deve existir  
**Pós-condições:** Resposta é adicionada à solicitação  
**Referências:** RF08, RNF02, RNF03, RN02, RN03, RN04  

**Fluxo básico:**  
    1. Usuário acessa uma solicitação de receita  
    2. Sistema apresenta a opção de resposta  
    3. Usuário informa a resposta  
    4. Sistema salva a resposta  
    5. Sistema prepara notificação  

**Fluxos de exceção:**  
    **Perfil privado ou solicitação fechada/expirada:**  
        1. Fluxo é interrompido  
        2. É exibida mensagem de erro apropriada  