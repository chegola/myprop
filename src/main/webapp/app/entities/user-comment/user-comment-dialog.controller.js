(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('UserCommentDialogController', UserCommentDialogController);

    UserCommentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity',
        'UserComment', 'Announcement', 'User', 'Principal'];

    function UserCommentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity,
        UserComment, Announcement, User, Principal) {
        var vm = this;

        vm.userComment = entity;
        vm.clear = clear;
        vm.save = save;
        getAnnouncement();
        getLogin();

        function getAnnouncement() {
            vm.userComment.announcement = Announcement.get({id : $stateParams.id});
            vm.announcements = Announcement.query();
        }

        function getLogin() {
             Principal.identity().then(function(account) {
                vm.userComment.user = User.get({login : account.login});
                vm.users = User.query();
             });
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.userComment.id !== null) {
                UserComment.update(vm.userComment, onSaveSuccess, onSaveError);
            } else {
                UserComment.save(vm.userComment, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mypropApp:userCommentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
