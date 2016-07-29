(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountDialogController', MyAccountDialogController);

    MyAccountDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'MyAccount', 'User'];

    function MyAccountDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, MyAccount, User) {
        var vm = this;

        vm.myAccount = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.myAccount.id !== null) {
                MyAccount.update(vm.myAccount, onSaveSuccess, onSaveError);
            } else {
                MyAccount.save(vm.myAccount, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mypropApp:myAccountUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
