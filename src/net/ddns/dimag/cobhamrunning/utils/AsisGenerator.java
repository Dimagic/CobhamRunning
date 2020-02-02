package net.ddns.dimag.cobhamrunning.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;

public class AsisGenerator {
	private List<String> symList;

	public AsisGenerator() {
		this.symList = getSymList();
	}

	public void generate() {
		// ASIS -> XXXX -> 4321
		for (int i4 = 0; i4 < symList.size(); i4++) {
			for (int i3 = 0; i3 < symList.size(); i3++) {
				for (int i2 = 0; i2 < symList.size(); i2++) {
					for (int i1 = 0; i1 < symList.size(); i1++) {

						System.out.println(String.format("%s%s%s%s", symList.get(i4), symList.get(i3), symList.get(i2),
								symList.get(i1)));
					}
				}
			}
		}
	}

	public List<String> getNewAsisRange(int count) {
		List<String> asisRange = new ArrayList<>();
		String lastAsis = "PRJ5";
		for (int i = 0; i < count; i++) {
			asisRange.add(getNewAsis(lastAsis));
			lastAsis = getNewAsis(lastAsis);

		}
		return asisRange;
	}

	private String getNewAsis(String lastAsis) {
		List<String> charr = Arrays.asList(lastAsis.split("(?!^)"));
		Collections.reverse(charr);
		for (int i = 0; i < lastAsis.length(); i++) {
			if (!isLastElement(charr.get(i))) {
				String nextVal = symList.get(symList.indexOf(charr.get(i)) + 1);
				charr.set(i, nextVal);
				Collections.reverse(charr);
				return getAsisString(charr);
			}
			while (true) {
				// if current and next elements are last
				try {
					charr.set(i, symList.get(0));
					if (isLastElement(charr.get(i + 1))) {
						charr.set(i + 1, symList.get(symList.indexOf(charr.get(i + 1)) + 1));
						i += 1;
					} else {
						charr.set(i + 1, symList.get(symList.indexOf(charr.get(i + 1)) + 1));
						Collections.reverse(charr);
						return getAsisString(charr);
					}
				} catch (IndexOutOfBoundsException e) {
					// there are no more asis
					break;
				}
			}
		}
		return null;
	}

	private String getAsisString(List<String> charr) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : charr) {
			stringBuilder.append(s);
		}
		return stringBuilder.toString();
	}

	private boolean isLastElement(String s) {
		return symList.get(symList.size() - 1).equals(s);
	}

	public void test() {
		String currAsis = "AAAA";
		System.out.println(symList.size());
		System.out.println(100 / symList.size());
		System.out.println(100 % symList.size());
		for (int i = currAsis.length() - 1; i >= 0; i--) {
			String s = Character.toString(currAsis.charAt(i));
			int currIndex = symList.indexOf(s);
			System.out.println(currIndex);
		}
	}

	public List<String> getSymList() {
		List<String> symList = new ArrayList<String>();
		for (int i = 0; i <= 9; i++) {
			symList.add(Integer.toString(i));
		}
		for (char az = 'A'; az <= 'Z'; az++) {
			symList.add(Character.toString(az));
		}
		return symList;
	}

	public boolean printLabel(PrintService printService, String asis) {
		if (printService == null || asis == null) {
			System.err.println("[Print Label] print service or label is invalid.");
			return false;
		}
		String czas = new SimpleDateFormat("d MMMMM yyyy'r.' HH:mm s's.'").format(new Date());
		String command = "N\n" + "A50,50,0,2,2,2,N,\"" + asis + "\"\n" + "B50,100,0,1,2,2,170,B,\"" + asis + "\"\n"
				+ "A50,310,0,3,1,1,N,\"" + czas + "\"\n" + "P1\n";
		String command2 = "^XA\n" + 
				"^LH20,3\n" + 
				"^FO0,0^B3N,N,40,N,N^FD<RRU00431><"+asis+">^FS\n" + 
				"^FO0,55^AFN,55,25^FD"+asis+"^FS\n" + 
				"^FO150,50^AFN,20,13^FDRRU00431^FS\n" + 
				"^FO380,50^AFN,20,13^FDPCB^FS\n" + 
				"^FO150,80^AFN,20,13^FDID-DAS RRU 3604^FS\n" + 
				"^FO455,50^AFN,20,13^FD<REGDAT>^FS\n" + 
				"^FO470,80^AFN,20,13^FDCOBHAM^FS\n" + 
				"^XZ";

		byte[] data;
		data = command2.getBytes(StandardCharsets.US_ASCII);
		Doc doc = new SimpleDoc(data, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
		boolean result = false;
		try {
			printService.createPrintJob().print(doc, null);
			System.out.println(data);
			result = true;
		} catch (PrintException e) {
			e.printStackTrace();
		}
		return result;
	}

}
