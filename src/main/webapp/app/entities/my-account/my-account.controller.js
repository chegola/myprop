(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountController', MyAccountController);

    MyAccountController.$inject = ['$scope', '$state', 'MyAccount'];

    function MyAccountController ($scope, $state, MyAccount) {
        var vm = this;
        
        vm.myAccounts = [];

        loadAll();

        function loadAll() {
            MyAccount.query(function(result) {
                vm.myAccounts = result;
            });
        }
    }
})();
