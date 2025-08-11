import java.io.*;
import java.util.ArrayList;


public class ArvoreBMais {

    private int  ordem;                 
    private int  maxElementos;          
    private int  maxFilhos;             
    private RandomAccessFile arquivo;   
    private String nomeArquivo;
    
    // Variáveis p\ recursividade
    private int  chave1Aux;
    private int  chave2Aux;
    private long paginaAux;
    private boolean cresceu;
    private boolean diminuiu;
    
   
    private class Pagina {

        protected int    ordem;                 
        protected int    maxElementos;         
        protected int    TAMANHO_PAGINA;
        protected int    TAMANHO_REGISTRO;
        protected int    maxFilhos;             
        protected long   proxima;  
        protected int    n;                     // Elementos na pagina
        protected int[]  chavep;                // Chave principal
        protected int[]  chaveS;                // Chave secundaria       
        protected long[] filhos;                
        
         

        // Construtor
        public Pagina(int o) {

            // Inicializando atributos
            n = 0;
            ordem = o;
            maxFilhos = o;
            maxElementos = o-1;
            proxima = -1;
            chavep = new int[maxElementos];
            chaveS = new int[maxElementos];
            filhos = new long[maxFilhos];
            
            // Criando vazia inicialmente
            for(int i=0; i<maxElementos; i++) {  
                chavep[i] = 0;
                chaveS[i] = 0;
                filhos[i]  = -1;
            }
            filhos[maxFilhos-1] = -1;
            
            // Cálculo do tamanho 
            TAMANHO_REGISTRO = 8;
            TAMANHO_PAGINA = 4 + maxElementos*TAMANHO_REGISTRO + maxFilhos*8 + 16;
        }
        
        // Retornando um vetor de bytes para usar no armazenanmento
        protected byte[] getBytes() throws IOException {
            
            // Fluxo de bytes
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(ba);
            
            // Quantos elementos
            out.writeInt(n);
            
            // Escrevendo elementos
            int i=0;
            while(i<n) {
                out.writeLong(filhos[i]);
                out.writeInt(chavep[i]);
                out.writeInt(chaveS[i]);
                i++;
            }
            out.writeLong(filhos[i]);
            
            // Enchendo espaços vazios
            byte[] registroVazio = new byte[TAMANHO_REGISTRO];
            while(i<maxElementos){
                out.write(registroVazio);
                out.writeLong(filhos[i+1]);
                i++;
            }

            // Ponteiro p proxima
            out.writeLong(proxima);
            
            // Retornando o vetor de bytes
            return ba.toByteArray();
        }

        
        // Reconstruindo a pagina com o vetor lido
        public void setBytes(byte[] buffer) throws IOException {
            
            // Fluxo de bytes
            ByteArrayInputStream ba = new ByteArrayInputStream(buffer);
            DataInputStream in = new DataInputStream(ba);
            
            // Elementos da pagina
            n = in.readInt();
            
            // Lendo elementos
            int i=0;
            while(i<maxElementos) {
                filhos[i]  = in.readLong();
                chavep[i] = in.readInt();
                chaveS[i] = in.readInt();
                i++;
            }
            filhos[i] = in.readLong();
            proxima = in.readLong();
        }
    }


    
    public ArvoreBMais(int o, String na) throws IOException {
        
        // Inicializando atributos
        ordem = o;
        maxElementos = o-1;
        maxFilhos = o;
        nomeArquivo = na;
        
        // Abrindo o arquivo e criando raiz vazia
        arquivo = new RandomAccessFile(nomeArquivo,"rw");
        if(arquivo.length()<8) 
            arquivo.writeLong(-1);  // raiz vazia
    }
    
    // Testando vazia
    public boolean vaziaA() throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        return raiz == -1;
    }
    
        
    // Busca recursiva de um elemento usando a chave.
    public int read(int c1) throws IOException {
        
        // Pegando a raiz
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        
        // Executando
        if(raiz!=-1)
            return readR(c1,raiz);
        else
            return -1;
    }
    
    // Busca recursiva
    private int readR(int chave1, long pagina) throws IOException {
        
        // Se inexistente retorna vazio
        if(pagina==-1)
            return -1;
        
        // Reconstruindo a página 
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
 
        // Encontra o ponto em que a chave deve estar na página
       
        int i=0;
        while(i<pa.n && chave1>pa.chavep[i]) {
            i++;
        }
        
        // Chave encontrada e testando se está na folha
        if(i<pa.n && pa.filhos[0]==-1 && chave1==pa.chavep[i]) {

            // Cria a lista e retorna as encontradas
            ArrayList lista = new ArrayList();
            while(chave1<=pa.chavep[i]) {
                
                if(chave1==pa.chavep[i])
                    lista.add(pa.chaveS[i]);
                i++;

                // Se chegar ao fim passa pra proxima
                if(i==pa.n) {
                    if(pa.proxima==-1)
                        break;
                    arquivo.seek(pa.proxima);
                    arquivo.read(buffer);
                    pa.setBytes(buffer);
                    i=0;
                }
            }
            
            // Resposta
            int[] resposta = new int[lista.size()];
            for(int j=0; j<lista.size(); j++)
                resposta[j] = (int)lista.get(j);
            return resposta[0];

        }
        
        // Se não encontrar testa a proxima
        else if(i==pa.n && pa.filhos[0]==-1) { 
            
            // Testa se há uma próxima folha e retorna vazio se tiver
            if(pa.proxima==-1)
                return -1;
            
            // Lendo a proxima
            arquivo.seek(pa.proxima);
            arquivo.read(buffer);
            pa.setBytes(buffer);
            
            // Testa se a chave é a primeira da próxima folha
            i=0;
            if(chave1<=pa.chavep[0]) {
                
                // Lista de retorno
                ArrayList lista = new ArrayList();
                
                // Testando se a chave foi encontrada e adicionando as secundarias
                while(chave1<=pa.chavep[i]) {
                    if(chave1==pa.chavep[i])
                        lista.add(pa.chaveS[i]);
                    i++;
                    if(i==pa.n) {
                        if(pa.proxima==-1)
                            break;
                        arquivo.seek(pa.proxima);
                        arquivo.read(buffer);
                        pa.setBytes(buffer);
                        i=0;
                    }
                }
                
                // Vetor resposta
                int[] resposta = new int[lista.size()];
                for(int j=0; j<lista.size(); j++)
                    resposta[j] = (int)lista.get(j);
                return resposta[0];
            }
            
            // Se nao tiver proxima pagina retorna vazio
            else
                return -1;
        }
        
        // Não encontrou continua recursividade
        if(i==pa.n || chave1<=pa.chavep[i])
            return readR(chave1, pa.filhos[i]);
        else
            return readR(chave1, pa.filhos[i+1]);
    }
        
    
    // Incluindo novos elementos recursivamente 
    public boolean create(int c1, int c2) throws IOException {

        // Validando chaves
        if(c1<0 || c2<0) {
            System.out.println( "Erro: Chaves negativas" );
            return false;
        }
            
        // Carregando raiz
        arquivo.seek(0);       
        long pagina;
        pagina = arquivo.readLong();

       
        chave1Aux = c1;
        chave2Aux = c2;
        paginaAux = -1;
        cresceu = false;
                
        // Chamada recursiva para inserir chaves
        boolean inserido = createR(pagina);
        
        // Teste pra checar a necessidade de uma nova raiz
        if(cresceu) {
            
            // Cria a nova página que será a raiz
            Pagina novaPagina = new Pagina(ordem);
            novaPagina.n = 1;
            novaPagina.chavep[0] = chave1Aux;
            novaPagina.chaveS[0] = chave2Aux;
            novaPagina.filhos[0] = pagina;
            novaPagina.filhos[1] = paginaAux;
            
            // Acha o espaço em disco ,todas as novas páginas são escrita no fim do arquivo.
            
            arquivo.seek(arquivo.length());
            long raiz = arquivo.getFilePointer();
            arquivo.write(novaPagina.getBytes());
            arquivo.seek(0);
            arquivo.writeLong(raiz);
        }
        
        return inserido;
    }
    
    
    // Inclusão recursiva
    
    private boolean createR(long pagina) throws IOException {
        
        // Teste p inicializar variaveis de controle
        if(pagina==-1) {
            cresceu = true;
            paginaAux = -1;
            return false;
        }
        
        // Lendo a pagina
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
        
        //Buscando ponteiro de descida
        int i=0;
        while(i<pa.n && (chave1Aux>pa.chavep[i] || (chave1Aux==pa.chavep[i] && chave2Aux>pa.chaveS[i]))) {
            i++;
        }
        
        // Testando se ja existe em uma folha
        if(i<pa.n && pa.filhos[0]==-1 && chave1Aux==pa.chavep[i] && chave2Aux==pa.chaveS[i]) {
            cresceu = false;
            return false;
        }
        
        // Continua a busca recursiva até um filho inexistente ser alcançado
        boolean inserido;
        if(i==pa.n || chave1Aux<pa.chavep[i] || (chave1Aux==pa.chavep[i] && chave2Aux<pa.chaveS[i]))
            inserido = createR(pa.filhos[i]);
        else
            inserido = createR(pa.filhos[i+1]);
        

        // Teste de validez
        if(!cresceu)
            return inserido;
        
        // Se tiver espaço na página, faz a inclusão nela
        if(pa.n<maxElementos) {

            // Movendo para direita
            for(int j=pa.n; j>i; j--) {
                pa.chavep[j] = pa.chavep[j-1];
                pa.chaveS[j] = pa.chaveS[j-1];
                pa.filhos[j+1] = pa.filhos[j];
            }
            
            // Inserindo o elemento
            pa.chavep[i] = chave1Aux;
            pa.chaveS[i] = chave2Aux;
            pa.filhos[i+1] = paginaAux;
            pa.n++;
            
            // Escrevendo no arquivo
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            
            // Encerra e retorna
            cresceu=false;
            return true;
        }
        
        // Se não couber na pagina dividir no meio
        
        // Criando uma nova pagina
        Pagina np = new Pagina(ordem);
        
        // Copiando metade dos elemen tos
        int meio = maxElementos/2;
        for(int j=0; j<(maxElementos-meio); j++) {    
            
            // copiando o elemento
            np.chavep[j] = pa.chavep[j+meio];
            np.chaveS[j] = pa.chaveS[j+meio];   
            np.filhos[j+1] = pa.filhos[j+meio+1];  
            
            // limpando espaço livre
            pa.chavep[j+meio] = 0;
            pa.chaveS[j+meio] = 0;
            pa.filhos[j+meio+1] = -1;
        }
        np.filhos[0] = pa.filhos[meio];
        np.n = maxElementos-meio;
        pa.n = meio;
        
        // Teste de lados
        if(i<=meio) {   
            
            // Movendo para direita
            for(int j=meio; j>0 && j>i; j--) {
                pa.chavep[j] = pa.chavep[j-1];
                pa.chaveS[j] = pa.chaveS[j-1];
                pa.filhos[j+1] = pa.filhos[j];
            }
            
            // Inserindo elemento
            pa.chavep[i] = chave1Aux;
            pa.chaveS[i] = chave2Aux;
            pa.filhos[i+1] = paginaAux;
            pa.n++;
            
            // Se for folha primeiro da direita
            if(pa.filhos[0]==-1) {
                chave1Aux = np.chavep[0];
                chave2Aux = np.chaveS[0];
            }
            
            // Senão o maior da esquerda
            else {
                chave1Aux = pa.chavep[pa.n-1];
                chave2Aux = pa.chaveS[pa.n-1];
                pa.chavep[pa.n-1] = 0;
                pa.chaveS[pa.n-1] = 0;
                pa.filhos[pa.n] = -1;
                pa.n--;
            }
        } 
        
        // Se registro novo for para direita
        else {
            int j;
            for(j=maxElementos-meio; j>0 && (chave1Aux<np.chavep[j-1] || (chave1Aux==np.chavep[j-1]&&chave2Aux<np.chaveS[j-1]) ); j--) {
                np.chavep[j] = np.chavep[j-1];
                np.chaveS[j] = np.chaveS[j-1];
                np.filhos[j+1] = np.filhos[j];
            }
            np.chavep[j] = chave1Aux;
            np.chaveS[j] = chave2Aux;
            np.filhos[j+1] = paginaAux;
            np.n++;

            // Seleciona o elemento a ser promovido
            chave1Aux = np.chavep[0];
            chave2Aux = np.chaveS[0];
            
            // Se não for folha remove
            if(pa.filhos[0]!=-1) {
                for(j=0; j<np.n-1; j++) {
                    np.chavep[j] = np.chavep[j+1];
                    np.chaveS[j] = np.chaveS[j+1];
                    np.filhos[j] = np.filhos[j+1];
                }
                np.filhos[j] = np.filhos[j+1];
                
                // Apagando o ultimo
                np.chavep[j] = 0;
                np.chaveS[j] = 0;
                np.filhos[j+1] = -1;
                np.n--;
            }

        }
        
        // Atualizando ponteiros se necessario
        if(pa.filhos[0]==-1) {
            np.proxima=pa.proxima;
            pa.proxima = arquivo.length();
        }

        // Salva no arquivo
        paginaAux = arquivo.length();
        arquivo.seek(paginaAux);
        arquivo.write(np.getBytes());

        arquivo.seek(pagina);
        arquivo.write(pa.getBytes());
        
        return true;
    }

    public boolean update(int chave1, int chave2) throws IOException {
                

        
        // Raiz da arvore
        arquivo.seek(0);       
        long pagina;                
        pagina = arquivo.readLong();
                
        // Chamada recursiva
        boolean updated = updateR(chave1, pagina, chave2);

        return updated;
    }
    

    // Caminhando recursivamente
    private boolean updateR(int chave1, long pagina, int chave2) throws IOException {
        
        boolean updated=false;
        
        // Teste
        if(pagina==-1) {
            return false;
        }
        
        // Lendo o registro
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);

        // Encontra a página em que a chave está presente saltando todas as chaves menores
        
        int i=0;
        while(i<pa.n && (chave1>pa.chavep[i] && !(chave1==pa.chavep[i]))) {
            i++;
        }
        // Chaves na folha
        if(i<pa.n && pa.filhos[0]==-1 && chave1==pa.chavep[i]) {

            // Atualizando endereço
            int j;
            j=i;
            pa.chaveS[j] = chave2;
            
            
            // Atualizando o registro
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            return true;
        }

        // Se a chave não tiver sido encontrada continua recursivamente
        if(i==pa.n || chave1<pa.chavep[i] || (chave1==pa.chavep[i])) {
            updated = updateR(chave1,pa.filhos[i],chave2);
        } else {
            updated = updateR(chave1, pa.filhos[i+1],chave2);
        }

        return updated;
    }
    



    
    
    // Remoção
    public boolean delete(int chave1) throws IOException {
                
        // Encontrando a raiz
        arquivo.seek(0);       
        long pagina;                
        pagina = arquivo.readLong();

        // Variavel de controle
        diminuiu = false;  
                
        // Chamada recursiva
        boolean excluido = deleteR(chave1, pagina);
        
        // Teste p checar a necessidade de exclusão da raiz
        if(excluido && diminuiu) {
            
            // Lê a raiz
            arquivo.seek(pagina);
            Pagina pa = new Pagina(ordem);
            byte[] buffer = new byte[pa.TAMANHO_PAGINA];
            arquivo.read(buffer);
            pa.setBytes(buffer);
            
            // Se tiver 0 elementos atualiza ponteiro
            if(pa.n == 0) {
                arquivo.seek(0);
                arquivo.writeLong(pa.filhos[0]);  
            }
        }
         
        return excluido;
    }
    

    // Exclusão recursiva
    private boolean deleteR(int chave1, long pagina) throws IOException {
        
        // Variaveis
        int diminuido;
        boolean excluido=false;
       
        
        // Testando se foi encontrado
        if(pagina==-1) {
            diminuiu=false;
            return false;
        }
        
        // Lendo o registro
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);

        // Encontrando a pagina que estão presente as chaves
        int i=0;
        while(i<pa.n && (chave1>pa.chavep[i] || (chave1==pa.chavep[i]))) {
            i++;
        }

        // Se foi encontrada
        if(i<pa.n && pa.filhos[0]==-1 && chave1==pa.chavep[i]) {

            // Movendo todos elementos para posição anterior
            int j;
            for(j=i; j<pa.n-1; j++) {
                pa.chavep[j] = pa.chavep[j+1];
                pa.chaveS[j] = pa.chaveS[j+1];
            }
            pa.n--;
            
            // limpando o ultimo
            pa.chavep[pa.n] = 0;
            pa.chaveS[pa.n] = 0;
            
            // Atualizando a pagina
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            
            // Se houver menos elemento que o necessario aponta para fusão
            diminuiu = pa.n<maxElementos/2;
            return true;
        }

        // Se a chave não tiver sido encontrada continua a busca recursiva por uma nova página. 
        if(i==pa.n || chave1<pa.chavep[i] || (chave1==pa.chavep[i])) {
            excluido = deleteR(chave1,pa.filhos[i]);
            diminuido = i;
        } else {
            excluido = deleteR(chave1, pa.filhos[i+1]);
            diminuido = i+1;
        }
        
    
        
        // Testando se precisa de fusão
        if(diminuiu) {

            // Carrega a página filho que ficou com menos elementos
            long paginaFilho = pa.filhos[diminuido];
            Pagina pFilho = new Pagina(ordem);
            arquivo.seek(paginaFilho);
            arquivo.read(buffer);
            pFilho.setBytes(buffer);
            
            // Criando uma pagina nova
            long paginaIrmao;
            Pagina pIrmao;
            
            // Tentando a fusão com o da esquerda
            if(diminuido>0) {
                
                // Carregando o da esquerda
                paginaIrmao = pa.filhos[diminuido-1];
                pIrmao = new Pagina(ordem);
                arquivo.seek(paginaIrmao);
                arquivo.read(buffer);
                pIrmao.setBytes(buffer);
                
                // Testando se o irmão pode ceder
                if(pIrmao.n>maxElementos/2) {
                    
                    // Movendo todos para esquerda
                    for(int j=pFilho.n; j>0; j--) {
                        pFilho.chavep[j] = pFilho.chavep[j-1];
                        pFilho.chaveS[j] = pFilho.chaveS[j-1];
                        pFilho.filhos[j+1] = pFilho.filhos[j];
                    }
                    pFilho.filhos[1] = pFilho.filhos[0];
                    pFilho.n++;
                    
                    // Se for folha, copia o elemento
                    if(pFilho.filhos[0]==-1) {
                        pFilho.chavep[0] = pIrmao.chavep[pIrmao.n-1];
                        pFilho.chaveS[0] = pIrmao.chaveS[pIrmao.n-1];
                    }
                    
                    // Se não for folha, rotaciona os elementos,
                    else {
                        pFilho.chavep[0] = pa.chavep[diminuido-1];
                        pFilho.chaveS[0] = pa.chaveS[diminuido-1];
                    }

                    // Copia o elemento do irmão para o pai
                    pa.chavep[diminuido-1] = pIrmao.chavep[pIrmao.n-1];
                    pa.chaveS[diminuido-1] = pIrmao.chaveS[pIrmao.n-1];
                        
                    
                    // Tira o elemnto do irmão
                    pFilho.filhos[0] = pIrmao.filhos[pIrmao.n];
                    pIrmao.n--;
                    diminuiu = false;
                }
                
                // Se não puder ceder, faz a fusão
                else {

                    // Se não for folha copiar para o de cima
                    
                    if(pFilho.filhos[0] != -1) {
                        pIrmao.chavep[pIrmao.n] = pa.chavep[diminuido-1];
                        pIrmao.chaveS[pIrmao.n] = pa.chaveS[diminuido-1];
                        pIrmao.filhos[pIrmao.n+1] = pFilho.filhos[0];
                        pIrmao.n++;
                    }
                    
                    
                    // Copia todos os registros para o irmão da esquerda
                    for(int j=0; j<pFilho.n; j++) {
                        pIrmao.chavep[pIrmao.n] = pFilho.chavep[j];
                        pIrmao.chaveS[pIrmao.n] = pFilho.chaveS[j];
                        pIrmao.filhos[pIrmao.n+1] = pFilho.filhos[j+1];
                        pIrmao.n++;
                    }
                    pFilho.n = 0; 
                    
                    // Se for folha copia para o proximo
                    if(pIrmao.filhos[0]==-1)
                        pIrmao.proxima = pFilho.proxima;
                    
                    // Move registros no pai
                    int j;
                    for(j=diminuido-1; j<pa.n-1; j++) {
                        pa.chavep[j] = pa.chavep[j+1];
                        pa.chaveS[j] = pa.chaveS[j+1];
                        pa.filhos[j+1] = pa.filhos[j+2];
                    }
                    pa.chavep[j] = 0;
                    pa.chaveS[j] = 0;
                    pa.filhos[j+1] = -1;
                    pa.n--;
                    diminuiu = pa.n<maxElementos/2;  // testa se o pai tem os elementos necessarios
                }
            }
            
            // Fusão com o direito
            else {
                
                // Carregando o irmão
                paginaIrmao = pa.filhos[diminuido+1];
                pIrmao = new Pagina(ordem);
                arquivo.seek(paginaIrmao);
                arquivo.read(buffer);
                pIrmao.setBytes(buffer);
                
                // Testando se pode ceder
                if(pIrmao.n>maxElementos/2) {
                    
                    // Se for folha
                    if( pFilho.filhos[0]==-1 ) {
                    
                        //copiando elemento do irmão
                        pFilho.chavep[pFilho.n] = pIrmao.chavep[0];
                        pFilho.chaveS[pFilho.n] = pIrmao.chaveS[0];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[0];
                        pFilho.n++;

                        // Subindo o proximo elemento
                        pa.chavep[diminuido] = pIrmao.chavep[1];
                        pa.chaveS[diminuido] = pIrmao.chaveS[1];
                        
                    } 
                    
                    // Se não for folha rotaciona
                    else {
                        
                        // Copia o elemento do pai
                        pFilho.chavep[pFilho.n] = pa.chavep[diminuido];
                        pFilho.chaveS[pFilho.n] = pa.chaveS[diminuido];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[0];
                        pFilho.n++;
                        
                        // Sobe o elemento esquerdo do irmão para o pai
                        pa.chavep[diminuido] = pIrmao.chavep[0];
                        pa.chaveS[diminuido] = pIrmao.chaveS[0];
                    }
                    
                    // move todos os registros no irmão para a esquerda
                    int j;
                    for(j=0; j<pIrmao.n-1; j++) {
                        pIrmao.chavep[j] = pIrmao.chavep[j+1];
                        pIrmao.chaveS[j] = pIrmao.chaveS[j+1];
                        pIrmao.filhos[j] = pIrmao.filhos[j+1];
                    }
                    pIrmao.filhos[j] = pIrmao.filhos[j+1];
                    pIrmao.n--;
                    diminuiu = false;
                }
                
                // Se não puder ceder funde os dois irmãos
                else {

                    // Se não for folha, o elemento do pai deve ser copiado para o irmão
                    
                    if(pFilho.filhos[0] != -1) {
                        pFilho.chavep[pFilho.n] = pa.chavep[diminuido];
                        pFilho.chaveS[pFilho.n] = pa.chaveS[diminuido];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[0];
                        pFilho.n++;
                    }
                    
                    // Copia o irmão da direita
                    for(int j=0; j<pIrmao.n; j++) {
                        pFilho.chavep[pFilho.n] = pIrmao.chavep[j];
                        pFilho.chaveS[pFilho.n] = pIrmao.chaveS[j];
                        pFilho.filhos[pFilho.n+1] = pIrmao.filhos[j+1];
                        pFilho.n++;
                    }
                    pIrmao.n = 0; 
                    
                    // Se for folha copia ponteiro para proximo
                    pFilho.proxima = pIrmao.proxima;
                    
                    // move os registros no pai
                    for(int j=diminuido; j<pa.n-1; j++) {
                        pa.chavep[j] = pa.chavep[j+1];
                        pa.chaveS[j] = pa.chaveS[j+1];
                        pa.filhos[j+1] = pa.filhos[j+2];
                    }
                    pa.n--;
                    diminuiu = pa.n<maxElementos/2;  // teste se o pai ficou com os elementos
                }
            }
            
            // Atualizando
            arquivo.seek(pagina);
            arquivo.write(pa.getBytes());
            arquivo.seek(paginaFilho);
            arquivo.write(pFilho.getBytes());
            arquivo.seek(paginaIrmao);
            arquivo.write(pIrmao.getBytes());
        }
        return excluido;
    }
    
    
    // Imprimindo a arovre
    public void print() throws IOException {
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        if(raiz!=-1)
            print1(raiz);
        System.out.println();
    }
    
    // Impressão recursiva
    private void print1(long pagina) throws IOException {
        
        // Chamadas recursivas return
        if(pagina==-1)
            return;
        int i;

        // Lendo registro da pagina passado
        arquivo.seek(pagina);
        Pagina pa = new Pagina(ordem);
        byte[] buffer = new byte[pa.TAMANHO_PAGINA];
        arquivo.read(buffer);
        pa.setBytes(buffer);
        
        // Imprimindo a pagina
        String endereco = String.format("%04d", pagina);
        System.out.print(endereco+"  " + pa.n +":"); // endereço e num de elementos
        for(i=0; i<maxElementos; i++) {
            System.out.print("("+String.format("%04d",pa.filhos[i])+") "+String.format("%2d",pa.chavep[i])+","+String.format("%2d",pa.chaveS[i])+" ");
        }
        System.out.print("("+String.format("%04d",pa.filhos[i])+")");
        if(pa.proxima==-1)
            System.out.println();
        else
            System.out.println(" --> ("+String.format("%04d", pa.proxima)+")");
        
        // Chamando recursivamente cada filho
        if(pa.filhos[0] != -1) {
            for(i=0; i<pa.n; i++)
                print1(pa.filhos[i]);
            print1(pa.filhos[i]);
        }
    }
       
}
