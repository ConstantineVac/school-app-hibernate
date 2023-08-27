$(document).ready(function() {
    // Handle search form submission
    $('.search').submit(function(e) {
        e.preventDefault();
        let searchVal = $('.search').val();

        if (!searchVal || searchVal === '') {
            // Add a blue border to the input to indicate an error
            $('.search').addClass('input-error');
        } else {
            // Remove the blue border if it exists
            $('.search').removeClass('input-error');

            // Perform AJAX search request here
        }
    });

    // Handle insert form submission
    $('.insert').submit(function(e) {
        e.preventDefault();
        let insertVal = $('.insert').val();

        if (!insertVal || insertVal === '') {
            // Add a blue border to the input to indicate an error
            $('.insert').addClass('input-error');
        } else {
            // Remove the blue border if it exists
            $('.insert').removeClass('input-error');
            // Perform AJAX insert request here
        }
    });
});