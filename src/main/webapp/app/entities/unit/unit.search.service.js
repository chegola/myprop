(function() {
    'use strict';

    angular
        .module('mypropApp')
        .factory('UnitSearch', UnitSearch);

    UnitSearch.$inject = ['$resource'];

    function UnitSearch($resource) {
        var resourceUrl =  'api/_search/units/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
