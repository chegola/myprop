(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('UserCommentDialogController', UserCommentDialogController);

    UserCommentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'UserComment', 'Announcement', 'User'];

    function UserCommentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, UserComment, Announcement, User) {
        var vm = this;

        vm.userComment = entity;
        vm.clear = clear;
        vm.save = save;
        vm.announcements = Announcement.query();
        vm.users = User.query();

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
