(function() {
    'use strict';
    angular
        .module('mypropApp')
        .factory('Line', Line);

    Line.$inject = ['$resource', 'DateUtils'];

    function Line ($resource, DateUtils) {
        var resourceUrl =  'api/lines/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.timestamp = DateUtils.convertLocalDateFromServer(data.timestamp);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.timestamp = DateUtils.convertLocalDateToServer(data.timestamp);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.timestamp = DateUtils.convertLocalDateToServer(data.timestamp);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
