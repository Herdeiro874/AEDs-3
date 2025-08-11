import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class HuffmanDecompression {
	static PriorityQueue<Arvore> pq1 = new PriorityQueue<Arvore>();
	static int[] quantos = new int[300];
	static String[] palavras = new String[300]; 
	static String[] bitesoss = new String[300]; 
	static int nbite;
	static int pp;
	static int can;
	static String bb; 
	static String tempxx; 
	

	static class Arvore implements Comparable<Arvore> {
		Arvore esq;
		Arvore dir;
		public String clt;
		public int bitess;
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

	//inicio decomprecao
	public static void descomprecaoI() {
		int i;
		if (raiz != null) anda(raiz);
		for (i = 0; i < 300; i++) quantos[i] = 0;
		for (i = 0; i < 300; i++) palavras[i] = "";

		pq1.clear();
		bb = "";
		tempxx = "";
		nbite = 0;
		pp = 0;
		can = 0;
	}

	//caminha pela arvore
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
			palavras[now.bitess] = st;
			return;
		}
		if (now.esq != null) nosss(now.esq, st + "0");
		if (now.dir != null) nosss(now.dir, st + "1");
	}

	public static void nossFa() {
		int i;
		can = 0;
		for (i = 0; i < 300; i++) {
			if (quantos[i] != 0) {

				Arvore tempxx = new Arvore();
				tempxx.bitess = i;
				tempxx.frequenciarv = quantos[i];
				tempxx.esq = null;
				tempxx.dir = null;
				pq1.add(tempxx);
				can++;
			}

		}
		Arvore temp1, temp2;

		if (can == 0) {
			return;
		} else if (can == 1) {
			for (i = 0; i < 300; i++)
				if (quantos[i] != 0) {
					palavras[i] = "0";
					break;
				}
			return;
		}

		//Verificando se o arquivo esta vazio
		while (pq1.size() != 1) {
			Arvore tempxx = new Arvore();
			temp1 = pq1.poll();
			temp2 = pq1.poll();
			tempxx.esq = temp1;
			tempxx.dir = temp2;
			tempxx.frequenciarv = temp1.frequenciarv + temp2.frequenciarv;
			pq1.add(tempxx);
		}
		raiz = pq1.poll();
	}

	//realiza o calculo de frequencia
	public static void freqcontando(String archieve) {

		File file = new File(archieve);
		int fey, i;
		Byte byy;
		try {
			FileInputStream fInput = new FileInputStream(file);
			DataInputStream dInput = new DataInputStream(fInput);
			can = dInput.readInt();

			for (i = 0; i < can; i++) {
				byy = dInput.readByte();
				fey = dInput.readInt();
				quantos[estNoss(byy)] = fey;
			}
			dInput.close();
			fInput.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		nossFa();
		if (can > 1)
			nosss(raiz, "");

		for (i = 0; i < 256; i++) {
			if (palavras[i] == null)
				palavras[i] = "";
		}
		file = null;
	}

	//formatacao de valores
	public static int estNoss(Byte byy) {
		int ret = byy;
		if (ret < 0) {
			ret = ~byy;
			ret = ret + 1;
			ret = ret ^ 255;
			ret += 1;
		}
		return ret;
	}

	//direcionamento da arvore
	public static void binarioss() {
		int i, j;
		String s;
		for (i = 0; i < 256; i++) {
			bitesoss[i] = "";
			j = i;
			while (j != 0) {
				if (j % 2 == 1)
					bitesoss[i] += "1";
				else
					bitesoss[i] += "0";
				j /= 2;
			}
			s = "";
			for (j = bitesoss[i].length() - 1; j >= 0; j--) {
				s += bitesoss[i].charAt(j);
			}
			bitesoss[i] = s;
		}
		bitesoss[0] = "0";
	}

	public static void binariossL(String zip, String unz) {
		File umm = null, doiss = null;
		int kl, byy;
		Byte blk;
		int j, i;
		bb = "";
		umm = new File(zip);
		doiss = new File(unz);
		try {
			FileOutputStream fOutput = new FileOutputStream(doiss);
			DataOutputStream dOutput = new DataOutputStream(fOutput);
			FileInputStream fInput = new FileInputStream(umm);
			DataInputStream dInput = new DataInputStream(fInput);
			try {
				can = dInput.readInt();
				for (i = 0; i < can; i++) {
					blk = dInput.readByte();
					j = dInput.readInt();
				}
				nbite = dInput.readInt();
			} catch (EOFException eof) {
			}

			while (true) {
				try {
					blk = dInput.readByte();
					byy = estNoss(blk);
					bb += fstt(bitesoss[byy]);

					while (true) {
						kl = 1;
						tempxx = "";
						for (i = 0; i < bb.length() - nbite; i++) {
							tempxx += bb.charAt(i);
							if (lt() == 1) {
								dOutput.write(pp);
								kl = 0;
								String s = "";
								for (j = tempxx.length(); j < bb.length(); j++) {
									s += bb.charAt(j);
								}
								bb = s;
								break;
							}
						}

						if (kl == 1)
							break;
					}
				} catch (EOFException eof) {
					break;
				}
			}
			fOutput.close();
			dOutput.close();
			fInput.close();
			dInput.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		umm = null;
		doiss = null;
	}

	public static int lt() {
		int i;

		for (i = 0; i < 256; i++) {
			if (palavras[i].compareTo(tempxx) == 0) {
				pp = i;
				return 1;
			}
		}
		return 0;

	}

	public static String fstt(String blk) {
		String ret = "";
		int i;
		int len = blk.length();
		for (i = 0; i < (8 - len); i++)
			ret += "0";
		ret += blk;
		return ret;
	}

	//funcao principal
	public static void huffdescomprecao(String archieve, int version) {
		descomprecaoI();
		freqcontando(archieve);
		binarioss();
		binariossL(archieve, "jogos/comp/arquivoHuffman" + version + ".db");
		descomprecaoI();
	}

}