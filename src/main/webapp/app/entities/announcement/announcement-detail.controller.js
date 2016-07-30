(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('AnnouncementDetailController', AnnouncementDetailController);

    AnnouncementDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Announcement'];

    function AnnouncementDetailController($scope, $rootScope, $stateParams, previousState, entity, Announcement) {
        var vm = this;

        vm.announcement = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mypropApp:announcementUpdate', function(event, result) {
            vm.announcement = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
