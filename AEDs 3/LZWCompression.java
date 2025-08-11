import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LZWCompression {
	public static int bsx;
	public static String cg;

	//inicializa comprecao
	public static void ComprecaoIn(String fileis, int version) {
		Map<String, Integer> dicionario = new HashMap<String, Integer>();
		int TamDicionario = 256;
		cg = "";
		for (int i = 0; i < 256; i++) dicionario.put("" + (char) i, i);

		int mpos = 256;
		String zzz = "";
		File arquivo, arquivoAux;
		arquivo = new File(fileis);
		arquivoAux = new File("jogos/comp/arquivoLZWCompressao" + version + ".db");

		try {
			FileInputStream fInput = new FileInputStream(arquivo);
			DataInputStream dInput = new DataInputStream(fInput);
			FileOutputStream fOutput = new FileOutputStream(arquivoAux);
			DataOutputStream dOutput = new DataOutputStream(fOutput);

			dOutput.writeInt(bsx);
			Byte bu;
			int chu;
			while (true) {
				try {
					bu = dInput.readByte();
					chu = convercaoo(bu);

					String buw = zzz + (char) chu;
					if (dicionario.containsKey(buw))
						zzz = buw;
					else {
						cg += convercaoo2(dicionario.get(zzz));
						while (cg.length() >= 8) {
							dOutput.write(convercaoS(cg.substring(0, 8)));
							cg = cg.substring(8, cg.length());
						}

						if (mpos < 100000) {
							dicionario.put(buw, TamDicionario++);
							mpos += buw.length();
						}
						zzz = "" + (char) chu;
					}
				} catch (EOFException eof) {
					break;
				}
			}

			if (!zzz.equals("")) {
				cg += convercaoo2(dicionario.get(zzz));
				while (cg.length() >= 8) {
					dOutput.write(convercaoS(cg.substring(0, 8)));
					cg = cg.substring(8, cg.length());
				}
				if (cg.length() >= 1) {
					dOutput.write(convercaoS(cg));
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

	public static int convercaoo(Byte var) {
		int ret = var;
		if (ret < 0) {
			ret += 256;
		}
		return ret;

	}

	//Inteiro para binario
	public static String convercaoo2(int inp) {
		String ret = "", r1 = "";
		if (inp == 0)
			ret = "0";
		int i;
		while (inp != 0) {
			if ((inp % 2) == 1)
				ret += "1";
			else
				ret += "0";
			inp /= 2;
		}
		for (i = ret.length() - 1; i >= 0; i--) {
			r1 += ret.charAt(i);
		}
		while (r1.length() != bsx) {
			r1 = "0" + r1;
		}
		return r1;
	}

	//String para bytes
	public static Byte convercaoS(String in) {

		int i, n = in.length();
		byte ret = 0;
		for (i = 0; i < n; i++) {
			ret *= 2.;
			if (in.charAt(i) == '1')
				ret++;
		}
		for (; n < 8; n++)
			ret *= 2.;
		Byte r = ret;
		return r;
	}

	//faz dicionario
	public static void DicionarioContas(String archieve) {
		Map<String, Integer> dicionario = new HashMap<String, Integer>();
		int TamDicionario = 256;
		for (int i = 0; i < 256; i++) dicionario.put("" + (char) i, i);

		int mpos = 256;
		String zzz = "";

		File arquivo = null;
		arquivo = new File(archieve);

		try {
			FileInputStream fInput = new FileInputStream(arquivo);
			DataInputStream dInput = new DataInputStream(fInput);

			Byte bu;
			int chu;
			while (true) {
				try {
					bu = dInput.readByte();
					chu = convercaoo(bu);
					String buw = zzz + (char) chu;
					if (dicionario.containsKey(buw))
						zzz = buw;
					else {
						if (mpos < 100000) {
							dicionario.put(buw, TamDicionario++);
							mpos += buw.length();
						}
						zzz = "" + (char) chu;
					}
				} catch (EOFException eof) {
					break;
				}
			}
			fInput.close();
			dInput.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		//Verificação do arquivo vazio
		if (TamDicionario <= 1) {
			bsx = 1;
		} else {
			bsx = 0;
			long i = 1;
			while (i < TamDicionario) {
				i *= 2;
				bsx++;
			}
		}
		arquivo = null;

	}

	//funcao principal
	public static void lzwComprecao(String archieve, int version) {
		bsx = 0;
		cg = "";
		DicionarioContas(archieve);
		ComprecaoIn(archieve, version);
		bsx = 0;
		cg = "";
	}

}