import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Register {
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

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    registerUser(sc);
                    break;
                case 2:
                    listUsers();
                    break;
                case 3:
                    addQuestion(sc);
                    break;
                case 4:
                    deleteQuestion(sc);
                    break;
                case 5:
                    searchUser(sc);
                    break;
                case 0:
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
        String fileName = "formulario.txt";
        List<String> questions = new ArrayList<>();

        // Ler o arquivo e armazenar as perguntas em uma lista
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            questions = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        // Cria um mapa para armazenar as respostas do usuário
        Map<String, String> answers = new LinkedHashMap<>();

        // Printar perguntas e capturar respostas
        for (String question : questions) {
            System.out.println(question);
            System.out.print("Resposta: ");
            String answer = sc.nextLine();
            answers.put(question, answer);
        }

        try {
            registerValidate(answers);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro: " + e.getMessage());
            return;
        }

        // Exibir as respostas
        System.out.println("\nRespostas fornecidas:");
        answers.forEach((question, answer) -> System.out.println(question + " " + answer));

        // Obter nome do usuário e formatar nome do arquivo "n-NOMEEMMAIUSCULAS.TXT"
        String userName = answers.get("1 - Qual seu nome completo?");
        if (userName != null && !userName.isEmpty()) {
            File dir = new File(".");
            File[] usersFiles = dir.listFiles((d, name) -> name.matches("\\d+-[A-Z]+\\.TXT"));

            int fileNumber = 1;
            if (usersFiles != null && usersFiles.length > 0) {
                fileNumber = Arrays.stream(usersFiles)
                        .map(f -> f.getName().split("-")[0]) // Pega o número inicial no nome do arquivo
                        .mapToInt(Integer::parseInt)
                        .max()
                        .orElse(0) + 1; // Incrementa para o próximo número disponível
            }

            String userFileName = fileNumber + "-" + userName.toUpperCase().replaceAll("\\s+", "") + ".TXT";

            // Salvar as respostas em um arquivo
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(userFileName))) {
                int counter = 1;
                for (Map.Entry<String, String> entry : answers.entrySet()) {
                    bw.write(counter + " - " + entry.getValue());
                    bw.newLine();
                    counter++;
                }
                System.out.println("Respostas salvas no arquivo: " + userFileName);
            } catch (IOException e) {
                System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
            }
        } else {
            System.err.println("Nome inválido. Não foi possível criar o arquivo.");
        }
    }

    /**
     * Valida os dados do usuário antes do cadastro.
     *
     * @param answers Um mapa contendo as respostas do usuário, com as perguntas como chaves.
     * @throws IllegalArgumentException Se alguma das validações falhar.
     */
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
        File dir = new File(".");
        // Filtra arquivos que seguem o padrão "n-NOME.TXT"
        File[] usersFiles = dir.listFiles((d, name) -> name.matches("\\d+-[A-Z]+\\.TXT"));

        // Verifica se existem usuários cadastrados
        if (usersFiles == null || usersFiles.length == 0) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        // Ordena os arquivos pelo número inicial do nome do arquivo
        Arrays.sort(usersFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getName().split("-")[0])));

        // Exibe a lista de usuários cadastrados
        System.out.println("\nLista de usuários cadastrados:");
        int counter = 1;
        for (File file : usersFiles) {
            // Extrai o nome do usuário do nome do arquivo
            String name = file.getName().substring(file.getName().indexOf('-') + 1).replace(".TXT", "");
            System.out.printf("%-3d %s%n", counter, name); // Formatação alinhada
            counter++;
        }
    }

    /**
     * Adiciona uma nova pergunta ao formulário de perguntas armazenado em arquivo.
     * A pergunta será numerada automaticamente com base na quantidade de perguntas já existentes.
     */
    private static void addQuestion(Scanner sc) {
        String fileName = "formulario.txt";
        List<String> questions = new ArrayList<>();

        // Tenta ler as perguntas já existentes no arquivo e armazená-las na lista
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            questions = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        // Solicita ao usuário que insira uma nova pergunta com validação
        String newQuestion;
        do {
            System.out.print("Digite a nova pergunta: ");
            newQuestion = sc.nextLine().trim();
            if (newQuestion.isEmpty()) {
                System.out.println("A pergunta não pode ser vazia. Tente novamente.");
            }
        } while (newQuestion.isEmpty());
        // Determina o número da nova pergunta e adiciona à lista
        int numQuestion = questions.size() + 1;
        String formattedQuestion = numQuestion + " - " + newQuestion;

        // Escreve apenas a nova pergunta no arquivo, sem reescrever as antigas
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(formattedQuestion);
            writer.newLine();
            System.out.println("Pergunta adicionada com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao adicionar pergunta: " + e.getMessage());
        }
    }

    private static void deleteQuestion(Scanner sc) {
        String fileName = "formulario.txt";
        List<String> questions = new ArrayList<>();

        // Lê todas as linhas do arquivo e as armazena na lista de perguntas
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            questions = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        // Impede de deletar as 4 perguntas iniciais
        if (questions.size() <=4) {
            System.out.println("Só novas perguntas podem ser deletadas.");
            return;
        }

        // Exibe apenas as perguntas que podem ser deletadas
        for (int i = 4; i < questions.size(); i++) {
            System.out.println((i + 1) + " - " + questions.get(i));
        }

        boolean valid = false;
        int index = -1;

        // Permite ao usuário tentar novamente caso insira um índice inválido
        while (!valid) {
            System.out.print("Digite o número da pergunta que deseja deletar: ");
            index = sc.nextInt();
            sc.nextLine();  // Limpa o buffer do scanner

            if (index > 4 && index <= questions.size()) {
                valid = true;
            } else {
                System.out.println("Operação inválida. Somente perguntas a partir da 5ª podem ser removidas. Tente novamente.");
            }
        }

        // Remove a pergunta selecionada
        questions.remove(index - 1);

        // Tenta reescrever o arquivo com a lista de perguntas atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < questions.size(); i++) {
                writer.write((i + 1) + " - " + questions.get(i).split(" - ", 2)[1]);
                writer.newLine();
            }
            System.out.println("Pergunta deletada com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao remover pergunta: " + e.getMessage());
        }
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