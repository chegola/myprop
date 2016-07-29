(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountDeleteController',MyAccountDeleteController);

    MyAccountDeleteController.$inject = ['$uibModalInstance', 'entity', 'MyAccount'];

    function MyAccountDeleteController($uibModalInstance, entity, MyAccount) {
        var vm = this;

        vm.myAccount = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            MyAccount.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
