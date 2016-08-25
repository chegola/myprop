(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountDialogApproveController', MyAccountDialogApproveController);

    MyAccountDialogApproveController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'MyAccount', 'User', 'Unit', 'Principal'];

    function MyAccountDialogApproveController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, MyAccount, User, Unit, Principal) {
        var vm = this;

        vm.myAccount = entity;
        vm.myAccount.approved = true;
        vm.clear = clear;
        vm.save = save;
        vm.units = Unit.query();

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

