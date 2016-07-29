(function() {
    'use strict';

    angular
        .module('mypropApp')
        .factory('MyAccountSearch', MyAccountSearch);

    MyAccountSearch.$inject = ['$resource'];

    function MyAccountSearch($resource) {
        var resourceUrl =  'api/_search/my-accounts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
