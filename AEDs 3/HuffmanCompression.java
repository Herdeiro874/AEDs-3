import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.PriorityQueue;

public class HuffmanCompression {

	
    static PriorityQueue<Arvore> pq = new PriorityQueue<Arvore>();
	static int[] quantos = new int[300];
	static String[] palavras = new String[300];
	static int nbite;
	static byte bitess;
	static int contador;  //variavel para contar caracteres distintos

	static class Arvore implements Comparable<Arvore> {
		Arvore esq;
		Arvore dir;
		public String clt;
		public int bitexx;
		public int frequenciarv;

		public int compareTo(Arvore arvore) {
			if (this.frequenciarv < arvore.frequenciarv)
				return -1;
			if (this.frequenciarv > arvore.frequenciarv)
				return 1;
			return 0;
		}
	}

	static Arvore raiz;

	//Comprimindo
	public static void comprecao() {
		int i;
		contador = 0;
		if (raiz != null) anda(raiz);
		for (i = 0; i < 300; i++) quantos[i] = 0;
		for (i = 0; i < 300; i++) palavras[i] = "";
		pq.clear();
	}
	
	//Frequencia dos caracteres
	public static void frequenciaContando(String fname) {
		File arquivoo = null;
		Byte bitess;

		arquivoo = new File(fname);
		try {
			FileInputStream fInput = new FileInputStream(arquivoo);
			DataInputStream dInput = new DataInputStream(fInput);
			while (true) {
				try {

					bitess = dInput.readByte();
					quantos[binariosss(bitess)]++;
				} catch (EOFException eof) {
					break;
				}
			}
			fInput.close();
			dInput.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		arquivoo = null;
	}

	//Binarios
	public static int binariosss(Byte var) {
		int ret = var;
		if (ret < 0) {
			ret = ~var;
			ret = ret + 1;
			ret = ret ^ 255;
			ret += 1;
		}
		return ret;
	}

	public static void anda(Arvore now) {

		if (now.esq == null && now.dir == null) {
			now = null;
			return;
		}
		if (now.esq != null) anda(now.esq);
		if (now.dir != null) anda(now.dir);
	}

	public static void nosss(Arvore now, String st) {
		now.clt = st;
		if ((now.esq == null) && (now.dir == null)) {
			palavras[now.bitexx] = st;
			return;
		}
		if (now.esq != null) nosss(now.esq, st + "0");
		if (now.dir != null) nosss(now.dir, st + "1");
	}

	public static void criaNo() {
		int i;
		pq.clear();

		for (i = 0; i < 300; i++) {
			if (quantos[i] != 0) {
				Arvore temp = new Arvore();
				temp.bitexx = i;
				temp.frequenciarv = quantos[i];
				temp.esq = null;
				temp.dir = null;
				pq.add(temp);
				contador++;
			}

		}
		Arvore temp1, temp2;

		if (contador == 0) {
			return;
		} else if (contador == 1) {
			for (i = 0; i < 300; i++)
				if (quantos[i] != 0) {
					palavras[i] = "0";
					break;
				}
			return;
		}

		//Verificando se o arquivo está vazio
		while (pq.size() != 1) {
			Arvore temp = new Arvore();
			temp1 = pq.poll();
			temp2 = pq.poll();
			temp.esq = temp1;
			temp.dir = temp2;
			temp.frequenciarv = temp1.frequenciarv + temp2.frequenciarv;
			pq.add(temp);
		}
		raiz = pq.poll();
	}

	//Arquivo auxiliar armazenando binarios
	public static void comprecaoinicial(String archieve) {

		File arquivoo, arquivooaux;

		arquivoo = new File(archieve);
		arquivooaux = new File("jogos/temp/arqAux.db");
		try {
			FileInputStream fInput = new FileInputStream(arquivoo);
			DataInputStream dInput = new DataInputStream(fInput);
			PrintStream ps = new PrintStream(arquivooaux);

			while (true) {
				try {
					bitess = dInput.readByte();
					ps.print(palavras[binariosss(bitess)]);
				} catch (EOFException eof) {
					break;
				}
			}

			fInput.close();
			dInput.close();
			ps.close();

		} catch (IOException e) {
			System.out.println(e);
		}
		arquivoo = null;
		arquivooaux = null;

	}

	//Criação do arquivo comprimido
	public static void comprecaofinal(String archieve, String archieveAux) {
		File arquivoo, fileAux;
		int i; //j = 10;
		Byte btt;

		arquivoo = new File(archieve);
		fileAux = new File(archieveAux);

		try {
			FileInputStream fInput = new FileInputStream(arquivoo);
			DataInputStream dInput = new DataInputStream(fInput);
			FileOutputStream fOutput = new FileOutputStream(fileAux);
			DataOutputStream dOutput = new DataOutputStream(fOutput);

			dOutput.writeInt(contador);
			for (i = 0; i < 256; i++) {
				if (quantos[i] != 0) {
					btt = (byte) i;
					dOutput.write(btt);
					dOutput.writeInt(quantos[i]);
				}
			}
			long texbits;
			texbits = arquivoo.length() % 8;
			texbits = (8 - texbits) % 8;
			nbite = (int) texbits;
			dOutput.writeInt(nbite);
			while (true) {
				try {
					bitess = 0;
					byte ch;
					for (nbite = 0; nbite < 8; nbite++) {
						ch = dInput.readByte();
						bitess *= 2;
						if (ch == '1')
							bitess++;
					}
					dOutput.write(bitess);

				} catch (EOFException eof) {
					int x;
					if (nbite != 0) {
						for (x = nbite; x < 8; x++) {
							bitess *= 2;
						}
						dOutput.write(bitess);
					}

					nbite = (int) texbits;
					break;
				}
			}
			dInput.close();
			dOutput.close();
			fInput.close();
			fOutput.close();

		} catch (IOException e) {
			System.out.println(e);
		}
		arquivoo.delete();
		arquivoo = null;
		fileAux = null;
	}

	//funcao principal
	public static void huffmanCompression(String archieve, int version) {
		comprecao();
		frequenciaContando(archieve);
		criaNo();
		if (contador > 1) nosss(raiz, "");
		comprecaoinicial(archieve);
		comprecaofinal("jogos/temp/arqAux.db", "jogos/comp/arquivoHuffmanCompressao" + version + ".db");																						
		comprecao();
	}

}