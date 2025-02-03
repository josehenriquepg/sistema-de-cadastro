import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Cadastro {
    public static void main(String[] args) {
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
        Scanner sc = new Scanner(System.in);

        // Printar perguntas e capturar respostas
        for (String question : questions) {
            System.out.println(question);
            System.out.print("Resposta: ");
            String answer = sc.nextLine();
            answers.put(question, answer);
        }

        sc.close();

        // Exibir as respostas
        System.out.println("\nRespostas fornecidas:");
        answers.forEach((question, answer) -> System.out.println(question + " " + answer));
    }
}