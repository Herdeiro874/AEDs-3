import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LZWDecompression {
	public static int bsx;
	public static String bttost[] = new String[256];
	public static String cg;

	//inicializa decomprecao
	public static void descomprecaoIn(String archieve, int version) {
		int knts;
		int TamDicionario = 256;
		int mpos = 256;
		String ppt;
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		
		for (int i = 0; i < 256; i++) dictionary.put(i, "" + (char) i);

		File arquivo = null, arquivoAux = null;
		arquivo = new File(archieve);
		arquivoAux = new File("jogos/comp/arquivoLZW" + version + ".db");
		try {
			FileInputStream fInput = new FileInputStream(arquivo);
			DataInputStream dInput = new DataInputStream(fInput);
			FileOutputStream fOutput = new FileOutputStream(arquivoAux);
			DataOutputStream dOutput = new DataOutputStream(fOutput);

			Byte cyte;
			bsx = dInput.readInt();

			while (true) {
				try {
					cyte = dInput.readByte();
					cg += bttost[ConvercaoInt(cyte)];
					if (cg.length() >= bsx)
						break;
				} catch (EOFException eof) {
					break;
				}
			}

			if (cg.length() >= bsx) {
				knts = ConvercaoInt2(cg.substring(0, bsx));
				cg = cg.substring(bsx, cg.length());
			} else {
				dInput.close();
				dOutput.close();
				return;
			}

			String imp = "" + (char) knts;

			dOutput.writeBytes(imp);

			while (true) {
				try {
					while (cg.length() < bsx) {
						cyte = dInput.readByte();
						cg += bttost[ConvercaoInt(cyte)];
					}
					knts = ConvercaoInt2(cg.substring(0, bsx));
					cg = cg.substring(bsx, cg.length());

					String entry = "";
					if (dictionary.containsKey(knts)) {

						entry = dictionary.get(knts);
					} else if (knts == TamDicionario) {
						entry = imp + imp.charAt(0);

					}
					dOutput.writeBytes(entry);

					if (mpos < 100000) {
						ppt = imp + entry.charAt(0);
						dictionary.put(TamDicionario++, ppt);
						mpos += ppt.length();
					}
					imp = entry;
				} catch (EOFException eof) {
					break;
				}
			}
			dInput.close();
			dOutput.close();
			fInput.close();
			fOutput.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		arquivo = null;
		arquivoAux = null;
	}

	//Binario para string
	public static void convercaoS() {
		int i, j;
		String r1;
		bttost[0] = "0";
		for (i = 0; i < 256; i++) {
			r1 = "";
			j = i;
			if (i != 0)
				bttost[i] = "";
			while (j != 0) {
				if ((j % 2) == 1)
					bttost[i] += "1";
				else
					bttost[i] += "0";
				j /= 2;
			}
			for (j = bttost[i].length() - 1; j >= 0; j--) {
				r1 += bttost[i].charAt(j);
			}
			while (r1.length() < 8) {
				r1 = "0" + r1;
			}
			bttost[i] = r1;
		}
	}

	//Byte para int
	public static int ConvercaoInt(Byte var) {
		int resppp = var;
		if (resppp < 0)
			resppp += 256;
		return resppp;

	}

	//String para int
	public static int ConvercaoInt2(String str) {
		int resppp = 0, i;
		for (i = 0; i < str.length(); i++) {
			resppp *= 2;
			if (str.charAt(i) == '1')
				resppp++;
		}
		return resppp;
	}

	//funcao principal
	public static void lzwdescomprecao(String archieve, int version) {
		cg = "";
		bsx = 0;
		convercaoS();
		descomprecaoIn(archieve, version);
		cg = "";
		bsx = 0;
	}

}