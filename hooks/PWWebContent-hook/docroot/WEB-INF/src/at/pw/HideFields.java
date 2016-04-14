package at.pw;


import java.util.Set;

import com.liferay.portal.kernel.xml.Document; 
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;

public class HideFields {


	public String clean (String input, String structureIDs) {
		
		
		Element node = null;
		
		System.out.println("CLEAN!");
		String es = "<div>" + input + "</div>";
		try {
			Document document = SAXReaderUtil.read(es);
			
			Element rootElement = document.getRootElement();

		    for (Element element : rootElement.elements()) {
		    	
		    	node = element;
		        String elementName = element.getName();
		        String elementClass = element.attributeValue("data-fieldName");
		    	System.out.println(elementClass);

//		            rootElement.add(serviceBuilderImportElement);
		        }
		 
			
			
		} catch (DocumentException e) {
			System.out.println("ERROR");
//			System.out.println(es);
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		
		return input;	
	}
}
