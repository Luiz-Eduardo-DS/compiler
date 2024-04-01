Projeto de Análise Sintática e Semântica em Pseudocódigo

Este é um projeto que implementa um analisador sintático e semântico para a linguagem de pseudocódigo. O objetivo do analisador é verificar se um programa escrito em pseudocódigo está sintaticamente correto e se segue as regras semânticas da linguagem.

Funcionalidades
Análise Sintática: O analisador sintático verifica se o programa escrito em pseudocódigo está estruturalmente correto, ou seja, se segue a gramática da linguagem.

Análise Semântica: Além da análise sintática, o analisador semântico verifica se o programa está semanticamente correto, ou seja, se as variáveis são usadas corretamente, se estão declaradas antes de serem usadas, se os tipos estão corretos, entre outras verificações.

Componentes
O projeto é composto por duas classes principais:

Parser: Esta classe é responsável pela análise sintática do programa em pseudocódigo. Ela recebe uma lista de tokens (símbolos léxicos) como entrada e realiza a análise para verificar se o programa está estruturalmente correto.

Semantic: Esta classe é responsável pela análise semântica do programa em pseudocódigo. Ela implementa várias funções para verificar se as variáveis são usadas corretamente, se os tipos são compatíveis, entre outras verificações.

Como Usar
Para utilizar o analisador sintático e semântico, siga estes passos:

Compile as classes Parser e Semantic.
Crie uma lista de tokens a partir do programa em pseudocódigo que deseja analisar.
Crie uma instância da classe Parser, passando a lista de tokens como argumento para o construtor.
Chame o método parse() para iniciar a análise sintática e semântica do programa.
Exemplo de Uso
Aqui está um exemplo de como utilizar o analisador sintático e semântico em um programa em pseudocódigo:

java
Copy code
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Suponha que 'tokens' seja uma lista de tokens gerada a partir do programa em pseudocódigo
        List<Tokens> tokens = getTokensFromPseudocodeProgram("algoritmo Teste\n ...");

        // Cria uma instância do Parser
        Parser parser = new Parser(tokens);

        // Inicia a análise sintática e semântica
        parser.parse();
    }
}
Contribuição
Se você quiser contribuir com este projeto, sinta-se à vontade para fazer um fork do repositório, implementar melhorias e enviar um pull request. As contribuições são bem-vindas!

Espero que este README forneça uma visão geral clara do projeto e de como usá-lo. Se precisar de mais informações ou tiver dúvidas, não hesite em entrar em contato.
