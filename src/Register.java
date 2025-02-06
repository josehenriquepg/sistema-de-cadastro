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

        // Exibir as respostas
        System.out.println("\nRespostas fornecidas:");
        answers.forEach((question, answer) -> System.out.println(question + " " + answer));

        // Obter nome do usuário e formatar nome do arquivo
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

    private static void listUsers() {
        File dir = new File(".");
        File[] usersFiles = dir.listFiles((d, name) -> name.matches("\\d+-[A-Z]+\\.TXT"));

        if (usersFiles == null || usersFiles.length == 0) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        System.out.println("\nLista de usuários cadastrados:");
        int counter = 1;
        for (File file : usersFiles) {
            String name = file.getName().split("-", 2)[1].replace(".TXT", "");
            System.out.println(counter + " - " + name);
            counter++;
        }
    }

    private static void addQuestion(Scanner sc) {
        System.out.print("Digite a nova pergunta: ");
        String newQuestion = sc.nextLine();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("formulario.txt", true))) {
            writer.write(newQuestion);
            writer.newLine();
            System.out.println("Pergunta adicionada com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao adicionar pergunta: " + e.getMessage());
        }
    }

    private static void deleteQuestion(Scanner sc) {
        List<String> questions = new ArrayList<>();
        String fileName = "formulario.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            questions = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        if (questions.size() <=4) {
            System.out.println("Só novas perguntas podem ser deletadas.");
            return;
        }

        for (int i = 4; i < questions.size(); i++) {
            System.out.println((i + 1) + " - " + questions.get(i));
        }

        System.out.print("Digite o número da pergunta a ser removida: ");
        int index = sc.nextInt();
        sc.nextLine();

        if (index <= 4 || index > questions.size()) {
            System.out.println("Operação inválida. Somente perguntas a partir da 5ª podem ser removidas.");
            return;
        }

        questions.remove(index - 1);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String question : questions) {
                writer.write(question);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao remover pergunta: " + e.getMessage());
        }
    }

    private static void searchUser (Scanner sc) {
        System.out.println("Buscar Usuário.");
        System.out.print("Nome, idade ou email do usuário: ");
        String searchTerm = sc.nextLine().toLowerCase();

        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.matches("\\d+-[A-Z]+\\.TXT"));

        if (files == null || files.length == 0) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        boolean found = false;
        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<String> items = br.lines().collect(Collectors.toList());
                String name = items.get(0).split(" - ")[1];
                String email = items.get(1).split(" - ")[1];
                String age = items.get(2).split(" - ")[1];

                if (name.toLowerCase().contains(searchTerm) || email.toLowerCase().contains(searchTerm) || age.toLowerCase().contains(searchTerm)) {
                    System.out.println("\nUsuário encontrado:");
                    items.forEach(System.out::println);
                    found = true;
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            }
        }

        if (!found) {
            System.out.println("Nenhum usuário encontrado com o termo informado.");
        }
    }
}