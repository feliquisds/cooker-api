Realiza manutenção de receita-texto

**Atores:** Usuário logado  
**Descrição:** Realiza a criação, atualização, ou exclusão de uma receita ou texto  
**Pré-condições:** Usuário deve estar autenticado; para atualizar/apagar, deve ser o autor do item  
**Pós-condições:** Receita ou texto é criado, atualizado, ou removido conforme ação  
**Referências:** RF02, RF03, RNF01, RNF02, RNF03, RN01, RN03, RN04  

**Fluxo básico:**  
    1. Usuário acessa a criação/edição da receita ou texto  
    2. Sistema apresenta os campos preenchíveis  
    3. Usuário preenche informações  
    4. Sistema salva o item  
    5. Sistema prepara notificação  

**Fluxos alternativos:**  
    **Apagar item:**  
        1. Usuário seleciona a opção de apagar  
        2. Sistema solicita confirmação  
        3. Usuário confirma  
        4. Sistema apaga o item  

**Fluxos de exceção:**  
    **Usuário não autorizado:**  
        1. Fluxo é interrompido  
        2. É exibida mensagem de erro apropriada  
