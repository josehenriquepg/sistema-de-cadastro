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
                case 0:
                    System.out.println("Saindo...");
                    sc.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    public static void registerUser(Scanner sc) {
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

    public static void listUsers() {
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
}