package at.pw.sammlung;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import at.pw.model.Inventory;
import at.pw.service.InventoryLocalServiceUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.simple.Element;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleConstants;
import com.liferay.portlet.journal.model.JournalStructure;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalStructureLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class AddObject
 */
public class AddObject extends MVCPortlet {
	/**
	 * Error Format for date
	 */
	String date_format_apache_error_pattern = "EEE MMM dd HH:mm:ss yyyy";
	SimpleDateFormat date_format_apache_error = new SimpleDateFormat(date_format_apache_error_pattern);
	String class_ = "PW-portlet::at.pw.sammlung.AddObject::";
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	
	
	public void addObject(ActionRequest request, ActionResponse response) throws Exception {
		
		String title = ParamUtil.getString(request, "title");
		String structureId = ParamUtil.getString(request, "structureId");
		String inventarNumberpattern = ParamUtil.getString(request, "inventarNumberpattern");
	
		long parentWebContentFolderID = ParamUtil.getLong(request, "webcontentFolderId");
		long parentDocumentFolderID   = ParamUtil.getLong(request, "documentFolderId");

		Inventory inventory = InventoryLocalServiceUtil.addInventoryWithAutoincrement();
		
		long inventarnummer = inventory.getInventarnummer();
		
		String foldername = String.format ("%06d_", inventarnummer) + title;
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		// Add WebContent
		String articaltemplate = ParamUtil.getString(request, "articaltemplate");
		// Add Folder
		
		long folderId = addFolder(foldername, parentDocumentFolderID, themeDisplay.getScopeGroupId(), request);
		
		long webcontetnId = addWebcontent(title, structureId, themeDisplay, parentWebContentFolderID, articaltemplate, request, inventarnummer, inventarNumberpattern, folderId);
		
		// Add Inventory
		inventory.setWebcontentId(webcontetnId);
		inventory.setFolderId(folderId);
		inventory.setStructureId(structureId);
		inventory.setStructuretemplateId("");
		InventoryLocalServiceUtil.updateInventory(inventory);
		
	}
	
 
	private long addFolder(String title, long parentFolderID, long repositoryId, ActionRequest request) {
		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFolder.class.getName(), request);
			Folder folder = DLAppServiceUtil.addFolder(repositoryId, parentFolderID, title, title, serviceContext);
			return folder.getFolderId();
		} catch(Exception ex) {
			System.err.println("[" + date_format_apache_error.format(new Date()) + "] [error] [PuchMuseum-portlet::at.graz.hmmc.liferay.portlet.puch.ImageUpload::createFolder] Error creating new Folder.");
			ex.printStackTrace();
		}
		return 0;
	}
	
	private long addWebcontent(String title, String structureId, ThemeDisplay themeDisplay, long parentFolderID, String articaltemplate, ActionRequest request, long inventarnummer, String inventarNumberpattern, long folderID) {
		
		
		long userId = themeDisplay.getUserId();
		long groupId = themeDisplay.getScopeGroupId();
		Map<Locale, String> titleMap = getNameMap(title);
		Map<Locale, String> descriptionMap = getNameMap(title);
		DecimalFormat myFormatter = new DecimalFormat(inventarNumberpattern);
		System.out.println(inventarNumberpattern);
		System.out.println(myFormatter.format(inventarnummer));
		
		
		
		String content = "<?xml version='1.0' encoding='UTF-8'?><root default-locale=\"de_DE\" available-locales=\"de_DE\"><dynamic-element name=\"_RON_Inventarnummer\" type=\"text\" index-type=\"keyword\" index=\"0\"><dynamic-content language-id=\"de_DE\"><![CDATA[" + myFormatter.format(inventarnummer) + "]]></dynamic-content></dynamic-element></root>";
		String ddmTemplateKey = "0";
		for(String id : articaltemplate.split(";")) {
			if(id.startsWith(structureId + "_")) {
				ddmTemplateKey = id.replaceAll(structureId + "_", "");
			}
		}
		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFolder.class.getName(), request);
			serviceContext.setScopeGroupId(themeDisplay.getScopeGroupId());
			serviceContext.setUserId(themeDisplay.getUserId());
			
			JournalArticle articel = JournalArticleLocalServiceUtil.addArticle(userId, groupId, parentFolderID, titleMap, descriptionMap, content, structureId, ddmTemplateKey, serviceContext);
			
			long classNameId = PortalUtil.getClassNameId(JournalArticle.class);
			DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(groupId, classNameId, structureId);
					
			String structureName = ddmStructure.getName(themeDisplay.getLocale());
					
			AssetVocabulary av = AssetVocabularyLocalServiceUtil.getGroupVocabulary(groupId, "Sammlungsobjekt");
			
			System.out.println( "SAMMLUNGSOBJECT HAT KATEGORIEN ");
			int index = 0;

			final String[] tagNames = {};
			final long[] assetCategoryIds = new long[1];
			
			final String defaultCategory = "beliebiges Objekt";
			
			boolean categorieFound = false;
			long defaultCategoryID = 0;
			
			for(AssetCategory c : av.getCategories()) {
				System.out.println( index + " = " + c.getName()+ ", id= " + c.getCategoryId());
				
				if (c.getName().equalsIgnoreCase(structureName)) {
					assetCategoryIds[0] = c.getCategoryId();
					categorieFound = true;
				}
				if (c.getName().equalsIgnoreCase(defaultCategory)) {
					defaultCategoryID = c.getCategoryId();
				}
				index++;
			}
			
			if (!categorieFound) {
				assetCategoryIds[0] = defaultCategoryID;
			}
			
			System.out.println("here we should look for the Template Name, If this matches with a category, the category is set, otherwise we set the the category generalObjekt");
			System.out.println(structureId);
			
			AssetEntryLocalServiceUtil.updateEntry(userId, groupId, JournalArticle.class.getName(), articel.getResourcePrimKey(), assetCategoryIds, tagNames);
			// set the main categories of the article according to the template
			
			String documentFolderURL = "http://sammlung.puchwiki.org/dokumente";
			
			documentFolderURL = "http://localhost:8080/web/guest/dokumente";
			documentFolderURL += "?p_p_id=20&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&_20_struts_action=%2Fdocument_library%2Fview&_20_folderId=";
			documentFolderURL += folderID;
			documentFolderURL += "&_20_viewEntries=true&_20_viewFolders=false&_20_action=browseFolder&_20_displayStyle=icon&_20_navigation=home&_20_searchType=2&_20_viewEntriesPage=false&_20_saveDisplayStyle=1";
			
	
			String content_HID_Dokument_Folder_ID = "<dynamic-element name=\"_HID_Dokument-Folder-ID\" type=\"text\" index-type=\"keyword\" index=\"0\"><dynamic-content language-id=\"de_DE\"><![CDATA[" + documentFolderURL + "]]></dynamic-content></dynamic-element>";
			
			//update the article
			try {
				Document doc = SAXReaderUtil.read (articel.getContent());
				System.out.println (doc.asXML());
				
				String fieldValue = ""; 
				String fieldName = "Inventarnummer"; 
				
				Node root =  doc.getRootElement();
				
				
				if(Validator.isNotNull(doc)) { 
								
					Node fieldContent = doc.selectSingleNode("//*/dynamic-element[@name='"+fieldName+"']/dynamic-content"); 
						if(fieldContent != null) { 
								 fieldValue = fieldContent.getText(); 
								 System.out.println("Inventarnummer = " + fieldValue);
						}
						
						
						
				}
				
				
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			
			
			return articel.getPrimaryKey();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		//JournalArticleLocalServiceUtil.addArticle(userId, groupId, folderId, classNameId, classPK, articleId, autoArticleId, version, titleMap, descriptionMap, content, type, ddmStructureKey, ddmTemplateKey, layoutUuid, displayDateMonth, displayDateDay, displayDateYear, displayDateHour, displayDateMinute, expirationDateMonth, expirationDateDay, expirationDateYear, expirationDateHour, expirationDateMinute, neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour, reviewDateMinute, neverReview, indexable, smallImage, smallImageURL, smallImageFile, images, articleURL, serviceContext)
	}
	
	private Map<Locale, String> getNameMap(String name) {
		Map<Locale, String> nameMap = new HashMap<Locale, String>();
		Locale locale = LocaleUtil.getDefault();
		nameMap.put(locale, name);
		return nameMap;
	}

}
