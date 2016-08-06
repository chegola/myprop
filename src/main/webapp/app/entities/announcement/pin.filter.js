(function() {
    'use strict';

    angular
        .module('mypropApp')
        .filter('pin', pin);

    function pin() {
        return pinFilter;

        function pinFilter (input) {
            var output = [];
            angular.forEach(input, function(item) {
                if (item.sticky === true) {
                        output.push(item);
                    }
                }
            );
            return output;
        }
    }
})();
