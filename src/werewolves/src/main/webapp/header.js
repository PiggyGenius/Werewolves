function processHeader(data){
	if(data === 'false')
		$('.header').load('header.html');
	else
		$('.header').load('loginHeader.html');
}

$(function(){
	$.post('loginUser', {}, processHeader)
});
