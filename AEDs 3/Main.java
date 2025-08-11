import java.time.LocalDate;
import java.time.LocalTime;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.util.Scanner;
import java.time.Duration;

public class Main {

    public static void main(String[] args) {
       
        Scanner scan = new Scanner(System.in);
        
        double[] sizeMainArq = new double[5];
        int vers = 1;
        int cont = 0;
        RandomAccessFile arq;      
        byte[] ba;
        int id = 0;
        String line;
        float xx, xz;
        int xw;
        LocalDate ld;
    

        System.out.println("\n");
        System.out.println("Inicializar Database? (y/n)");




        //inicializa a base de dados utilizando o .csv
        if(scan.nextLine().toLowerCase().equals("y"))
        {

            try{           

                RandomAccessFile[] limpa = new RandomAccessFile[3];
                limpa[0] = new RandomAccessFile("jogos/arvB.db", "rw");
                limpa[1] = new RandomAccessFile("jogos/hash.db", "rw");
                limpa[2] = new RandomAccessFile("jogos/jogos.db", "rw");
                for(int i=0;i<3;i++){
                    limpa[i].setLength(0);
                }
                CRUD<Jogos> c = new CRUD<>(Jogos.class.getConstructor(), "jogos/jogos.db");
                BufferedReader buff = new BufferedReader(new FileReader("jogos/VGS.csv"));
                line = buff.readLine();


                while ((line = buff.readLine()) != null) {

                    String[] array = line.split(">");  //separa registro do .csv em um array                            
                    String[] dev = array[14].split(",");  //cria o array de developers

                    if(dev[0].equals("NaN"))dev[0] = "Null";
                    

                    if(array[10].equals("NaN"))xx = 0;
                    else xx = Float.parseFloat(array[10]);


                    if(array[2].equals("NaN"))ld = LocalDate.of(1, 1, 1);
                    else{ 
                        xz = Float.parseFloat(array[2]);
                        xw = (int)xz;
                        ld = LocalDate.of(xw, 1, 1);  //tipo data com dia e mês setado em 1
                    }

                    Jogos j1 = new Jogos(id, array[0], xx, array[1], ld, dev);
                
                    int idJogo = c.create(j1);
                    //System.out.println(c.readA(idJogo));

                }

                
                buff.close(); 


                
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //Interface do programa
        try{  

            CRUD<Jogos> c = new CRUD<>(Jogos.class.getConstructor(), "jogos/jogos.db");
            boolean erro = true;
            arq = new RandomAccessFile("jogos/jogos.db", "rw");

            while(erro){
                
                System.out.println("\nCreate: c\nRead: r\nUpdate: u\nDelete: d\nOrdenar: o\nBoyerMoore: b\nCompressão: h\nDecompressão: l\nSair: s");
                String opt = scan.next();
                opt = opt.toLowerCase();
                char tolowChar = opt.charAt(0);

                switch (tolowChar) {

                    case 'c': //CREATE

                        scan.reset();

                        arq.seek(0);
                        int idCreate = arq.readInt()+1;

                        scan.nextLine();

                        System.out.print("\nDigite Nome: ");
                        String nomeCreate = scan.nextLine();

                        System.out.print("\nDigite Nota: ");
                        float notaCreate = scan.nextFloat();

                        scan.nextLine();

                        System.out.print("\nDigite Plataforma: ");
                        String nomePlat = scan.nextLine();

                        System.out.print("\nDigite Ano de Lancamento: ");
                        int anoLanca = scan.nextInt();

                        scan.nextLine();

                        System.out.print("\nDigite Developers (\"aaaa,bbbb,cccc,dddd...\"): ");
                        String zzz = scan.nextLine();

                        String[] j = zzz.split(",");

                        
                        Jogos jo1 = new Jogos(idCreate, nomeCreate, notaCreate, nomePlat, LocalDate.of(anoLanca, 1, 1), j);
                        int idJogo = c.create(jo1);

                        Jogos jogoCreate = c.readA(idJogo);
                        if(jogoCreate!=null)jogoCreate.printJogo();
                        else System.out.println("null");
                        
                        break;
                    
                    case 'r': //READ

                        scan.reset();

                        System.out.print("\nDigite ID para leitura: ");
                        int idrJogo = scan.nextInt();
                        Jogos jogoler = c.readA(idrJogo);
                        if(jogoler!=null)jogoler.printJogo();
                        else System.out.println("null");
                        
                        break;
                    
                    case 'u': //UPDATE

                        scan.reset();

                        System.out.print("\nDigite ID a ser atualizado: ");
                        int idUpdate = scan.nextInt();

                        scan.nextLine();

                        System.out.print("\nDigite Nome: ");
                        String nomeUpdate = scan.nextLine();

                        System.out.print("\nDigite Nota: ");
                        float notaUpdate = scan.nextFloat();

                        scan.nextLine();

                        System.out.print("\nDigite Plataforma: ");
                        String nomePlatUpdate = scan.nextLine();

                        System.out.print("\nDigite Ano de Lancamento: ");
                        int anoLancaUpdate = scan.nextInt();

                        scan.nextLine();

                        System.out.print("\nDigite Developers (\"aaaa,bbbb,cccc,dddd...\"): ");
                        String zzu = scan.nextLine();
                        String[] ju = zzu.split(",");
                        
                        Jogos j2 = new Jogos(idUpdate, nomeUpdate, notaUpdate, nomePlatUpdate, LocalDate.of(anoLancaUpdate, 1, 1), ju);
                        c.update(j2);

                        Jogos jogoUp = c.readA(idUpdate);

                        if(jogoUp!=null)jogoUp.printJogo();
                        else System.out.println("null");
                        
                        break;
                        
                    case 'd': //DELETE

                        scan.reset();

                        System.out.print("\nDigite ID do Registro a ser Deleteado: ");                   
                        
                        int idDelete = scan.nextInt();
                        c.delete(idDelete);
                        Jogos jogoDelete = c.readA(idDelete);

                        if(jogoDelete==null)
                            System.out.println("\nJogo excluído");
                        else
                            System.out.println("\nFalha na Exclusão\n" + c.readA(idDelete));

                        break;

                    case 'o': //ORDENAR REGISTROS
                    
                        IntercalacaoBalanceada interc = new IntercalacaoBalanceada();
                        interc.exSort(c);

                        break;   
                    
                    case 'h': //HUFFMAN E LZW

                        LocalTime huff0 = LocalTime.now();
                        HuffmanCompression.huffmanCompression("jogos/jogos.db", vers);      
                        LocalTime Fhuff = LocalTime.now();

                        LocalTime lzw0 = LocalTime.now();
                        LZWCompression.lzwComprecao("jogos/jogos.db", vers);
                        LocalTime fLzw = LocalTime.now();

                        Duration huff = Duration.between(huff0, Fhuff);
                        Duration lzw = Duration.between(lzw0, fLzw);
            
                        System.out.println("\nTempo Huffman: " + huff);
                        System.out.println("Tempo LZW: " + lzw + "\n");

                        CRUD.calcularFormulas("jogos/jogos.db", "jogos/comp/arquivoHuffmanCompressao" + vers + ".db", "jogos/comp/arquivoLZWCompressao" + vers + ".db");

                        RandomAccessFile arqMain = new RandomAccessFile("jogos/jogos.db", "rw");
                        sizeMainArq[cont++] = arqMain.length();
                        arqMain.close();

                        vers++;
                        
                        break;

                    case 'l'://DECOMPRESSÃO

                        System.out.println("Versões: " + (vers-1));
                        System.out.println("Escolha versão: ");
                        int code = scan.nextInt();

                        if(code <= vers && code != 0)
                        {
                            huff0 = LocalTime.now();
                            HuffmanDecompression.huffdescomprecao("jogos/comp/arquivoHuffmanCompressao" + code + ".db", code);
                            Fhuff = LocalTime.now();

                            lzw0 = LocalTime.now();
                            LZWDecompression.lzwdescomprecao("jogos/comp/arquivoLZWCompressao" + code + ".db", code);
                            fLzw = LocalTime.now();

                            huff = Duration.between(huff0, Fhuff);
                            lzw = Duration.between(lzw0, fLzw);

                            System.out.println("\nTempo Huffman: " + huff);
                            System.out.println("Tempo LZW: " + lzw + "\n");

                            RandomAccessFile arqCHuff = new RandomAccessFile("jogos/comp/arquivoHuffman" + code + ".db", "rw");
                            RandomAccessFile arqCLZW = new RandomAccessFile("jogos/comp/arquivoLZW" + code + ".db", "rw");
                            double sizeHuff = arqCHuff.length();
                            double sizeLZW = arqCLZW.length();
                            for(int i = 0; i < cont; i++){
                                System.out.println("Tam original " + (i+1) + ": " + sizeMainArq[i] + "B");
                            }
                            System.out.println("Tam descompactado Huffman: " + sizeHuff + " B");
                            System.out.println("Tam descompactado LZW: " + sizeLZW + " B");

                            arqCHuff.close();
                            arqCLZW.close();
                        }

                    break;
                        
                    case 'b'://BOYER MOORE

                        BoyerMoore bm = new BoyerMoore();
            
                        System.out.println("Escreva o Padrão");
                        String padrao = scan.next();

                        LocalTime beforeBm = LocalTime.now();
                        bm.iniBoyer("jogos/jogos.db", padrao);
                        LocalTime afterBm = LocalTime.now();

                        Duration dBm = Duration.between(beforeBm, afterBm);
                        System.out.println("\nTempo Boyer Moore: " + dBm);
                        bm.results();

                    break;

                    case 's': //SAIR DO PROGRAMA
                    
                        arq.close();
                        scan.close();
                        erro = false;

                        break;
                                           
                    default: System.out.println("\nValor Inválido");
                        break;
                }                 

            }


        }catch(Exception e){
            e.printStackTrace();
        }
        

        scan.close();
    }

}
