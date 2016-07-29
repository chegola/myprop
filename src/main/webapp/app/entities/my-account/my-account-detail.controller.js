(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('MyAccountDetailController', MyAccountDetailController);

    MyAccountDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'MyAccount', 'User'];

    function MyAccountDetailController($scope, $rootScope, $stateParams, previousState, entity, MyAccount, User) {
        var vm = this;

        vm.myAccount = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mypropApp:myAccountUpdate', function(event, result) {
            vm.myAccount = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
