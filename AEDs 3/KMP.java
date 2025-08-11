import java.io.IOException;
import java.io.RandomAccessFile;

public class KMP {

    private Integer contagem = 0;
    private int quantidade = 0;

    //método principal
    private void busca(char[] texto, char[] padrao) {
        int n = texto.length; //tamanho do texto
        int m = padrao.length; //tamanho do padrão

        int[] lps = computaArray(padrao, m);
        int i = 0;
        int j = 0;

        while (i < n) { //caminha pelo texto
            contagem++;
            if (padrao[j] == texto[i]) {
                j++;
                i++;
            }
            if (j == m) { //padrão foi totalmente correspondido
                quantidade++; 
                j = lps[j - 1];
            } else {
                if (i < n && padrao[j] != texto[i]) {
                    if (j != 0)
                        j = lps[j - 1];
                    else
                        i++;
                }
            }
        }
    }

    //calcula o array LPS
    private int[] computaArray(char[] padrao, int m) {
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (padrao[i] == padrao[len]) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = len;
                    i++;
                }
            }
        }

        return lps;
    }

    //inicia a busca KMP
    public void iniKMP(String arquivo, String padrao) throws IOException {
        quantidade = 0;

        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");

        arq.seek(0);
        int idFim = arq.readInt();
        int pos = 4;                            //ponteiro primeiro registro
        int tam = 0;
        Jogos temp = new Jogos();
        boolean acabou = false;

        do {
            arq.seek(pos);
            tam = arq.readInt();                //lê tamanho
            long p1 = arq.getFilePointer();
            boolean lapide = arq.readBoolean(); // Lê a lápide
            if (!lapide) {                      //verifica é valido
                arq.seek(p1);
                byte[] arrayByte = new byte[tam];
                arq.read(arrayByte);            //lê array
                temp.fromByteArray(arrayByte);  //transforma o array em objeto
                String texto = temp.toString();
                busca(texto.toCharArray(), padrao.toCharArray()); //realiza a busca KMP no texto
                if (temp.getId() == idFim) {    //verifica EOF
                    acabou = true;
                }
            }
            pos += tam + 4;                     //avança para o proximo registro
        } while (!acabou);
        arq.close();

        System.out.println("\nNúmero de instâncias do padrão: "+ quantidade);
        System.out.println("Número de comparações: " + contagem);
    }
}