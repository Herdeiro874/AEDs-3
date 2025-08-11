import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Jogos implements Registro{
    
    protected Boolean Lapide;
    protected int Id;
    protected String name;
    protected String plataform;
    protected float criticScore;
    protected LocalDate yor;
    protected String[] developer;


    //Construtores
    public Jogos(int id, String n, float i, String d, LocalDate a, String[] t){

        Lapide = false;
        Id = id;
        name = n;
        criticScore = i;
        plataform = d;
        yor = a;
        
        if(t.length==0)
        {
            developer = new String[1];
            developer[0] = " ";
        }else{
                developer = new String[t.length]; 
                for(int j = 0; j < t.length; j++)
                {
                    developer[j] = t[j];
                }
             
            }
    }
    
    public Jogos(){

        Lapide = true;
        Id =  -1;
        name = " ";
        criticScore = 0F;
        plataform = "";
        yor = LocalDate.now();
        developer = new String[0];

    }

    //Devolve uma string com os dados do jogador
    public String toString(){
        DecimalFormat df= new DecimalFormat("#,##0.00");//formata o valor dos pontos

        String x = developer[0];

        for(int j = 1; j < developer.length; j++)
        {
            x += ",";
            x += developer[j];
        }

        return "\nLapide:"+Lapide+
                "\nID:"+Id+
                "\nNome:"+name+
                "\nScore:"+criticScore+
                "\nLancamento:"+yor+
                "\nDevelopers:"+x;
    }


    

    public String getName(){
            return this.name;
    }

    public boolean getLapide(){
            return this.Lapide;
    }


    public String getPlataform(){
            return this.plataform;
    }

    public LocalDate getYor(){
            return this.yor;
    }
    public float getCriticScore(){
            return this.criticScore;
    }
    public String[] getDeveloper(){
            return this.developer;
    }
    public int getId(){
            return this.Id;
    }

    public void setName(String name){
            this.name = name;
    }

    public void setLapide(Boolean Lapide){
            this.Lapide = Lapide;
    }

    public void setId(int Index){
            this.Id = Index;
    }
    
    public void setPlataform(String plataform){
            this.plataform = plataform;
    }
    public void setCriticScore(int criticScore){
            this.criticScore = criticScore;
    }
    public void setYor(LocalDate yor){
            this.yor = yor;
    }


    public void printJogo(){
        
        String[] dev = this.getDeveloper();
        System.out.println(" ");

        System.out.println("ID:" + this.getId());
        System.out.println("Nome:" + this.getName());
        System.out.println("Plataforma:" + this.getPlataform());
        System.out.println("Nota:" + this.getCriticScore());
        System.out.println("Lançamento:" + this.getYor().getYear());

        System.out.print("Developers:" + dev[0]);
        for(int i = 1; i < dev.length; i++){
                System.out.print(",");
                System.out.print(dev[1]);
        }

        System.out.println(" ");
    }

    public byte[] toByteArray() throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        String z;

        z = developer[0];

        dos.writeBoolean(Lapide);
        dos.writeInt(Id);
        dos.writeUTF(name);
        dos.writeFloat(criticScore);

        for(int tamanhoString = plataform.length(); tamanhoString < 20; tamanhoString++)plataform += " ";

        dos.writeUTF(plataform);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu");
        dos.writeInt(Integer.parseInt(yor.format(formatter)));

        for(int k = 1; k < developer.length; k++){

            z += ";";
            z += developer[k];

        }
        dos.writeUTF(z);


        return baos.toByteArray();
    }


    public void fromByteArray(byte[] ba) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        Lapide = dis.readBoolean();
        Id = dis.readInt();
        name = dis.readUTF();
        criticScore = dis.readFloat();
        plataform = dis.readUTF();
        yor = LocalDate.of(dis.readInt(), 1, 1);
        developer = dis.readUTF().split(";");

    }



}