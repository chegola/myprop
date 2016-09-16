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
            UserComment.queryByAnnouncement({id : $stateParams.id, sort: sort()},
                onSuccess, onError);

            function sort() {
                var result = ['last_modified_date'];
                result.push('announcement_id');
                return result;
            }

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
