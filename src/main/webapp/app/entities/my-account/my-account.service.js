(function() {
    'use strict';
    angular
        .module('mypropApp')
        .factory('MyAccount', MyAccount);

    MyAccount.$inject = ['$resource'];

    function MyAccount ($resource) {
        var resourceUrl =  'api/my-accounts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
