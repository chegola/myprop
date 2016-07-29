(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountController', MyAccountController);

    MyAccountController.$inject = ['$scope', '$state', 'MyAccount', 'MyAccountSearch'];

    function MyAccountController ($scope, $state, MyAccount, MyAccountSearch) {
        var vm = this;
        
        vm.myAccounts = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            MyAccount.query(function(result) {
                vm.myAccounts = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            MyAccountSearch.query({query: vm.searchQuery}, function(result) {
                vm.myAccounts = result;
            });
        }    }
})();
