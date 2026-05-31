Apresenta detalhes de um livro de receita

**Atores:** Usuário não logado, usuário logado
**Descrição:** Exibe informações de um livro de receita, como descrição, autor, e itens
**Pré-condições:** Livro de receita a ser visualizado deve existir
**Pós-condições:** Informações do livro de receita são apresentadas
**Referências:** RF04, RF09, RNF02, RNF03, RN03, RN04, RN05

**Fluxo básico:**
    1. Usuário acessa/pesquisa um livro de receita
    2. Sistema obtém dados
    3. Informações são apresentadas ao usuário final

**Fluxos alternativos:**
    **Usuário é dono do livro de receita:**
        1. Fluxo segue normalmente
        2. Um botão de editar perfil é adicionado

**Fluxos de exceção:**
    **Perfil/livro de receita privado:**
        1. Fluxo é interrompido
        2. É exibido mensagem de erro "livro de receita privado"