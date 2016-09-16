(function() {
    'use strict';
    angular
        .module('mypropApp')
        .controller('UserCommentAnnouncementController', UserCommentAnnouncementController);

    UserCommentAnnouncementController.$inject = ['$scope', '$state', 'UserComment', '$stateParams', 'AlertService', '$log'];

    function UserCommentAnnouncementController ($scope, $state, UserComment, $stateParams, AlertService, $log) {
        var vm = this;
        vm.userComments = [];
        vm.getByAnnouncement = getByAnnouncement;

        function getByAnnouncement() {
            UserComment.queryByAnnouncement({id : $stateParams.id},
                onSuccess, onError);

            function onSuccess(data, headers) {
                vm.userComments = data;
                $log.debug("getByAnnouncementId:" + $stateParams.id + " SUCCESS");
            }

            function onError(error) {
                 $log.debug("getByAnnouncementId:" + $stateParams.id + " ERROR");
                 AlertService.error(error.data.message);
            }
        }
    }
})();
