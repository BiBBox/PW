package at.pw;


import com.liferay.portal.kernel.xml.Document; 
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

public class HideFields {

	
	public String clean (String input ) {

		String es = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + input; 
		try {
			Document document = SAXReaderUtil.read(es);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return input;	
	}
}
