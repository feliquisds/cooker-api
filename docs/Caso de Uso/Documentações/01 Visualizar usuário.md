Apresenta detalhes de um usuário

**Atores:** Usuário não logado, usuário logado
**Descrição:** Exibe informações de um usuário, como foto de perfil e biografia
**Pré-condições:** Usuário a ser visualizado deve existir
**Pós-condições:** Informações do usuário são apresentadas
**Referências:** RF01, RF09, RNF02, RNF03, RN03, RN04

**Fluxo básico:**
    1. Usuário acessa/pesquisa um perfil de outro usuário
    2. Sistema obtém dados e filtra dados sensíveis
    3. Informações são apresentadas ao usuário final

**Fluxos alternativos:**
    **Usuário é dono do perfil:**
        1. Fluxo segue normalmente
        2. Um botão de editar perfil é adicionado

**Fluxos de exceção:**
    **Perfil privado:**
        1. Fluxo é interrompido
        2. É exibido mensagem de erro apropriada