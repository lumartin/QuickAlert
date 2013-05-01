package com.quantizity.quickalert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

public class AlertStorage {
	private final int MAX_ELEMENTS = 1;
	
	private Vector<Alert> alerts = null;
	
	public AlertStorage() throws Exception 
	{
		try {
			this.setAlerts(new Vector<Alert>(MAX_ELEMENTS));
		} catch (Exception ex) {
			throw ex;
		}
	}

	public Vector<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(Vector<Alert> alerts) throws Exception {
		if (alerts.size()<=MAX_ELEMENTS) this.alerts = alerts;
		else throw new Exception("MAX_ELEMENTS for alerts exceeded");
	}
	
	public int addAlert(Alert alert) 
	{
		if (alerts.size()==MAX_ELEMENTS) return -2;
		if (alert.getDuration()<=0 || alert.getEndTime()==null) return -1;
		alerts.add(alert);
		return 0;
	}
	
	public  boolean hasSameEndTime(Alert al) {
		long al_min = (long) al.getEndTime().getTime()/60000;
		for (Alert alert : this.alerts) {
			long alert_min = (long) alert.getEndTime().getTime()/60000;
			if (al_min==alert_min) return true;
		}
		return false;
	}
	
	public int removeAlert(Alert alert)
	{
		if(alerts.contains(alert)) {
			alerts.remove(alert);
			return 0;
		}
		else return -1;
	}
	
	public int persistAlerts(Context ctx)
	{
		if (alerts.size()>0) {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.newDocument();
				
				Element root = document.createElement("Alerts");
				
				createAlertElements(document, root);
				
				//document.appendChild(root);
				FileOutputStream file = ctx.openFileOutput("alerts.xml", Context.MODE_PRIVATE);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            DOMSource source = new DOMSource(root);
	            StreamResult result =  new StreamResult(file);
	            transformer.transform(source, result);
	            file.close(); 
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	private void createAlertElements(Document document, Element root)
	{
		for (Alert alert : this.alerts) {
			Element elAlert = document.createElement("Alert");
			elAlert.setAttribute("recurring", (alert.isRecurring()? "true" : "false"));
			elAlert.setAttribute("duration", alert.getDuration().toString());
			elAlert.setAttribute("initTime", (alert.getInitTime()!=null)? String.valueOf(alert.getInitTime().getTime()) : "");
			elAlert.setAttribute("endTime", (alert.getEndTime()!=null) ? String.valueOf(alert.getEndTime().getTime()) : "");
			Element description = document.createElement("Description");
			description.setNodeValue(alert.getDescription());
			elAlert.appendChild(description);
			root.appendChild(elAlert);
		}
	}
	
	private void createAlertsFromElements(Document document) {
		NodeList nAlerts = document.getElementsByTagName("Alert");
		this.alerts = new Vector<Alert>();
		for (int i = 0; i< nAlerts.getLength(); i++) {
			Node elAlert = nAlerts.item(i);
			if (elAlert.getNodeType() == Node.ELEMENT_NODE) {
				if(Long.valueOf(elAlert.getAttributes().getNamedItem("endTime").getNodeValue()).longValue() > System.currentTimeMillis()) {
					Alert alert = new Alert();
					alert.setDuration(Long.valueOf(elAlert.getAttributes().getNamedItem("duration").getNodeValue()));
					alert.setRecurring(elAlert.getAttributes().getNamedItem("duration").getNodeValue().equals("true") ? true : false);
					Date iTime = new Date();
					iTime.setTime(Long.valueOf(elAlert.getAttributes().getNamedItem("initTime").getNodeValue()).longValue());
					alert.setInitTime(iTime);
					Date eTime = new Date();
					eTime.setTime(Long.valueOf(elAlert.getAttributes().getNamedItem("endTime").getNodeValue()).longValue());
					alert.setEndTime(eTime);
					alert.setDuration(Long.valueOf(elAlert.getAttributes().getNamedItem("duration").getNodeValue()));
					Node description = elAlert.getFirstChild();
					alert.setDescription(description.getNodeValue());
					if (!alert.isOld()) this.addAlert(alert);
				}
			}
		}
	}
	
	public void loadAlerts(Context ctx)
	{
		try {
			FileInputStream file = ctx.openFileInput("alerts.xml");
			int num = file.read();
			
			if (num>0) {
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(file);
				createAlertsFromElements(document);
			}
			file.close();
			
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}
}
