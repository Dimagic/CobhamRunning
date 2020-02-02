package net.ddns.dimag.cobhamrunning.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.MultiDoc;
import javax.print.MultiDocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import fr.w3blog.zpl.constant.ZebraFont;
import fr.w3blog.zpl.model.ZebraLabel;
import fr.w3blog.zpl.model.ZebraPrintException;
import fr.w3blog.zpl.model.element.ZebraBarCode39;
import fr.w3blog.zpl.model.element.ZebraText;
import fr.w3blog.zpl.utils.ZebraUtils;
import fr.w3blog.zpl.utils.ZplUtils;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.AsisPrintJob;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;

public class ZebraPrint {
	private PrintService printService;
	private AsisPrintJob printJob;
	private Set<LabelTemplate> templates;
	private String printerName;

	public ZebraPrint() {
		testpng();
	}
	
	public void testpng(){
//		String s = "^XA^LH20,3^FO0,0^B3N,N,40,N,N^FD<AN9><SN>^FS^FO0,55^AFN,55,25^FD<SN>^FS^FO150,50^AFN,20,13^FD<AN>^FS^FO380,50^AFN,20,13^FD<SUBCON>^FS^FO150,80^AFN,20,13^FD<SAD>^FS^FO455,50^AFN,20,13^FD<REGDAT>^FS^FO470,80^AFN,20,13^FDCOBHAM^FS^XZ";
//		ZplUtils zu = new ZplUtils();
//		zu.zplCommand(s);
//		ZebraLabel zebraLabel = new ZebraLabel(510, 90);
//		zebraLabel.setDefaultZebraFont(ZebraFont.ZEBRA_ZERO);
////		new ZebraBarCode39(positionX, positionY, text, barCodeHeigth)
//		zebraLabel.addElement(new ZebraText(10, 20, "CA201212AA", 14));
//		zebraLabel.addElement(new ZebraBarCode39(0, 0, "CA201212AA", 40, 0, 0));
//		zebraLabel.addElement(new );
//		BufferedImage image = zebraLabel.getImagePreview();
//		try {
//			ImageIO.write(image, "png", new File("zpl.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public ZebraPrint(AsisPrintJob printJob, String printerName) {
		this.printerName = printerName;
		this.printService = getPrinterByName(printerName);
		this.printJob = printJob;
		this.templates = printJob.getArticle().getTemplates();
	}

	public PrintService getPrintService() {
		return printService;
	}

	public void setPrintService(PrintService printService) {
		this.printService = printService;
	}

	public boolean printLabel() {
		if (printService == null || printJob == null) {
			MsgBox.msgWarning("Print label", "Print service or print job document is invalid.");
			return false;
		}
		String currTemplate = "";
		String dateCreate = getDateForPrint(printJob.getDateCreate());
		StringBuffer buffer;
		List<String> printList = new ArrayList<String>();
		for (LabelTemplate template : templates) {
			buffer = new StringBuffer();
			for (Asis asis : printJob.getAsisSet()) {
				currTemplate = template.getTemplate();
				currTemplate = currTemplate.replaceAll("<AN9>", asis.getArticleHeaders().getArticle());
				currTemplate = currTemplate.replaceAll("<AN>", asis.getArticleHeaders().getArticle());
				currTemplate = currTemplate.replaceAll("<SN>", asis.getAsis());
				currTemplate = currTemplate.replaceAll("<SUBCON>", "PCB");
				currTemplate = currTemplate.replaceAll("<SAD>", asis.getArticleHeaders().getShortDescript());
				currTemplate = currTemplate.replaceAll("<REGDAT>", dateCreate);
				if (asis.getArticleHeaders().getNeedmac()) {
					currTemplate = currTemplate.replace("<MAC>", asis.getMacAddress().getMac());
				}
				buffer.append(currTemplate);
				printList.add(currTemplate);
			}
			runPrinting(buffer.toString());
		}

		return true;
	}

	private void runPrinting(String s) {
		try {
			DocPrintJob job = getPrintService().createPrintJob();
			job.addPrintJobListener(new ZebraJobListener());
			byte[] by = s.getBytes();
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			Doc doc = new SimpleDoc(by, flavor, null);
			job.print(doc, null);
		} catch (PrintException e) {
			MsgBox.msgException(e);
		}

	}

	private String getDateForPrint(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String y = Integer.toString(year).substring(2);
		String m;
		String d;
		if (month < 10) {
			m = String.format("0%s", Integer.toString(month));
		} else {
			m = Integer.toString(month);
		}
		if (day < 10) {
			d = String.format("0%s", Integer.toString(day));
		} else {
			d = Integer.toString(day);
		}
		return String.format("%s%s%s", y, m, d);
	}

	private PrintService getPrinterByName(String name) {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		for (PrintService prService : printServices) {
			if (prService.getName().toLowerCase().contains(name.toLowerCase())) {
				return prService;
			}
		}
		return null;
	}

	public void stringToBarcode() {
		try {
			// Create the barcode bean
			Code39Bean bean = new Code39Bean();

			final int dpi = 150;

			// Configure the barcode generator
			bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); // makes the narrow
																// bar
																// width exactly
																// one pixel
			bean.setWideFactor(3);
			bean.doQuietZone(false);

			// Open output file
			File outputFile = new File("out.jpg");
			OutputStream out = new FileOutputStream(outputFile);
			try {
				// Set up the canvas provider for monochrome JPEG output
				BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/jpeg", dpi,
						BufferedImage.TYPE_BYTE_BINARY, false, 0);

				// Generate the barcode
				bean.generateBarcode(canvas, "RRU00431/PHTZ");

				// Signal end of generation
				canvas.finish();
			} finally {
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class JobCompleteMonitor extends PrintJobAdapter {
		private boolean completed = false;

		@Override
		public void printJobCanceled(PrintJobEvent pje) {
			signalCompletion();
		}

		@Override
		public void printJobCompleted(PrintJobEvent pje) {
			signalCompletion();
		}

		@Override
		public void printJobFailed(PrintJobEvent pje) {
			signalCompletion();
		}

		@Override
		public void printJobNoMoreEvents(PrintJobEvent pje) {
			signalCompletion();
		}

		private void signalCompletion() {
			synchronized (JobCompleteMonitor.this) {
				completed = true;
				JobCompleteMonitor.this.notify();
			}
		}

		public synchronized void waitForJobCompletion() {
			try {
				while (!completed) {
					wait();
				}

			} catch (InterruptedException e) {

			}
		}
	}

}
