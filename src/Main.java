import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Menu Principal:");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Listar todos os usuários cadastrados");
            System.out.println("3 - Cadastrar nova pergunta ao fomrulário");
            System.out.println("4 - Deletar pergunta do formulãrio");
            System.out.println("5 - Pesquisar usuário por nome, idade ou email");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            String option = sc.nextLine();

            switch (option) {
                case "1":
                    registerUser(sc);
                    break;
                case "2":
                    listUsers();
                    break;
                case "3":
                    addQuestion(sc);
                    break;
                case "4":
                    deleteQuestion(sc);
                    break;
                case "5":
                    searchUser(sc);
                    break;
                case "0":
                    System.out.println("Saindo...");
                    sc.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    // Registra os novos usuários
    private static void registerUser(Scanner sc) {

    }

    //Valida os dados do usuário antes do cadastro
    private static void registerValidate(Map<String, String> answers) {
        String name = answers.get("1 - Qual seu nome completo?");
        String email = answers.get("2 - Qual seu email de contato?");
        String ageStr = answers.get("3 - Qual sua idade?");
        String height = answers.get("4 - Qual sua altura?");

        // Valida o tamanho mínimo do nome
        if (name == null || name.length() < 10) {
            throw new IllegalArgumentException("O nome deve ter pelo menos 10 caracteres.");
        }

        // Valida o formato do email
        if (!email.matches("^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email inválido. Formato esperado: exemplo@dominio.com");
        }

        // Valida se a idade é um número válido e maior que 18
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 18) {
                throw new IllegalArgumentException("O nome deve ter mais de 18 anos.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Idade deve ser um número válido.");
        }

        // Valida o formato da altura (deve conter uma vírgula ou ponto)
        if (!height.matches("\\d+[,.]\\d+")) {
            throw new IllegalArgumentException("A altura deve estar no formato correto, ex: 1,75.");
        }
    }

    /**
     * Lista todos os usuários cadastrados no diretório atual.
     * Os usuários são identificados por arquivos no formato "n-NOME.TXT",
     * onde "n" é um número sequencial e "NOME" está em maiúsculas.
     * Se não houver usuários cadastrados, exibe uma mensagem informativa.
     */
    private static void listUsers() {

    }

    /**
     * Adiciona uma nova pergunta ao formulário de perguntas armazenado em arquivo.
     * A pergunta será numerada automaticamente com base na quantidade de perguntas já existentes.
     */
    private static void addQuestion(Scanner sc) {

    }

    private static void deleteQuestion(Scanner sc) {
        
    }

    // Buscar usuários cadastrados com base em um termo de pesquisa.
    private static void searchUser (Scanner sc) {
        System.out.println("Buscar Usuário.");
        System.out.print("Nome, idade ou email do usuário: ");
        String searchTerm = sc.nextLine().toLowerCase(); // Converte o termo de busca para minúsculas

        File dir = new File(".");
        // Filtra os arquivos que seguem o padrão de nome (número seguido de letras maiúsculas e ".TXT")
        File[] files = dir.listFiles((d, name) -> name.matches("\\d+-[A-Z]+\\.TXT"));

        // Verifica se existem arquivos de usuários
        if (files == null || files.length == 0) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        boolean found = false;

        // Itera sobre os arquivos encontrados
        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<String> items = br.lines().collect(Collectors.toList()); // Lê as linhas do arquivo e as armazena em uma lista
                // Extrai nome, email e idade do usuário
                String name = items.get(0).split(" - ")[1];
                String email = items.get(1).split(" - ")[1];
                String age = items.get(2).split(" - ")[1];

                // Verifica se o termo de busca está contido em qualquer uma das informações do usuário
                if (name.toLowerCase().contains(searchTerm) || email.toLowerCase().contains(searchTerm) || age.toLowerCase().contains(searchTerm)) {
                    System.out.println("\nUsuário encontrado:");
                    items.forEach(System.out::println); // Exibe as informações do usuário
                    found = true;
                }
            } catch (IOException e) {
                // Caso haja erro ao ler o arquivo, exibe a mensagem de erro
                System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            }
        }

        // Caso nenhum usuário tenha sido encontrado, exibe uma mensagem
        if (!found) {
            System.out.println("Nenhum usuário encontrado com o termo informado.");
        }
    }
}