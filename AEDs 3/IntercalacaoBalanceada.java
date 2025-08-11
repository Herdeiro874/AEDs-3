import java.io.RandomAccessFile;


//Caminhos = 4


public class IntercalacaoBalanceada {
    
    
    static int MAX_SIZE = 2;
    static int MAX_ID = 99999;


    // Distribui os registros pelos arquivos e os ordena
    public void distribui(){

        try {
            
            RandomAccessFile arq = new RandomAccessFile("jogos/jogos.db", "rw");
            RandomAccessFile arqtmp[] = new RandomAccessFile [4];

            for(int i = 0; i<4; i++){
                arqtmp[i] = new RandomAccessFile("jogos/temp/" + i + ".db", "rw");
            }
            
            arq.seek(4);   // Pula ID do maior                                      
            int i = 0, y = 0;
            int size;   
            byte[] ba;
            Jogos[]arrayJogos = new Jogos[MAX_SIZE];
            for(int j = 0; j<MAX_SIZE;j++)arrayJogos[j] = new Jogos();
            

            while(!fim(arq)){                           

                // Distribui arquivo principal pelos temporários
                while(!fim(arq) &&  i < MAX_SIZE){              
                    size = arq.readInt();
                    ba = new byte[size];
                    arq.read(ba);
                    arrayJogos[i].fromByteArray(ba);

                    if(!arrayJogos[i].getLapide())i++;
                    
                }
                quicksort(arrayJogos, 0, arrayJogos.length - 1);
                
                // Escreve registros em binário nos aquivos temporários
                for(i = 0; i < arrayJogos.length; i++){                
                    if(!arrayJogos[i].getLapide()){
                        ba = arrayJogos[i].toByteArray();
                        arqtmp[y].writeInt(ba.length);
                        arqtmp[y].write(ba);
                    }
                }

                for(i = 0; i < arrayJogos.length; i++){                
                    arrayJogos[i] = new Jogos();
                }
                
                y = (y+1)%4;
                i=0;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        

    }

    // Faz o swap do quicksort
    public void swap(Jogos[] array, int i, int j){
        Jogos aux;
        aux = array[i];
        array[i] = array[j];
        array[j] = aux;      
    }

    // Ordena o array de jogos
    public void quicksort(Jogos[] array, int esq, int dir) {
        int i = esq, j = dir;
        int pivo = array[(dir+esq)/2].getId();
        while (i <= j) {
            while (array[i].getId() < pivo) i++;
            while (array[j].getId() > pivo) j--;
            if (i <= j) {
                swap(array, i, j);
                i++;
                j--;
            }
        }
        if (esq < j)  quicksort(array,esq, j);
        if (i < dir)  quicksort(array, i, dir);
    }



    public int iniIntercala(){ //incia a intercalação com tamanho fixo e checa a quantidade de arquivos
        try {
        RandomAccessFile arq = new RandomAccessFile("jogos/temp/1.db", "rw");
        if(nulo(arq))return 0;           
        } catch (Exception e) {}
        return intercala(0, MAX_SIZE);
    }

    public int iniIntercalaVar(){ //incia a intercalação com tamanho variável e checa a quantidade de arquivos
        try {
        RandomAccessFile arq = new RandomAccessFile("jogos/temp/1.db", "rw");
        if(nulo(arq))return 0;           
        } catch (Exception e) {}
        return intercalaVariavel(0);
    }



    //Intercalação com tamanho fixo
    public int intercala(int index, int tam_seg){
        
        try {
            
            RandomAccessFile arq = new RandomAccessFile("jogos/jogos.db", "rw");
            RandomAccessFile[] arqtmp = new RandomAccessFile [4];
            RandomAccessFile[] arqtmpE = new RandomAccessFile [4];

            Jogos arrayJogos[] = new Jogos[4];
            for(int j = 0; j<4;j++)arrayJogos[j] = new Jogos();

            int file = 0;
            int size;
            int a0, a1, a2, a3;
            a0 = a1 = a2 = a3 = 0;

            // Carrega arquivos de leitura
            for(int i=0; i<4; i++){
                arqtmp[i] = new RandomAccessFile("jogos/temp/" + index + ".db", "rw");  
                index = (index+1)%8;
            }

            // Carrega arquivos de escrita
            for(int i=0; i<4; i++){
                arqtmpE[i] = new RandomAccessFile("jogos/temp/" + index + ".db", "rw");  
                index = (index+1)%8;
            }
            

            // Intercalação recursiva
            while(!fim(arqtmp[0]) || !fim(arqtmp[1]) || !fim(arqtmp[2]) || !fim(arqtmp[3])){
                
                // Carrega o primeiro registro de cada um dos arquivos de leitura
                for(int i=0; i<4; i++){
                    arrayJogos[i] = carregaDados(arqtmp[i], arrayJogos[i]);
                }

                while(a0<tam_seg || a1<tam_seg || a2<tam_seg || a3<tam_seg){
               
                    if(arrayJogos[0].getId()<arrayJogos[1].getId() && arrayJogos[0].getId()<arrayJogos[2].getId() && arrayJogos[0].getId()<arrayJogos[3].getId() && a0<tam_seg){
                            size = arrayJogos[0].toByteArray().length;
                            byte[] b0 = new byte[size];
                            b0 = arrayJogos[0].toByteArray();
                            arqtmpE[file].writeInt(size);
                            arqtmpE[file].write(b0);
                            a0++;
                            if(fim(arqtmp[0]))arrayJogos[0].setId(MAX_ID);
                            if(arrayJogos[0].getId() != MAX_ID)carregaComLim(arqtmp[0], arrayJogos, 0, a0, tam_seg);

                    }else   
                            if(arrayJogos[1].getId()<arrayJogos[0].getId() && arrayJogos[1].getId()<arrayJogos[2].getId() && arrayJogos[1].getId() < arrayJogos[3].getId() && a1<tam_seg){
                                size = arrayJogos[1].toByteArray().length;
                                byte[] b0 = new byte[size];
                                b0 = arrayJogos[1].toByteArray();
                                arqtmpE[file].writeInt(size);
                                arqtmpE[file].write(b0);
                                a1++;
                                if(fim(arqtmp[1]))arrayJogos[1].setId(MAX_ID);
                                if(arrayJogos[1].getId() != MAX_ID)carregaComLim(arqtmp[1], arrayJogos, 1, a1, tam_seg);

                        }else   
                                if(arrayJogos[2].getId()<arrayJogos[0].getId() && arrayJogos[2].getId()<arrayJogos[1].getId() && arrayJogos[2].getId()<arrayJogos[3].getId() && a2<tam_seg){
                                    size = arrayJogos[2].toByteArray().length;
                                    byte[] b0 = new byte[size];
                                    b0 = arrayJogos[2].toByteArray();
                                    arqtmpE[file].writeInt(size);
                                    arqtmpE[file].write(b0);
                                    a2++;
                                    if(fim(arqtmp[2]))arrayJogos[2].setId(MAX_ID);
                                    if(arrayJogos[2].getId() != MAX_ID)carregaComLim(arqtmp[2], arrayJogos, 2, a2, tam_seg);

                        }else   
                                    if(a3<tam_seg){
                                        size = arrayJogos[3].toByteArray().length;
                                        byte[] b0 = new byte[size];
                                        b0 = arrayJogos[3].toByteArray();
                                        arqtmpE[file].writeInt(size);
                                        arqtmpE[file].write(b0);
                                        a3++;
                                        if(fim(arqtmp[3]))arrayJogos[3].setId(MAX_ID);
                                        if(arrayJogos[3].getId() != MAX_ID)carregaComLim(arqtmp[3], arrayJogos, 3, a3, tam_seg);
                                }

                    if(arrayJogos[0].getId() == MAX_ID)a0 = tam_seg;
                    if(arrayJogos[1].getId() == MAX_ID)a1 = tam_seg;
                    if(arrayJogos[2].getId() == MAX_ID)a2 = tam_seg;
                    if(arrayJogos[3].getId() == MAX_ID)a3 = tam_seg;

                }
                file = (file+1)%4;        
                a0 = a1 = a2 = a3 = 0;       
            }
            
            // Chama a recursão caso o arquivo de escrita não
            // tenha o mesmo tamanho que o arquivo não ordenado

            int aux = index;
            for(int i = 0; i<4; i++){
                arqtmp[i] = new RandomAccessFile("jogos/temp/" + aux + ".db", "rw");
                aux = (aux+1)%8;
                arqtmp[i].setLength(0);
            }   

            index = (index+4)%8;
            if(!nulo(arqtmpE[1]) || !nulo(arqtmpE[2]) || !nulo(arqtmpE[3]))index = intercala(index, tam_seg*4);
            

            

                       
        } catch (Exception e) {
            System.out.println("erro" + e.getMessage());
        }
        return index;
        
    }

    //Intercalação de tamanho variável
    public int intercalaVariavel(int index){
        

        try {
            
            RandomAccessFile arq = new RandomAccessFile("jogos/jogos.db", "rw");
            RandomAccessFile[] arqtmp = new RandomAccessFile [4];
            RandomAccessFile[] arqtmpE = new RandomAccessFile [4];


            Jogos arrayJogos[] = new Jogos[4];
            for(int j = 0; j<4;j++)arrayJogos[j] = new Jogos();

            int file = 0;
            int size;
            boolean a0, a1, a2, a3;
            a0 = a1 = a2 = a3 = true;

            // Carrega arquivos de leitura
            for(int i = 0; i<4; i++){
                arqtmp[i] = new RandomAccessFile("jogos/temp/" + index + ".db", "rw");  
                index = (index+1)%8;
            }

            // Carrega arquivos de escrita
            for(int i = 0; i<4; i++){
                arqtmpE[i] = new RandomAccessFile("jogos/temp/" + index + ".db", "rw");  
                index = (index+1)%8;
            }
            

            // Intercalação recursiva
            while(!fim(arqtmp[0]) || !fim(arqtmp[1]) || !fim(arqtmp[2]) || !fim(arqtmp[3])){

            // Carrega o primeiro registro de cada um dos arquivos de leitura
            for(int i = 0; i<4; i++){
                arrayJogos[i] = carregaDados(arqtmp[i], arrayJogos[i]);
            }

                while(a0 || a1 || a2 || a3){
               
                    if(arrayJogos[0].getId() < arrayJogos[1].getId() && arrayJogos[0].getId() < arrayJogos[2].getId() && arrayJogos[0].getId() < arrayJogos[3].getId() && a0){
                            size = arrayJogos[0].toByteArray().length;
                            byte[] b0 = new byte[size];
                            b0 = arrayJogos[0].toByteArray();
                            arqtmpE[file].writeInt(size);
                            arqtmpE[file].write(b0);
                            if(fim(arqtmp[0]))arrayJogos[0].setId(MAX_ID);
                            if(arrayJogos[0].getId() != MAX_ID) a0 = carregaSemLim(arqtmp[0], arrayJogos, 0);

                    }else   
                            if(arrayJogos[1].getId() < arrayJogos[0].getId() && arrayJogos[1].getId() < arrayJogos[2].getId() && arrayJogos[1].getId() < arrayJogos[3].getId() && a1){
                                size = arrayJogos[1].toByteArray().length;
                                byte[] b0 = new byte[size];
                                b0 = arrayJogos[1].toByteArray();
                                arqtmpE[file].writeInt(size);
                                arqtmpE[file].write(b0);
                                if(fim(arqtmp[1]))arrayJogos[1].setId(MAX_ID);
                                if(arrayJogos[1].getId() != MAX_ID) a1 = carregaSemLim(arqtmp[1], arrayJogos, 1);

                        }else   
                                if(arrayJogos[2].getId() < arrayJogos[0].getId() && arrayJogos[2].getId() < arrayJogos[1].getId() && arrayJogos[2].getId() < arrayJogos[3].getId() && a2){
                                    size = arrayJogos[2].toByteArray().length;
                                    byte[] b0 = new byte[size];
                                    b0 = arrayJogos[2].toByteArray();
                                    arqtmpE[file].writeInt(size);
                                    arqtmpE[file].write(b0);
                                    if(fim(arqtmp[2]))arrayJogos[2].setId(MAX_ID);
                                    if(arrayJogos[2].getId() != MAX_ID) a2 = carregaSemLim(arqtmp[2], arrayJogos, 2);

                        }else   
                                    if(a3){
                                        size = arrayJogos[3].toByteArray().length;
                                        byte[] b0 = new byte[size];
                                        b0 = arrayJogos[3].toByteArray();
                                        arqtmpE[file].writeInt(size);
                                        arqtmpE[file].write(b0);
                                        if(fim(arqtmp[3]))arrayJogos[3].setId(MAX_ID);
                                        if(arrayJogos[3].getId() != MAX_ID) a3 = carregaSemLim(arqtmp[3], arrayJogos, 3);
                                }

                    if(arrayJogos[0].getId() == MAX_ID) a0 = false;
                    if(arrayJogos[1].getId() == MAX_ID) a1 = false;
                    if(arrayJogos[2].getId() == MAX_ID) a2 = false;
                    if(arrayJogos[3].getId() == MAX_ID) a3 = false;

                }
                file = (file + 1) % 4;        
                a0 = a1 = a2 = a3 = true;       
            }
            
            // Chama a recursão caso o arquivo de escrita não
            // tenha o mesmo tamanho que o arquivo não ordenado

            int aux = index;
            for(int i = 0; i<4; i++){
                arqtmp[i] = new RandomAccessFile("jogos/temp/" + aux + ".db", "rw");
                aux = (aux+1)%8;
                arqtmp[i].setLength(0);
            }   

            index = (index+4)%8;
            if(!nulo(arqtmpE[1]) || !nulo(arqtmpE[2]) || !nulo(arqtmpE[3]))index = intercalaVariavel(index);
            

            

                       
        } catch (Exception e) {
            System.out.println("erro" + e.getMessage());
        }
        return index;
        
    }


    
    // Checa se é fim do arquivo
    public boolean fim(RandomAccessFile arq)throws Exception{
        return arq.getFilePointer() >= arq.length();

    }
    
    // Checa se arquivo é vazio 
    public boolean nulo(RandomAccessFile arq)throws Exception{
        return 0 >= arq.length();

    }




    //Carrega Dados para o Array de Jogos
    public Jogos carregaDados(RandomAccessFile arqtmp, Jogos arrayJogos)throws Exception{
        if(!fim(arqtmp)){ 
            int size = arqtmp.readInt();
            byte ba[] = new byte[size];
            arqtmp.read(ba);
            arrayJogos.fromByteArray(ba);
        }else{ arrayJogos = new Jogos();
            arrayJogos.setId(MAX_ID);
                
        };
        

        return arrayJogos;
    }



    //Carrega Dados para intercalação com tamanho fixo
    public void carregaComLim(RandomAccessFile arqtmp, Jogos arrayJogos[], int i, int a, int tam_seg)throws Exception{
        
        if(!fim(arqtmp)){ 
            Jogos aux = new Jogos();
            long ponteiro = arqtmp.getFilePointer();            
            int size = arqtmp.readInt();
            byte ba[] = new byte[size];
            arqtmp.read(ba);
            aux.fromByteArray(ba);

            if(aux.getId() > arrayJogos[i].getId() && a<tam_seg) arrayJogos[i] = aux;
            else{
                arqtmp.seek(ponteiro);
                arrayJogos[i].setId(MAX_ID);
            }
        }else{ 
            arrayJogos[i] = new Jogos();
            arrayJogos[i].setId(MAX_ID);
            }
    }



    //Carrega Dados para intercalação sem tamanho fixo
    public Boolean carregaSemLim(RandomAccessFile arqtmp, Jogos arrayJogos[], int i)throws Exception{
        
        if(!fim(arqtmp)){ 
            Jogos aux = new Jogos();
            long ponteiro = arqtmp.getFilePointer();            
            int size = arqtmp.readInt();
            byte ba[] = new byte[size];
            arqtmp.read(ba);
            aux.fromByteArray(ba);

            if(aux.getId() > arrayJogos[i].getId()) arrayJogos[i] = aux;
            else{
                arqtmp.seek(ponteiro);
                arrayJogos[i].setId(MAX_ID);
                return false;
            }
        }else{ 
            arrayJogos[i] = new Jogos();
            arrayJogos[i].setId(MAX_ID);
            };
        
        return true;
    }


  






    // Chama funções necessárias para intercalação
    public void exSort(CRUD<Jogos> c){       
       
        try {
            RandomAccessFile[] arqtmp = new RandomAccessFile [8];
            RandomAccessFile arq = new RandomAccessFile("jogos/jogos.db", "rw");
            RandomAccessFile limpa[] = new RandomAccessFile[3];
            limpa[0] = new RandomAccessFile("jogos/hash.db", "rw");
            limpa[1] = new RandomAccessFile("jogos/arvB.db", "rw");
            limpa[2] = new RandomAccessFile("jogos/temp/htemp.db", "rw");
            byte[] ba;
            int size = 0;


            // Apaga sujeiras dos Temps
            for(int i = 0; i<8; i++){
                arqtmp[i] = new RandomAccessFile("jogos/temp/" + i + ".db", "rw");
                arqtmp[i].setLength(0);
            }   

            // Pula bits de "Maior ID" para distribuir
            arq.seek(4);  
            distribui();     

            // Intercalação balanceada
            int index = iniIntercala();

            // Grava o registro ordenado de volta no arquivo
            arq.seek(0);
            int id = arq.readInt();
            arq.setLength(0);
            arq.seek(0);
            arq.writeInt(id);

            // Reinicializa arvore e hash
            for(int i = 0; i<3; i++)limpa[i].setLength(0); 
            c.arvB = new ArvoreBMais(8, "jogos/arvB.db");
            c.he = new HashExtensivel(8, "jogos/hash.db", "jogos/temp/htemp.db" );

            Jogos jogo = new Jogos();
            while(!fim(arqtmp[index])){
                size = arqtmp[index].readInt();
                ba = new byte[size];
                arqtmp[index].read(ba);
                jogo.fromByteArray(ba);
                
                c.he.create(jogo.getId(), (int)arq.getFilePointer());
                c.arvB.create(jogo.getId(), (int)arq.getFilePointer());

                arq.writeInt(ba.length);
                arq.write(ba);
            }
            c.fim = arq.length();
            // Limpa os Temps
            for(int i = 0; i<8; i++){
                arqtmp[i] = new RandomAccessFile("jogos/temp/" + i + ".db", "rw");
                arqtmp[i].setLength(0);
            }   

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    
    }
    
}
