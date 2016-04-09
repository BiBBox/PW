var fieldWrapperSelector = '.control-group.field-wrapper';

hideFields()

function hideFields() {

	YUI().use('node', function (Y) {
		
		

		var allFieldWrappers = Y.all(fieldWrapperSelector);
		var fieldsDisplay = [];
		allFieldWrappers.each(function (elFieldWrapper) {			
			var fieldname = elFieldWrapper.getData('fieldname');
			if (fieldname.startsWith('_HID_')) {
				elFieldWrapper.hide();
			}
			if (fieldname.startsWith('_RON_')) {
				elFieldWrapper.one('input').setAttribute ('readonly', true);
			}
		});
	});
	
}
