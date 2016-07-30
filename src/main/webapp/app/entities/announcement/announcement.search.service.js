(function() {
    'use strict';

    angular
        .module('mypropApp')
        .factory('AnnouncementSearch', AnnouncementSearch);

    AnnouncementSearch.$inject = ['$resource'];

    function AnnouncementSearch($resource) {
        var resourceUrl =  'api/_search/announcements/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
