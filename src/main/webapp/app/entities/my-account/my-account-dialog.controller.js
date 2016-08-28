(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountDialogController', MyAccountDialogController);

    MyAccountDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'MyAccount', 'User', 'Unit', 'Principal'];

    function MyAccountDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, MyAccount, User, Unit, Principal) {
        var vm = this;

        vm.myAccount = entity;
        vm.clear = clear;
        vm.save = save;
        vm.units = Unit.query();
        vm.user = null;
        vm.users = null;
        getLogin();

        if (!vm.myAccount.name_surname) {
            getAccount();
        }
        vm.account = null;

        function getLogin() {
         Principal.identity().then(function(account) {
            vm.user = account.login;
            vm.myAccount.user = User.get({login : vm.user});
            vm.users = User.query();
         });
        }

        function getAccount() {
            Principal.identity().then(function(account) {
                var separator = "";
                if (!account.firstName) {
                    account.firstName = ""
                } else {
                    separator = "  "; // a space between name and surname
                }

                if (!account.lastName) {
                    account.lastName = ""
                }
                vm.myAccount.name_surname = account.firstName + separator + account.lastName;
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
