(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountController', MyAccountController);

    MyAccountController.$inject = ['$scope', '$state', 'MyAccount'];

    function MyAccountController ($scope, $state, MyAccount) {
        var vm = this;

        vm.myAccounts = [];
        vm.hasData = false;
        vm.setApproved = setApproved;

        loadAll();

        function loadAll() {
            MyAccount.query(function(result) {
                vm.myAccounts = result;
                if (vm.myAccounts.length !== 0) {
                    vm.hasData = true;
                }
            });
        }

        function setApproved (myAccount, isApproved) {
           myAccount.approved = isApproved;
           MyAccount.update(myAccount, function () {
                vm.loadAll();
                vm.clear();
            });
        }

    }
})();
