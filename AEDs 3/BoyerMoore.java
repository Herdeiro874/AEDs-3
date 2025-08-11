import java.io.RandomAccessFile;
import java.io.IOException;


public class BoyerMoore {

    private Integer quantidade = 0;
    private Integer contagem = 0;

    static int TAM_MAX = 256;

    static void caracterRuim(char []str, int tam, int chaRuim[]){
        //inicializa vetor
        for (int i = 0; i < TAM_MAX; i++)
            chaRuim[i] = -1;

        //preenche posições do padrão
        for (int i = 0; i < tam; i++){
            chaRuim[(int) str[i]] = i;
        }   
	}
	
    public void casoUm(int []aux, int []posBoy, char []padrao, int pl){
        //pl = padrão.length 
        int i = pl, j = pl + 1;
        posBoy[i] = j;

        //percorre o padrão
        while(i > 0){
            while(j <= pl && padrao[i - 1] != padrao[j - 1]){
                if (aux[j] == 0){
                    contagem++;
                    aux[j] = j - i;
                }
                //atualiza posição
                j = posBoy[j];
            }
            //parar o loop quando acha posição 
            i--; j--;
            posBoy[i] = j;
        }
    }

    public void casos23(int []aux, int []posBoy, char []padrao, int pl){
        int i, j;
        j = posBoy[0];

        //percorre o padrão
        for(i = 0; i <= pl; i++){
            if(aux[i] == 0){
                contagem++;
                aux[i] = j;
            }  
            //o sufixo fica menor
            if (i == j){
                contagem++;
                j = posBoy[j];
            }  
        }
    }

    public void busca(char texto[], char padrao[]){

        int j = 0, cr;

        int tl = texto.length;    //tam texto
        int pl = padrao.length;   //tam padrão 
        

        //vetor de deslocamento
        int chaRuim[] = new int[TAM_MAX];
        int []posBoy = new int[pl + 1];
        int []aux = new int[pl + 1];
        
        //Vetor de caractere ruim
        caracterRuim(padrao, pl, chaRuim);
        j = 0; 

        for(int i = 0; i < pl + 1; i++){
            aux[i] = 0;
        }
        
        //Sufixo bom       
        casoUm(aux, posBoy, padrao, pl);
        casos23(aux, posBoy, padrao, pl); 

        //percorre texto ate (tl - pl)
        while(j <= (tl - pl))
        {
            cr = pl-1;
            while(cr >= 0 && padrao[cr] == texto[j+cr])
            {
                cr--;
            }
            if (cr < 0) //padrão encontrado
            {
                j += (j+pl < tl)? pl-chaRuim[texto[j+pl]] : 1;
                quantidade++;
            }else
            {                
                int pos = texto[j+cr];
                int x = 1;
                if(pos<=256){                                   //cr = carater ruim
                    x = maior(1, cr - chaRuim[texto[j+cr]]);  //evita um deslocamento negativo
                }
                
                int y = aux[cr + 1]; //sufixo bom

                //vê qual o maior deslocamento
                if(x>y){
                    j += x;
                }else{
                    j += y;
                } 
            }   
        }
    }

    static int maior (int a, int b) { 
        if(a>b){
            return a;
        }else{
            return b;
        }
    }

    public void iniBoyer(String arquivo, String padrao) throws IOException{ //le o arquivo
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        
        boolean fim = false;
        arq.seek(0);
        int idFim = arq.readInt();
        int pos = 4;                            //ponteiro primeiro registro
        int tam = 0;
        Jogos jogos = new Jogos();    

        do{
            arq.seek(pos);
            tam = arq.readInt();                //lê tamanho
            long p1 = arq.getFilePointer();
            boolean lapide = arq.readBoolean(); //lê lápide
            if(!lapide){                        //verifica é valido
                arq.seek(p1);
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte);            //lê array
                jogos.fromByteArray(arrayByte); //transforma o array em objeto
                String texto = jogos.toString();
                busca(texto.toCharArray(), padrao.toCharArray());
                if(jogos.getId()==idFim){       //verifica EOF
                    fim = true;
                }
            } 
            pos += tam + 4;                     //avança para o proximo registro
        }while(!fim);    
        arq.close();
    }      

    public void results(){
        System.out.println("Número de instâncias do padrão: " + quantidade);
        System.out.println("Número de comparações: " + contagem);
    }
}