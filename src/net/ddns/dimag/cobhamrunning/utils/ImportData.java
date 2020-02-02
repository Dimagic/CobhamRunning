package net.ddns.dimag.cobhamrunning.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jboss.logging.Cause;

import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.models.MacAddress;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.AsisService;
import net.ddns.dimag.cobhamrunning.services.LabelTemplateService;
import net.ddns.dimag.cobhamrunning.services.MacAddressService;

public class ImportData implements MsgBox{
	
	public ImportData() {
	}

	public void importAsis() {
		int count = (MsgBox.msgInputInt("Enter ASIS count:")).intValue();
		AsisGenerator aisGenerator = new AsisGenerator();
		List<String> asisRange = aisGenerator.getNewAsisRange(count);
		AsisService asisService = new AsisService();
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			for (String asisStr : asisRange) {
				Asis asis = new Asis(asisStr);
				asisService.saveAsis(asis);
			}
			tx.commit();
			MsgBox.msgInfo("Import complete", "Importing ASIS range complete");
		} catch (Exception e) {
			MsgBox.msgException(e);
			tx.rollback();
		} finally {
			session.close();
		}
	}

	public void generateMac() {
		int count = (MsgBox.msgInputInt("Enter MAC address count:")).intValue();
		MacAddressService macService = new MacAddressService();
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {	
			for (int i = 0; i < count; i++) {
				MacAddress mac = new MacAddress(getRandomMACAddress());
				macService.saveMac(mac);
			}
			tx.commit();
			MsgBox.msgInfo("Import complete", "Generating MAC range complete");
		} catch (Exception e) {
			MsgBox.msgException(e);
			tx.rollback();
		} finally {
			session.close();
		}

	}
	
	private HashMap<String, LabelTemplate> checkTemplates(){
		LabelTemplateService labelService = new LabelTemplateService();
		List<LabelTemplate> labelList = labelService.findAllLabelTemplate();
		HashMap<String, LabelTemplate> labelsMap = new HashMap<String, LabelTemplate>();
		if (labelList.size() == 0){
			MsgBox.msgWarning("Import warning", "Label templates not found.\nPlease fix it and try again.");
			return null;
		}
		for (LabelTemplate template: labelList){
			if (template.getTemplate().toUpperCase().contains("<MAC>")){
				labelsMap.put("MAC", template);
				continue;
			} 
			labelsMap.put("STANDARD", template);
		}
		if (!labelsMap.keySet().contains("MAC")){
			MsgBox.msgWarning("Import warning", "Template for MAC address not found.\nPlease fix it and try again.");
			return null;
		}
		if (!labelsMap.keySet().contains("STANDARD")){
			MsgBox.msgWarning("Import warning", "Standard template not found.\nPlease fix it and try again.");
			return null;
		}
		return labelsMap;
	}
	
	public void importArticles() {
		if (checkTemplates() == null){
			return;
		} 
		LabelTemplateService labelService = new LabelTemplateService();
		List<LabelTemplate> withMac = labelService.findAllLabelTemplate();
		List<LabelTemplate> withoutMac = labelService.findAllWithoutMac();
		List<String[]> importingData;
		CSVUtils csvUtils = new CSVUtils();
		try {
			importingData = csvUtils.getCsvData(); 
		} catch (NullPointerException e) {
			return;
		}
		
		ArticleHeadersService articleHeadersService = new ArticleHeadersService();
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			
			int counter = 0;
			for (String[] s : importingData) {
				if (s[0].length() != 8)
					continue;
				ArticleHeaders articleHeaders = new ArticleHeaders();
				articleHeaders.setName(s[0].substring(0, 7));
				articleHeaders.setRevision(s[0].substring(s[0].length() - 1));
				articleHeaders.setShortDescript(s[1]);
				articleHeaders.setLongDescript(s[2]);
				if (s[3].toUpperCase().equals("YES")){
					articleHeaders.setNeedmac(true);
					articleHeaders.setTemplates(withMac.stream().collect(Collectors.toSet()));
				} else {
					articleHeaders.setNeedmac(false);
					articleHeaders.setTemplates(withoutMac.stream().collect(Collectors.toSet()));
				}
				articleHeadersService.saveArticle(articleHeaders);
	
				counter += 1;
			}
			tx.commit();
			MsgBox.msgInfo("Import complete", String.format("Importing articles range complete.\nImported records: %s", counter));
		} catch (Exception e) {
//			Throwable cause = null;
//			while(null != (cause = e.getCause())  && (e != cause) ) {
//		        System.out.println(cause);
//		    }
			if (tx != null){
				tx.rollback();	
			}
			MsgBox.msgException(e);
			e.printStackTrace();
			
		} finally {
			session.close();
		}	
	}


	private String getRandomMACAddress() {
		Random rand = new Random();
		byte[] macAddr = new byte[6];
		rand.nextBytes(macAddr);

		macAddr[0] = (byte) (macAddr[0] & (byte) 254);
		StringBuilder sb = new StringBuilder(18);
		for (byte b : macAddr) {

			if (sb.length() > 0)
				sb.append("-");

			sb.append(String.format("%02x", b));
		}
		return sb.toString().toUpperCase();
	}
}
