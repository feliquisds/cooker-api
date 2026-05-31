Realiza autenticação de usuário

**Atores:** Usuário não logado  
**Descrição:** Exibe a autenticação de um usuário por meio de credenciais  
**Pré-condições:** Usuário deve ter uma conta cadastrada  
**Pós-condições:** Usuário é autenticado  
**Referências:** RF01, RNF02, RNF03  

**Fluxo básico:**  
    1. Usuário informa email e senha  
    2. Sistema valida os dados  
    3. Usuário é autenticado  

**Fluxos alternativos:**  
    **Registro de conta:**  
        1. Usuário acessa tela de cadastro  
        2. Usuário preenche informações de cadastro  
        3. Sistema valida os dados  
        4. Usuário é autenticado  

**Fluxos de exceção:**  
    **Credenciais incorretas:**  
        1. Fluxo é interrompido  
        2. É exibida mensagem de erro apropriada  
