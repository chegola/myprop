(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('UnitDetailController', UnitDetailController);

    UnitDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Unit'];

    function UnitDetailController($scope, $rootScope, $stateParams, previousState, entity, Unit) {
        var vm = this;

        vm.unit = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mypropApp:unitUpdate', function(event, result) {
            vm.unit = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
