(function() {
    'use strict';

    angular
        .module('mypropApp')
        .filter('unpin', unpin);

    function unpin() {
        return unpinFilter;

        function unpinFilter (input) {
            var output = [];
            angular.forEach(input, function(item) {
                if (item.sticky === null  || item.sticky === false) {
                        output.push(item);
                    }
                }
            );
            return output;
        }
    }
})();
