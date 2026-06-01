Realiza recuperação de senha

**Atores:** Usuário não logado  
**Descrição:** Realiza a recuperação de acesso à conta por meio de um link enviado por email  
**Pré-condições:** Usuário deve ter uma conta cadastrada  
**Pós-condições:** Usuário pode redefinir a senha  
**Referências:** RF01, RF11, RNF02, RNF03  

**Fluxo básico:**  
    1. Usuário solicita recuperação de senha  
    2. Sistema envia um link de redefinição por email  
    3. Usuário informa a nova senha  
    4. Sistema atualiza a senha  

**Fluxos alternativos:**  
    **Email não cadastrado:**    
        1. Sistema não encontra email, e não produz erro  

**Fluxos de exceção:**  
    **Link expirado:**  
        1. Fluxo é interrompido  
        2. É solicitado um novo link de recuperação  
