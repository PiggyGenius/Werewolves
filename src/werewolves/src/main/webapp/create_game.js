//function isPositiveInteger(str) {
    //return /^?(0|[1-9]\d*)$/.test(str);
//}
function getDate(){
	var tomorrow = new Date();
	tomorrow.setDate(tomorrow.getDate() + 1);
	var year = tomorrow.getFullYear();
	var month = tomorrow.getMonth() + 1;
	var day = tomorrow.getDate();
	if(month < 10)
		month = '0' + month;
	if(day < 10)
		day = '0' + day;
	return year + '-' + month + '-' + day;
}

$(document).ready(function () {
	// Doesn't work with firefox, buggy
	//$(function() {
		//$( "#gameDate" ).datepicker({ dateFormat: 'yy-mm-dd'});
	//});
	$(function(){
		var dayHours = $('.dayHours');
		var dayLengthHours = $('.dayLengthHours');
		var nightLengthHours = $('.nightLengthHours');
		for(i=0; i <= 23; i++){
			dayHours.append($('<option></option>').val(i).html(i));
			dayLengthHours.append($('<option></option>').val(i).html(i));
			nightLengthHours.append($('<option></option>').val(i).html(i));
		}

		var dayMinutes = $('.dayMinutes');
		var dayLengthMinutes = $('.dayLengthMinutes');
		var nightLengthMinutes = $('.nightLengthMinutes');
		dayMinutes.append($('<option selected="selected"></option>').val(0).html(0));
		dayLengthMinutes.append($('<option selected="selected"></option>').val(0).html(0));
		nightLengthMinutes.append($('<option selected="selected"></option>').val(0).html(0));
		for(i=1; i <= 59; i++){
			dayMinutes.append($('<option></option>').val(i).html(i));
			dayLengthMinutes.append($('<option></option>').val(i).html(i));
			nightLengthMinutes.append($('<option></option>').val(i).html(i));
		}
		dayHours.val(8);
		dayLengthHours.val(14);
		nightLengthHours.val(10);
		document.getElementById('gameDate').value = getDate();
	});
    $('#createGame').validate({ // initialize the plugin
        rules: {
            minPlayer: {
                required: true,
                digits: true
            },
            maxPlayer: {
                required: true,
				digits: true
            },
			gameDate: {
				required: true,
				dateISO: true
			},
			contamination: {
				required: true,
				number: true,
				max: 1.0,
				min: 0.0
			},
			insomniac: {
				required: true,
				number: true,
				max: 1.0,
				min: 0.0
			},
			fortuneTeller: {
				required: true,
				number: true,
				max: 1.0,
				min: 0.0
			},
			spiritualist: {
				required: true,
				number: true,
				max: 1.0,
				min: 0.0
			},
			werewolf: {
				required: true,
				number: true,
				max: 1.0,
				min: 0.0
			}
        },
    });
});
