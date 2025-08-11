import java.lang.reflect.Constructor;
import java.io.IOException;
import java.io.RandomAccessFile;


class CRUD <T extends Registro>{

    public Constructor<T> constructor;
    public long inicio;
    public long fim; 
    public String nome;
    public RandomAccessFile arq;
    public HashExtensivel he;
    public ArvoreBMais arvB;
    


    // Construtor
    public CRUD(Constructor<T> constructor, String nome)throws Exception{

        this.inicio = 0;
        this.constructor = constructor;
        this.nome = nome;
        
        he = new HashExtensivel(8, "jogos/hash.db", "jogos/temp/htemp.db" );
        arvB = new ArvoreBMais(8, "jogos/arvB.db");
        arq = new RandomAccessFile(this.nome, "rw");

        if(arq.length() == 0)
        {
            arq.writeInt(-1);
        }

        this.fim = arq.length();       
    }



    //Create
    public int create(T registro)throws Exception{
        

        arq.seek(this.inicio);

        int id = arq.readInt() + 1;
        registro.setId(id);
        
        arq.seek(this.inicio);
        arq.writeInt(id);

        arq.seek(this.fim);

        this.arvB.create(id, (int)arq.getFilePointer());
        this.he.create(id, (int)arq.getFilePointer());

        byte[] ba = registro.toByteArray();
        arq.writeInt(ba.length);
        arq.write(ba);

        this.fim = arq.length(); 

        return id;
    }


    

    public T readH(int id)throws Exception{      // Método read utilizando Hash
        T resp = null; 
        long ponteiro = he.read(id);
        if(ponteiro > 0){
            arq.seek(ponteiro);

            int size = arq.readInt();

            if(!arq.readBoolean()){
                arq.seek(ponteiro+4);
                resp = constructor.newInstance();
                byte[] ba = new byte[size];
                arq.read(ba);
                resp.fromByteArray(ba);
            }
        }

        return resp;
    }

    public T readA(int id)throws Exception{    // Método read utilizando ÁrvoreB
        T resp = null; 
        long ponteiro = arvB.read(id);


        if(ponteiro > 0){
            arq.seek(ponteiro);

            int size = arq.readInt();

            if(!arq.readBoolean()){
                arq.seek(ponteiro+4);
                resp = constructor.newInstance();
                byte[] ba = new byte[size];
                arq.read(ba);
                resp.fromByteArray(ba);
            }
        }
        return resp;
    }   




    public boolean update(T alterado)throws Exception{

        boolean resp = false;
        int id = alterado.getId();
        T aux = constructor.newInstance();
        long ponteiro = he.read(id);
     
               
        if(ponteiro > 0){
            arq.seek(ponteiro);

            int size = arq.readInt();

            if(!arq.readBoolean()){

                arq.seek(ponteiro + 4);
                byte[] ba = new byte[size];
                arq.read(ba);
                aux.fromByteArray(ba);

                byte[] novo = alterado.toByteArray();

                if(novo.length > size){


                    arq.seek(ponteiro+4);
                    arq.writeBoolean(true);                   // Deletar arquivo                    
                    arq.seek(this.fim);                         // Mover para fim do arquivo

                    he.update(id, arq.getFilePointer());        // Atualiza registros dos Índices
                    arvB.update(id, (int)arq.getFilePointer());

                    arq.writeInt(novo.length);                  // Escrever tamanho do registro
                    arq.write(novo);
                    this.fim = arq.length(); 
                    resp = true;
                }else{
                    arq.seek(ponteiro+4);
                    arq.write(novo);
                    resp = true;
                }
            }

        }   

     return resp;
    }

    public boolean delete(int id)throws Exception{

        boolean resp = false; 
        arq.seek(this.inicio);     
        long ponteiro = he.read(id);

        if(ponteiro > -1){
            arq.seek(ponteiro);


            if(!arq.readBoolean()){
                arq.seek(ponteiro + 5);
                he.delete(arq.readInt());
                arq.seek(ponteiro + 5);
                arvB.delete(arq.readInt());

                arq.seek(ponteiro + 4);
                arq.writeBoolean(true); //Marca arquivo como inválido
                resp = true;
            }

        }
   

        return resp;
    }



    public static void calcularFormulas(String base, String compHuff, String compLZ) throws IOException{

		RandomAccessFile arq = new RandomAccessFile(base, "rw");
		RandomAccessFile arqH = new RandomAccessFile(compHuff, "rw");
        RandomAccessFile arqL = new RandomAccessFile(compLZ, "rw");

		double sizeMain = arq.length();
		double sizeHuff = arqH.length();
        double sizeLZW = arqL.length();

		System.out.println("Tam original: "+ sizeMain + " B");
		System.out.println("Tam Huffman: "+ sizeHuff + " B");
        System.out.println("Tam LZW: "+ sizeLZW + " B\n");

		double  divHuff = sizeHuff/sizeMain;
        double divLZW = sizeLZW / sizeMain;

		double prHuff = 100 * (1 - divHuff);
        double prLZW = 100 * (1 - divLZW);

		System.out.println("Taxa compressão Huffman: " + divHuff);
        System.out.println("Taxa compressão LZW: " + divLZW);
        System.out.println("Fator compressão Huffman: " + prHuff);
		System.out.println("Fator compressão LZW: " + prLZW + " \n");

		divHuff = (double) sizeMain/sizeHuff;
        divLZW = (double) sizeMain/sizeLZW;

		double gcHuff = 100 * log(divHuff); // / / / //
        double gcLZW = 100 * log(divLZW);
        
		System.out.println("Ganho de compressão Huffman: " + gcHuff);
        System.out.println("Ganho de compressão LZW: " + gcLZW + "\n");

		arq.close();
		arqH.close();
        arqL.close();
        
	}

    

    public static double log(double valor) {
        return Math.log(valor) / Math.log(2.71828182846);
    }
    
}




/* 
//Read não indexado

    public T read(int id)throws Exception{

        T resp = null; 
        
        arq = new RandomAccessFile(this.nome, "rw");

        //System.out.println("inicio: " + this.inicio);

        arq.seek(this.inicio);     
        int lastID = arq.readInt();
        boolean achou = false;
        long ponteiro = arq.getFilePointer();

        //System.out.println("lastID: " + lastID);

        if(lastID >= id)
        {
            resp = this.constructor.newInstance();
            byte[] ba;
            Boolean valido;

            while(!achou && (arq.getFilePointer() < this.fim))
            {
                
                //System.out.println("tamanho: " + arq.readInt());

                ba = new byte[arq.readInt()];

                ponteiro = arq.getFilePointer();

                valido = arq.readBoolean();   

                arq.seek(ponteiro);

                arq.read(ba);
              
                //ponteiro = arq.getFilePointer();

                if(!valido)
                {
                    resp.fromByteArray(ba);
                    achou = (resp.getId() == id);
                }

            }

            if(!achou)resp = null;
  
        }
        return resp;
    }
*/
/*
    //Update não indexado
    public boolean update(T updated)throws Exception{

        boolean resp = false; 
        arq = new RandomAccessFile(this.nome, "rw");
        arq.seek(this.inicio);
        
        //System.out.println("aqui: "+updated);

        int lastID = arq.readInt();
        boolean achou = false;
        int id = updated.getId();
        long ponteiro = 0;


        if(lastID >= id)
        {

            T aux = this.constructor.newInstance();
            byte[] ba;
            Boolean valido;
            int tam = 0;


            while((arq.getFilePointer() < this.fim) && !achou)
            {

                tam = arq.readInt();
                ponteiro = arq.getFilePointer();
                valido = arq.readBoolean();    

                arq.seek(ponteiro);
                         
                ba = new byte[tam];
                arq.read(ba);

                if(!valido)
                {

                    aux.fromByteArray(ba);
                    achou = (aux.getId() == id);

                }
                
            }


            if(achou){

                byte[] novo = updated.toByteArray();

                if(novo.length > tam)
                {

                    //delete
                    arq.seek(ponteiro);
                    arq.writeBoolean(true);                
                    arq.seek(this.fim);  


                    arq.writeInt(novo.length);
                    arq.write(novo);

                    this.fim = arq.length(); 
                    
                    resp = true;
                    
                }else{
                    arq.seek(ponteiro);
                    arq.write(novo);
                    resp = true;
                }
            }
        }   

        return resp;
    }
*/    
/*
    //Delete não indexado
    public boolean delete(int id)throws Exception{

        boolean resp = false; 

        arq = new RandomAccessFile(this.nome, "rw");
        arq.seek(this.inicio);             
        int lastID = arq.readInt();
        boolean achou = false;
        long ponteiro = 0;


        if(lastID >= id)
        {

            T aux = this.constructor.newInstance();
            byte[] ba;
            Boolean valido;
            int tam = 0;

            while(!achou && (arq.getFilePointer() < this.fim))
            {
                
                tam = arq.readInt();
                ponteiro = arq.getFilePointer();
                valido = arq.readBoolean();               
                ba = new byte[tam];
                arq.seek(ponteiro);
                arq.read(ba);

                if(!valido)
                {
                    aux.fromByteArray(ba);
                    achou = (aux.getId() == id);
                }
            }

            if(achou)
            {
                arq.seek(ponteiro);
                arq.writeBoolean(true);//Deletar arquivo
                resp = true;
            }
        }   

        return resp;
    }
*/