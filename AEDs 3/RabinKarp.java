import java.io.IOException;
import java.io.RandomAccessFile;

public class RabinKarp {
    private int count;
    private int comparisons;

    public void readArq(String arquivo, String padrao) throws IOException {
        count = 0; // Inicializa o contador de padrões encontrados
        comparisons = 0; // Inicializa o contador de comparações

        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");

        arq.seek(0);
        int idFim = arq.readInt();
        int pos = 4;
        int tam = 0;
        Jogos temp = new Jogos();
        boolean acabou = false;

        do {
            arq.seek(pos);
            tam = arq.readInt();
            long p1 = arq.getFilePointer();
            boolean lapide = arq.readBoolean();
            if (!lapide) {
                arq.seek(p1);
                byte[] arrayByte = new byte[tam];
                arq.read(arrayByte);
                temp.fromByteArray(arrayByte);
                String texto = temp.toString();
                int patternCount = search(texto.toCharArray(), padrao.toCharArray()); // Incrementa o contador com o resultado da busca
                count += patternCount;
                pos += tam + 4;
            }
            if (temp.getId() == idFim) {
                acabou = true;
            }
        } while (!acabou);
        arq.close();

        System.out.println("Quantidade total de padrões encontrados: " + count); // Imprime a quantidade total de padrões encontrados
        System.out.println("Quantidade total de comparações: " + comparisons); // Imprime a quantidade total de comparações
    }

    private int search(char[] texto, char[] padrao) {
        int n = texto.length;
        int m = padrao.length;
        int d = 256; // Tamanho do alfabeto (ASCII)
        int q = 101; // Número primo para calcular o hash

        int p = 0; // Hash do padrão
        int t = 0; // Hash do texto
        int h = 1;

        // Valor de h = d^(m-1) % q
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % q;
        }

        int count = 0; // Contador de padrões encontrados

        // Calcula o hash do padrão e do primeiro trecho do texto
        for (int i = 0; i < m; i++) {
            p = (d * p + padrao[i]) % q;
            t = (d * t + texto[i]) % q;
        }

        // Procura o padrão no texto
        for (int i = 0; i <= n - m; i++) {
            // Se o hash do padrão é igual ao hash do trecho do texto
            // Verifica se os caracteres são iguais para evitar colisões de hash
            if (p == t) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    comparisons++; // Incrementa o contador de comparações
                    if (texto[i + j] != padrao[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return count + 1; // Retorna o número de comparações até encontrar o primeiro padrão
                }
            }
            // Se o padrão não foi encontrado, calcula o hash do próximo trecho do texto
            if (i < n - m) {
                t = (d * (t - texto[i] * h) + texto[i + m]) % q;
                // Se o valor do hash for negativo, adiciona q para torná-lo positivo
                if (t < 0) {
                    t = t + q;
                }
            }
            comparisons++; // Incrementa o contador de comparações
        }
        return count;
    }
}