(function() {
    'use strict';

    angular
        .module('mypropApp')
        .filter('loginName', loginName);

    function loginName() {
        return loginNameFilter;

        function loginNameFilter (input) {
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
