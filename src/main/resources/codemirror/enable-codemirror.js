window.addEventListener('load', ready, false);

function ready(){
	
	enableCodeMirror('.json:not(.rest-failure)');
	enableCodeMirrorMerge('.json.rest-failure');
	
}

function enableCodeMirror(selector){

	var i, value, editor,
		jsons = document.querySelectorAll(selector);

	for(i=0; i<jsons.length; i++){

		value = jsons[i].innerHTML;
		jsons[i].innerHTML = "";
		
		editor = CodeMirror(jsons[i], {
			lineNumbers: false,
			mode: {name: 'javascript', json: true},
			value: value,
			readOnly: true
		});
	}	
}

function enableCodeMirrorMerge(selector){

	var jsons = document.querySelectorAll(selector);
	var i;
	
	for(i=0; i<jsons.length; i++){
		mergeView(jsons[i]);
	}	
};

function mergeView(target){

	var editor,
		expectedValue, actualValue, 
		expected = target.querySelector('.expected'),
		actual = target.querySelector('.actual');

	expectedValue = expected.innerHTML;
	actualValue = actual.innerHTML;
	
	actual.parentNode.removeChild(actual);
	expected.parentNode.removeChild(expected);
	target.innerHTML = '';
	
	editor = CodeMirror.MergeView(target, {
		value: expectedValue,
		origLeft: null,
		orig: actualValue,
		lineNumbers: false,
		revertButtons: false,
		mode: {name: 'javascript', json: true},
		highlightDifferences: true,
		collapseIdentical: false
//		connect: "align"
	});
};