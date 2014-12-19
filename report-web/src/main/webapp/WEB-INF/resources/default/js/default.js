function doOrder() {
    var order = '';
    var len = $('.outputHidden').length;
    $('.outputHidden').each(function(index) {
        order += ($(this).text() + ((index == (len - 1)) ? '' : ';'));
    });
    $('#form\\:order').val(order);
    return true;
}