(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('UserCommentDetailController', UserCommentDetailController);

    UserCommentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserComment', 'Announcement', 'User'];

    function UserCommentDetailController($scope, $rootScope, $stateParams, previousState, entity, UserComment, Announcement, User) {
        var vm = this;

        vm.userComment = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mypropApp:userCommentUpdate', function(event, result) {
            vm.userComment = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
