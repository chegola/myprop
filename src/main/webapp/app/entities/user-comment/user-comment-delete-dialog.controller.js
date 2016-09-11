(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('UserCommentDeleteController',UserCommentDeleteController);

    UserCommentDeleteController.$inject = ['$uibModalInstance', 'entity', 'UserComment'];

    function UserCommentDeleteController($uibModalInstance, entity, UserComment) {
        var vm = this;

        vm.userComment = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            UserComment.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
