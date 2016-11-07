(function() {
    'use strict';

    angular
        .module('mypropApp')
        .controller('LineDetailController', LineDetailController);

    LineDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Line'];

    function LineDetailController($scope, $rootScope, $stateParams, previousState, entity, Line) {
        var vm = this;

        vm.line = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mypropApp:lineUpdate', function(event, result) {
            vm.line = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
